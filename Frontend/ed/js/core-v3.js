const Core = () => {

    const split = (input, i) => {
        return (
                   input.search(" ") !== -1
                       ? input.split(" ")
                       : input.match(new RegExp(`.{${i}}`, "g"))
               ) || [input];
    };

    const maps = [
        ["kh", "x"], ["nh", "n'"], ["c(?!h)", "k"], ["th", "w"], ["q", "k"], ["ngh?", "q"], ["gh", "g"],
        ["ph", "f"], ["tr", "c"], ["ch", "c"], ["d", "z"], ["gi", "z"], ["r", "z"], ["đ", "d"]
    ];

    const regex_bin = /(?:[01]{1,8}\s*){4,}/g;
    const regex_b64 = /(?:[\w+\/]{4})*(?:[\w+\/]{4}|[\w+\/]{3}=|[\w\/]{2}==)/g;
    const regex_uni = /(?:(?:\\u[0-9a-fA-F]{4}|\w)?[ !'#$%&"()*+-.,\/\\:;<=>?@[\]^_`{|}~]?)*/g;
    const regex_hex = /(?:[0-9A-Fa-f]{2}\s*)+[0-9A-Fa-f]{2}/g;
    const regex_url = /((https?|ftp):\/\/)?\w+([\-.]\w+)*\.[a-z0-9]{2,5}(:[0-9]{1,5})?(\/.*)?/ig;

    const decodeURI = input => {
        try {
            return decodeURIComponent(input.replace(/\+/g, " "));
        } catch (e) {
            return null;
        }
    };

    const checkURI = input => !/\s/.test(input) && decodeURI(input) !== null;

    return {
        standards: () => ["txt", "b64", "hex"],
        decodeOrder: () => ["bin", "hex", "b64", "uri", "uni", "vie", "txt"],

        bin: {
            id: "bin",
            name: "Binary",
            regex: regex_bin,
            valid: input => regex_bin.test(input.replace(/\s/g, "")),
            de: input => {
                const arr = split(input, 8);
                let finalStr = "";
                for (let i = 0; i < arr.length; i++) {
                    finalStr += String.fromCharCode(parseInt(arr[i], 2));
                }
                return finalStr;
            },
            en: input => {
                let output = "";
                const pad = "00000000";
                for (let i = 0; i < input.length; i++) {
                    const str = input[i].charCodeAt(0).toString(2);
                    output += pad.substring(0, 8 - str.length) + str;
                }
                return output;
            }
        },

        hex: {
            id: "hex",
            name: "Hex",
            regex: regex_hex,
            valid: input => regex_hex.test(input),
            de: input => {
                const s = split(input, 2);
                let str = "";
                for (let i = 0; i < s.length; i++) {
                    str += String.fromCharCode(parseInt(s[i], 16));
                }
                const s_cape = (window && window.escape) ||
                               encodeURIComponent;
                try {
                    return decodeURIComponent(s_cape(str));
                } catch (e) {
                    return null;
                }
            },
            en: input => {
                uns_cape = unescape || decodeURIComponent;
                input = uns_cape(encodeURIComponent(input));
                let result = "";
                for (let i = 0; i < input.length; i++) {
                    result += input.charCodeAt(i).toString(16);
                }
                return result.toUpperCase();
            }
        },

        b64: {
            id: "b64",
            name: "Base 64",
            regex: regex_b64,
            valid: input => regex_b64.test(input.replace(/\s/g, "")),
            de: input => {
                let d = atob(input);
                try {
                    return decodeURIComponent(d.split("").map(c => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2)).join(""));
                } catch (e) {
                    return null;
                }
            },
            en: input => btoa(encodeURIComponent(input)
                .replace(/%([0-9A-F]{2})/g, (match, p1) => String.fromCharCode(`0x${p1}`)))
        },

        uri: {
            id: "uri",
            name: "URI",
            valid: checkURI,
            regex: regex_url,
            validUrl: input => regex_url.test(input),
            de: decodeURI,
            en: input => encodeURIComponent(input).replace(/'/g, "%27").replace(/"/g, "%22")
        },

        vie: {
            id: "vie",
            name: "New Vietnamese",
            valid: () => true,
            // "Bạn đùa à? Cái thứ này thì dịch thế đ** nào được!"
            de: () => null,
            en: input => maps.reduce((result, map) => input.replace(new RegExp(map[0], "g"), map[1])
                .replace(new RegExp(map[0].toUpperCase(), "g"), map[1].toUpperCase()), input)
        },

        uni: {
            id: "uni",
            name: "Unicode",
            valid: input => regex_uni.test(input),
            de: input => {
                let off = 0;
                const len = input.length;
                let out = "";

                while (off < len) {
                    let c = input.charAt(off++);
                    if (c !== "\\") {
                        out += c;
                        continue;
                    }
                    c = input.charAt(off++);
                    if (c === "u") {
                        let value = 0;
                        for (let i = 0; i < 4; i++) {
                            c = input.charAt(off++);
                            let code = c.charCodeAt(0);
                            switch (c) {
                                case "0":
                                case "1":
                                case "2":
                                case "3":
                                case "4":
                                case "5":
                                case "6":
                                case "7":
                                case "8":
                                case "9":
                                    value = (value << 4) + code - 48;
                                    break;
                                case "a":
                                case "b":
                                case "c":
                                case "d":
                                case "e":
                                case "f":
                                    value = (value << 4) + 10 + code - 97;
                                    break;
                                case "A":
                                case "B":
                                case "C":
                                case "D":
                                case "E":
                                case "F":
                                    value = (value << 4) + 10 + code - 65;
                                    break;
                                default:
                                    throw "Malformed \\uXXXX encoding.";
                            }
                        }
                        out += String.fromCharCode(value);
                    } else {
                        switch (c) {
                            case "t":
                                out += "\t";
                                break;
                            case "r":
                                out += "\r";
                                break;
                            case "n":
                                out += "\n";
                                break;
                            case "f":
                                out += "\f";
                                break;
                        }
                    }
                }
                return out;
            },
            en: input => {
                let sb = "";
                const pad = "0000";
                try {
                    const len = input.length;
                    for (let i = 0; i < len; i++) {
                        const code = input.charCodeAt(i);
                        const char = input.charAt(i);
                        if (code > 61 && code < 127) {
                            if (code === "\\") {
                                sb += "\\\\";
                                continue;
                            }
                            sb += char;
                            continue;
                        }
                        switch (code) {
                            case " ":
                                sb += " ";
                                break;
                            case "\t":
                                sb += "\\t";
                                break;
                            case "\n":
                                sb += "\\n";
                                break;
                            case "\r":
                                sb += "\\r";
                                break;
                            case "\f":
                                sb += "\\f";
                                break;
                            case "=":
                            case ":":
                            case "#":
                            case "!":
                                sb += "\\" + char;
                                break;
                            default:
                                if (code < 0x0020 || code > 0x007e) {
                                    const s = code.toString(16).toUpperCase();
                                    sb += "\\u" + pad.substr(0, 4 - s.length) + s;
                                } else {
                                    sb += char;
                                }
                        }
                    }
                } catch (e) {}
                return sb;
            }
        },

        txt: {
            id: "txt",
            name: "Text",
            valid: () => true,
            de: input => input,
            en: input => input
        },

        decode: (core, input) => {
            if (!core.valid(input = input.trim())) return null;

            return input.replace(core.regex, match => {
                const result = core.de(match);
                if (checkURI(result)) {
                    const link = decodeURI(result);
                    const url = new URL(link);
                    return `<a href="${link}" target="_blank">${url.hostname}</a>`;
                } else {return result;}
            });

        },

        encode: (core, input) => core.en(input.trim())
    };
};

