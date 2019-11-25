if (typeof navigator !== "undefined") {
    var Browser = {};

    var ua = navigator.userAgent.toLowerCase();
    var s;
    if (s = ua.match(/msie ([\d.]+)/)) {
        Browser.name = "MSIE";
    } else if (s = ua.match(/firefox\/([\d.]+)/)) {
        Browser.name = "Firefox";
    } else if (s = ua.match(/chrome\/([\d.]+)/)) {
        Browser.name = "Chrome";
    } else if (s = ua.match(/opera.([\d.]+)/)) {
        Browser.name = "Opera";
    } else if (s = ua.match(/version\/([\d.]+).*safari/)) {
        Browser.name = "Safari";
    }
    Browser.version = s ? s[1] : 0;

    console.log(`${Browser.name}: ${Browser.version}`);
}