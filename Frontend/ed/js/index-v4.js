'use strict';
((d, w, core) => {
    //region FUNCTIONAL
    const compose = (...fns) => fns.reduce((f, g) => x => f(g(x)));
    const pipe = (...fns) => fns.reduce((f, g) => (x) => g(f(x)));
    //endregion FUNCTIONAL

    //region DECLARE
    const btn = {
        clear: $("#clear"),
        go: $("#go"),
        copy: $("#copy")
    };
    const nav = {
        div: $("#side-nav"),
        open: $(".open-nav"),
        close: $(".close-nav"),
        overlay: $(".overlay")};
    const notification = $("#notify");

    const standards = core.standards();
    const sync = {left: !1, right: !1};
    const source = {
        prefix: "en_",
        list: standards,
        div: $("#encrypt"),
        txt: $('#input'),
        id: "detect",
        autoDetect: !0
    };
    const target = {
        prefix: "de_",
        list: standards,
        div: $("#decrypt"),
        txt: $('#output'),
        id: "txt"
    };
    const drop = {
        div: $("#drop-ctn"),
        all: $("#all-crypt"),
        recent: $("#recent-crypt"),
        close: $("#drop-close"),
        search: $("#search"),
        ed: "",
        init: !1,
        show: !1
    };
    //endregion declare

    // region translating
    const process = () => {
        const result = convert(source.txt.val().trim());
        resetCrypt(source, result.id);
        resetCrypt(target, target.id);
        checkOutput(result.txt);
    };

    const convert = input => {
        if (!source.autoDetect) {
            return {id: source.id, txt: core.decode(core[source.id], input)};
        }
        // auto detect - detect by order
        const list = core.decodeOrder();
        for (let i of list) {
            const txt = core.decode(core[i], input);
            if (txt !== null) return {id: core[i].id, txt};
        }
    };

    const checkOutput = out => {
        if (out === "") {
            hide(btn.copy, notification)
        } else if (out) {
            // out = core.encode(core[target.id], out);
            const nospace = out.replace(/\s/g, "");
            show(btn.copy, btn.clear);
            hide(notification);
            if (core.uri.validUrl(nospace)) {
                // out = nospace;
                btn.go.removeClass("hidden")
            } else {
                hide(btn.go)
            }
        } else {
            hide(btn.copy);
            notify("<strong>Error!</strong> Data was not properly encrypted!");
        }
        target.txt.html(out);
    };

    const populate = ed => {
        ed.div.html("").append($("<a>", {id: `${ed.prefix}choose`, "class": `choose-crypt a-icon a-down`})
            .on("click", showCryptTable));

        if (ed === source) {
            ed.div.append($("<a>", {id: "en_detect", "class": "active crypt_detect"})
                .text("AUTO DETECT").on("click", changeDetect));
        }
        ed.list.forEach(id => {
            const element = $("<a>", {id: `${ed.prefix}id`, "class": `crypt_${id}`})
                .text(core[id].name.toUpperCase()).on("click", changeCrypt);
            ed.div.append(element)
        });
        $(`#${ed.prefix}${ed.id}`).addClass("active")
    };

    const isOverflow = e => {
        return e.prop("scrollWidth") > e.prop("clientWidth") || e.prop("scrollHeight") > e.prop("clientHeight");
    };

    const resetCrypt = (ed, id) => {
        id = id || ed.id;
        const idx = ed.list.indexOf(id);
        const overflow = isOverflow(ed.div);
        if (overflow) {
            (idx < 0) ? ed.list.pop() : ed.list.splice(idx, 1);
            ed.list.unshift(id);
            populate(ed);
        } else if (idx < 0) {
            ed.list.pop();
            ed.list.unshift(id);
            populate(ed);
        }

        ed.div.find(".active").removeClass("active");
        $(`#${ed.prefix}${id}`).addClass("active");

        if (ed === source) $("#en_detect").css({"color": source.autoDetect ? "#438dff" : "#666666"});
        localStorage.setItem(`${ed.prefix}list`, JSON.stringify(ed.list));
    };
    //endregion translating

    //region EVENT HANDLING
    const clear = () => {
        source.txt.val("");
        target.txt.val("");
        hide(btn.go, btn.clear, btn.copy);
    };
    source.txt.on("dblclick", clear).on("input propertychange paste", e => {
        process(e);
    }).on("scroll", () => {
        if (!sync.left) {
            sync.right = !0;
            target.txt.scrollTop(source.txt[0].scrollTop);
        }
        sync.left = !1;
    });

    target.txt.on("scroll", () => {
        if (!sync.right) {
            sync.left = !0;
            source.txt.scrollTop(target.txt[0].scrollTop);
        }
        sync.right = !1;
    });

    const changeDetect = e => {
        event(e);
        if ($(w).width() < 768) return showCryptTable(e);
        source.autoDetect = !0;
        source.div.find(".active").removeClass("active");
        $("#en_detect").addClass("active").css({"color": "#2572EB"});
        process()
    };

    const changeCrypt = e => {
        event(e);
        if ($(w).width() < 768) return showCryptTable(e);
        let [ed, id] = e.target.id.split("_");
        if (ed === "en") {
            ed = source;
            source.autoDetect = !1;
            $("#en_detect").css({"color": "#666666"});
        } else {ed = target;}
        ed["id"] = id;
        process()
    };

    //region Drop Event
    const showCryptTable = e => {
        event(e);

        if (!drop.init) {
            initializeDrop(drop.all, core.decodeOrder().sort());
            drop.init = !0;
        }
        drop.ed = e.target.id.substr(0, 2);
        initializeDrop(drop.recent, ((drop.ed === "en") ? source : target).list);

        if (!drop.show) {
            drop.show = !0;
            show(drop.div);
        } else {hideDrop()}
        drop.search.focus()
    };

    drop.search.on("click", e => event(e)).on("keyup", () => {
        let key = drop.search.val().toUpperCase();
        filter(drop.all.children(), key);
        filter(drop.recent.children(), key);
    });

    const filter = (list, key) => {
        list.each(li => {
            let hide = key && !list[li].innerText.toUpperCase().includes(key);
            $(list[li]).css({"display": (hide ? "none" : "block")})
        });
    };

    const initializeDrop = (dom, list) => {
        dom.html("");
        list.forEach(e => dom.append($(`<li>`, {id: `choice_${e}`, "class": "choice col-m-4 col-3"})
            .text(core[e].name).on("click", makeChoiceCrypt)));
    };

    const makeChoiceCrypt = e => resetCrypt(drop.ed === "en" ? source : target, e.target.id.substr(7));

    const hideDrop = () => {
        filter(drop.all.children());
        filter(drop.recent.children());
        hide(drop.div);
        drop.show = !1;
    };
    drop.close.on("click", e => hideDrop());
    $(w).on("click", e => {if (drop.show) hideDrop()});
    //endregion Drop Event

    $("#swap").on("click", e => {
        const s = source.id === "detect" ? "txt" : source.id;
        source.id = target.id;
        source.autoDetect = !1;
        target.id = s;
        source.txt.val(target.txt.text());
        process();
    });

    btn.copy.on("click", () => {
        target.txt.select();
        document.execCommand('copy');
    });

    btn.clear.on("click", clear);

    nav.open.on("click", e => {
        event(e);
        nav.div.addClass("nav-show");
        show(nav.overlay);
        $("#container").addClass("off-canvas");
    });

    nav.close.on("click", e => {
        event(e);
        nav.div.removeClass("nav-show");
        hide(nav.overlay);
        $("#container").removeClass("off-canvas");
    });

    btn.go.on("click", e => {
        event(e);
        let link = target.txt.val();
        if (!link.startsWith("http")) link = "http://" + link;
        w.open(link, '_blank')
    });

    $(".btn-close").on("click", e => {
        event(e);
        hide(notification);
    });

    const event = e => {
        e.stopPropagation();
        e.preventDefault();
    };

    const hide = (...ele) => ele.forEach(e => e.addClass("hidden"));
    const show = (...ele) => ele.forEach(e => e.removeClass("hidden"));

    const notify = msg => {
        show(notification);
        $("#msg-content").html(msg);
    };
    //endregion event handling

    //region INITIALIZATION
    if (w.File && w.FileReader && w.FileList && w.Blob) {
        const div_inp = $(".div-input");
        div_inp.on("dragenter", e => {
            event(e);
            div_inp.css({"border": "2px dashed blue"});
            e.originalEvent.dataTransfer.dropEffect = "copy";
        }).on("dragleave dragend mouseout drop", e => {
            event(e);
            div_inp.css({"border": "none"});
            try {
                const files = e.originalEvent.dataTransfer.files;
                const reader = new FileReader();
                reader.onload = evt => {
                    source.txt.val(evt.target.result);
                    process();
                };
                reader.readAsText(files[0], "UTF-8");
            } catch (e) {}
        });
    } else {
        alert('The File APIs are not fully supported in this browser.');
    }

    if (typeof Storage !== "undefined") {
        source.list = JSON.parse(localStorage.getItem("en_list")) || standards;
        target.list = JSON.parse(localStorage.getItem("de_list")) || standards;
        source.autoDetect = localStorage.getItem("autoDetect") || source.autoDetect;
    }

    populate(source);
    populate(target);

    if (source.txt.val()) show(btn.clear);

    const getParam = () => {
        const param = document.location.search;
        return !param ? null : param.substr(1).split('&').map(k => k.split('=')).reduce((m, o) => {
            if (o[0] && o[1]) m[core.uri.de(o[0])] = core.uri.de(o[1]);
            return m;
        }, {});
    };
    const params = getParam();
    if (params && params["q"]) {
        source.txt.val(params["q"]);
        process();
    }
    target.div.scrollbar();

    //endregion INITIALIZATION
})(document, window, Core());
