// export default
class MainForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {dropdownVisible: false};
        this.core = Core();
    }

    handleToggleChoices = () => this.setState(({dropdownVisible: !this.state.dropdownVisible}));

    // handleToggleDropdown = () => {}

    handleError = (e) => this.props.onError(e);

    render() {
        return (
            <div className="ed">
                <GroupUpper core={this.core} onToggleDropdown={this.handleToggleChoices}/>
                <GroupLower core={this.core} onToggleDropdown={this.handleToggleChoices}
                    dropdownVisible={this.state.dropdownVisible}/>
            </div>
        );
    }
}

//region CHANGE MODE
class CryptList extends React.Component {
    static recentEncrypt = () => ["bin", "hex", "b64"];
    static recentDecrypt = () => ["bin", "hex", "b64"];
    static all = () => ["bin", "hex", "b64", "uri", "uni", "vie", "txt"];

    makeChoiceCrypt = e => resetCrypt(drop.ed === "en" ? source : target, e.target.id.substr(7));
}

class ModeDropdown extends React.Component {
    constructor(props) {
        super(props);
        this.allCrypt = this.props.allCrypt;
        this.state = {
            all: this.allCrypt,
            visible: this.props.visible
        };
    }

    getCrypt = crypts => {
        const {core} = this.props;
        return crypts.reduce((all, crypt) => {
            all.push(
                <li key={crypt} className="choice col-m-4 col-3" datatype={crypt} onClick={this.handleChangeMode}>
                    {core[crypt].name}
                </li>
            );
            return all;
        }, []);
    };

    handleChangeMode = e => {
        this.props.onToggleChoices(e.target.getAttribute("datatype"));
        this.setState({visible: false});
    };

    filter = e => {
        const {core} = this.props;
        const filter = e.target.value;
        const all = this.allCrypt.filter(crypt => core[crypt].name.includes(filter));
        this.setState({all});
    };

    render() {
        const {visible, recent} = this.props;
        const {all} = this.state;
        return (
            <div className={`drop-ctn ${visible ? "" : "hidden"}`}>
                <div>
                    <input type="text" title="search" className="search" placeholder="Search..." onChange={this.filter}/>
                    <i className="btn float right-center icon cross"/>
                </div>
                <div className="choices">
                    <h3>Recent Crypts</h3>
                    <ul className="group">{recent}</ul>
                    <h3>All Crypts</h3>
                    <ul className="group">{this.getCrypt(all)}</ul>
                </div>
            </div>
        );
    }
}

// endregion CHANGE MODE

class GroupUpper extends React.Component {

    handleToggleDropdown = e => this.props.onToggleDropdown(e);

    getCrypt = list => {
        const {core} = this.props;
        return list.reduce((all, crypt) => {
            all.push(<a className={`crypt_${crypt}`} key={crypt}>{core[crypt].name.toUpperCase()}</a>);
            return all;
        }, []);
    };

    render() {
        const recentEncrypt = this.getCrypt(CryptList.recentEncrypt());
        const recentDecrypt = this.getCrypt(CryptList.recentDecrypt());

        return (
            <div className="group upper">
                <div className="group crypt col-5 col-s-5">
                    <a className="en choose-crypt a-icon a-down" onClick={this.handleToggleDropdown}/>
                    <a className="active crypt_detect">AUTO DETECT</a>
                    {recentEncrypt}
                </div>
                <div className="col-2 col-s-2"><i className="icon swap"/></div>
                <div className="group crypt col-5 col-s-5">
                    <a className="choose-crypt a-icon a-down" onClick={this.handleToggleDropdown}/>
                    {recentDecrypt}
                </div>
            </div>
        );
    }
}

class GroupLower extends React.Component {
    constructor(props) {
        super(props);
        this.state = {input: "", output: "", dropdownVisible: false};
    }

    //region COPY //FIXME
    process = () => {
        const result = convert(source.txt.val().trim());
        resetCrypt(source, result.id);
        resetCrypt(target, target.id);
        checkOutput(result.txt);
    };

    convert = input => {
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

    checkOutput = out => {
        if (out === "") {
            hide(btn.copy, notification);
        } else if (out) {
            // out = core.encode(core[target.id], out);
            const nospace = out.replace(/\s/g, "");
            show(btn.copy, btn.clear);
            hide(notification);
            if (core.uri.validUrl(nospace)) {
                // out = nospace;
                btn.go.removeClass("hidden");
            } else {
                hide(btn.go);
            }
        } else {
            hide(btn.copy);
            notify("<strong>Error!</strong> Data was not properly encrypted!");
        }
        target.txt.html(out);
    };

    populate = ed => {
        ed.div.html("").append($("<a>", {
            id: `${ed.prefix}choose`, "class": `choose-crypt a-icon a-down`
        }).on("click", showCryptTable));

        if (ed === source) {
            ed.div.append($("<a>", {id: "en_detect", "class": "active crypt_detect"})
                .text("AUTO DETECT").on("click", changeDetect));
        }
        ed.list.forEach(id => {
            const element = $("<a>", {id: ed.prefix + id, "class": `crypt_${id}`})
                .text(core[id].name.toUpperCase()).on("click", changeCrypt);
            ed.div.append(element);
        });
        $(`#${ed.prefix}${ed.id}`).addClass("active");
    };

    isOverflow = e => {
        return e.prop("scrollWidth") > e.prop("clientWidth") || e.prop("scrollHeight") > e.prop("clientHeight");
    };

    resetCrypt = (ed, id) => {
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

        if (ed === source) $("#en_detect").css({color: source.autoDetect ? "#438dff" : "#666666"});
        localStorage.setItem(`${ed.prefix}list`, JSON.stringify(ed.list));
    };
    //endregion COPY

    handleInput = e => {
        const input = e.target.value;
        const output = e.target.value; // process(input);
        this.setState({input, output});
    };

    handleToggleChoices = () => {
        this.setState({dropdownVisible: !this.state.dropdownVisible});
    };

    render() {
        const {input, output} = this.state;
        const {core, dropdownVisible} = this.props;

        return (
            <div className="group under">
                <ModeDropdown allCrypt={CryptList.all()} visible={dropdownVisible} core={core}
                    onToggleChoices={this.handleToggleChoices}/>
                <Input onInputChange={this.handleInput} value={input}/>
                <Output value={output}/>
            </div>
        );
    }
}

class Input extends React.Component {
    constructor(props) {
        super(props);
        this.state = {clearVisible: false};
    }

    handleInput = e => {
        const value = e.target.value;
        this.props.onInputChange(e);
    };

    handleClear = () => {
        this.setState({clearVisible: false});
    };

    render() {
        let {value} = this.props;
        let {clearVisible} = this.state;
        return (
            <div className="div-input col-6 col-m-6">
                <textarea className="txt input" title="input" name="input" value={value} autoFocus
                    onInput={this.handleInput} onPaste={this.handleInput} onChange={this.handleInput}/>
                <i className={`btn float top-right icon cross ${clearVisible ? "" : "hidden"}`} onClick={this.handleClear}/>
            </div>
        );
    }
}

class Output extends React.Component {
    render() {
        return (
            <div className="div-output col-6 col-m-6">
                <div className="txt output wordwrap" title="output">{this.props.value}</div>
                <i id="go" className="float top-right icon right-arrow hidden"/>
                <i id="copy" className="float bottom-left icon copy hidden"/>
            </div>
        );
    }
}
