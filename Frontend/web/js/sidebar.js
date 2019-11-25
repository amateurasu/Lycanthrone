$("#listSearch").on("keyup", function () {
        var term = document.querySelector("#listSearch").value.toLowerCase();
        var tag = document.querySelectorAll(".filter__link");
        for (i = 0; i < tag.length; i++) {
            if (tag[i].innerHTML.toLowerCase().indexOf(term) !== -1) {
                $(tag[i]).show();
                $(tag[i]).parent().parent().show();
            } else {
                $(tag[i]).hide();
            }
        }
    }
);

const filter_link = $('.filter__link');
filter_link.on("click", e => {
    let t = $(e.target);
    console.log(t);
    t.siblings('.filter__tree').slideToggle(250);
    t.siblings('.filter__tree').children('.filter__tree-item').children('.filter__tree').hide();
    filter_link.removeClass('active');
    t.addClass('active');
    var clickLabel = $(e.target).closest('span').text();
    $('#supFool').text(clickLabel);
    console.log(clickLabel);
});