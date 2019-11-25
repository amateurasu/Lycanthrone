'use strict';

EventTarget.prototype.on = function (event, handler) {
    this.addEventListener(event, handler);
    return this;
};

const _Element = Element.prototype;

_Element.html = function (html, append) {
    if (append) {this.innerHTML += html;} else {this.innerHTML = html;}
    return this;
};

_Element.css = function (styles) {
    for (const s in styles) {
        if (styles.hasOwnProperty(s)) this.style[s] = styles[s];
    }
};