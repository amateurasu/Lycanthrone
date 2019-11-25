!function () {
    var x = Json.parse(document.querySelector("div[id='pagelet_timeline_main_column']").getAttribute('data-gt'));
    prompt("ID", x.profile_owner);
}();
