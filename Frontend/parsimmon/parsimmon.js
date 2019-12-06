"use strict";

function Parsimmon(action) {
    if (!(this instanceof Parsimmon)) {
        return new Parsimmon(action);
    }
    this._ = action;
}

const _ = Parsimmon.prototype;

const index = Parsimmon((input, i) => makeSuccess(i, makeLineColumnIndex(input, i)));

let times = (n, f) => {
    for (let i = 0; i < n; i++) f(i);
};

const forEach = (arr, f) => times(arr.length, i => f(arr[i], i, arr));

const reduce = (arr, seed, f) => {
    forEach(arr, (elem, i, arr) => seed = f(seed, elem, i, arr));
    return seed;
};

let map = (f, arr) => reduce(
    arr, [], (acc, elem, i, a) => acc.concat([f(elem, i, a)])
);

const lshiftBuffer = input => {
    const asTwoBytes = reduce(
        input, [], (a, v, i, b) => a.concat(
            i === b.length - 1
                ? Buffer.from([v, 0]).readUInt16BE(0)
                : b.readUInt16BE(i)
        )
    );
    return Buffer.from(map(x => ((x << 1) & 0xffff) >> 8, asTwoBytes));
};

const bitPeekBuffer = input => input[0] >> 7;

const consumeBitsFromBuffer = (n, input) => {
    let state = {v: 0, buf: input};
    times(n, () => {
        state = {
            v: (state.v << 1) | bitPeekBuffer(state.buf),
            buf: lshiftBuffer(state.buf)
        };
    });
    return state;
};

const sum = arr => reduce(arr, 0, (x, y) => x + y);

const find = (pred, arr) => reduce(
    arr, null, (found, elem) => found || (pred(elem) ? elem : found)
);

const bufferExists = () => typeof Buffer !== "undefined";

const ensureBuffer = () => {
    if (!bufferExists()) {
        throw new Error("Buffer global does not exist; please use webpack if you need to parse Buffers in the browser.");
    }
};

const bitSeq = alignments => {
    ensureBuffer();
    const totalBits = sum(alignments);
    if (totalBits % 8 !== 0) {
        throw new Error(`The bits [${alignments.join(", ")}] add up to ${totalBits} which is not an even number of bytes;
             the total should be divisible by 8`);
    }
    const bytes = totalBits / 8;

    const tooBigRange = find(x => x > 48, alignments);
    if (tooBigRange) {
        throw new Error(`${tooBigRange} bit range requested exceeds 48 bit (6 byte) Number max.`);
    }

    return new Parsimmon((input, i) => {
        const newPos = bytes + i;
        if (newPos > input.length) {
            return makeFailure(i, `${bytes.toString()} bytes`);
        }
        return makeSuccess(
            newPos,
            reduce(
                (acc, bits) => {
                    const state = consumeBitsFromBuffer(bits, acc.buf);
                    return {
                        coll: acc.coll.concat(state.v),
                        buf: state.buf
                    };
                },
                {coll: [], buf: input.slice(i, newPos)},
                alignments
            ).coll
        );
    });
};

function bitSeqObj(namedAlignments) {
    ensureBuffer();
    const seenKeys = {};
    let totalKeys = 0;
    const fullAlignments = map(item => {
        if (isArray(item)) {
            const pair = item;
            if (pair.length !== 2) {
                throw new Error(`[${pair.join(", ")}] should be length 2, got length ${pair.length}`);
            }
            assertString(pair[0]);
            assertNumber(pair[1]);
            if (Object.prototype.hasOwnProperty.call(seenKeys, pair[0])) {
                throw new Error(`duplicate key in bitSeqObj: ${pair[0]}`);
            }
            seenKeys[pair[0]] = true;
            totalKeys++;
            return pair;
        } else {
            assertNumber(item);
            return [null, item];
        }
    }, namedAlignments);
    if (totalKeys < 1) {
        throw new Error(`bitSeqObj expects at least one named pair, got [${namedAlignments.join(", ")}]`);
    }
    const namesOnly = map(pair => pair[0], fullAlignments);
    const alignmentsOnly = map(pair => pair[1], fullAlignments);

    return bitSeq(alignmentsOnly).map(parsed => {
        const namedParsed = map((name, i) => [name, parsed[i]], namesOnly);

        return reduce(namedParsed, {}, (obj, kv) => {
            if (kv[0] !== null) {
                obj[kv[0]] = kv[1];
            }
            return obj;
        });
    });
}

function parseBufferFor(other, length) {
    return new Parsimmon((input, i) => {
        ensureBuffer();
        return i + length > input.length
            ? makeFailure(i, `${length} bytes for ${other}`)
            : makeSuccess(i + length, input.slice(i, i + length));
    });
}

const parseBuffer = length => parseBufferFor("buffer", length).map(unsafe => Buffer.from(unsafe));

const encodedString = (encoding, length) => parseBufferFor("string", length).map(buff => buff.toString(encoding));

const isInteger = value => typeof value === "number" && Math.floor(value) === value;

const assertValidIntegerByteLengthFor = (who, length) => {
    if (!isInteger(length) || length < 0 || length > 6) {
        throw new Error(`${who} requires integer length in range [0, 6].`);
    }
};

const uintBE = length => {
    assertValidIntegerByteLengthFor("uintBE", length);
    return parseBufferFor(`uintBE(${length})`, length).map(buff => buff.readUIntBE(0, length));
};

const uintLE = length => {
    assertValidIntegerByteLengthFor("uintLE", length);
    return parseBufferFor(`uintLE(${length})`, length).map(buff => buff.readUIntLE(0, length));
};

const intBE = length => {
    assertValidIntegerByteLengthFor("intBE", length);
    return parseBufferFor(`intBE(${length})`, length).map(buff => buff.readIntBE(0, length));
};

const intLE = length => {
    assertValidIntegerByteLengthFor("intLE", length);
    return parseBufferFor(`intLE(${length})`, length).map(buff => buff.readIntLE(0, length));
};

const floatBE = () => parseBufferFor("floatBE", 4).map(buff => buff.readFloatBE(0));

const floatLE = () => parseBufferFor("floatLE", 4).map(buff => buff.readFloatLE(0));

const doubleBE = () => parseBufferFor("doubleBE", 8).map(buff => buff.readDoubleBE(0));

const doubleLE = () => parseBufferFor("doubleLE", 8).map(buff => buff.readDoubleLE(0));

const toArray = arrLike => Array.prototype.slice.call(arrLike);

// -*- Helpers -*-
const isParser = obj => obj instanceof Parsimmon;

function isArray(x) {
    return {}.toString.call(x) === "[object Array]";
}

const isBuffer = x => bufferExists() && Buffer.isBuffer(x);

function makeSuccess(index, value) {
    return {
        status: true,
        index: index,
        value: value,
        furthest: -1,
        expected: []
    };
}

function makeFailure(index, expected) {
    if (!isArray(expected)) {
        expected = [expected];
    }
    return {
        status: false,
        index: -1,
        value: null,
        furthest: index,
        expected: expected
    };
}

function mergeReplies(result, last) {
    if (!last) {
        return result;
    }
    if (result.furthest > last.furthest) {
        return result;
    }
    const expected =
        result.furthest === last.furthest
            ? union(result.expected, last.expected)
            : last.expected;
    return {
        status: result.status,
        index: result.index,
        value: result.value,
        furthest: last.furthest,
        expected: expected
    };
}

function makeLineColumnIndex(input, i) {
    if (isBuffer(input)) {
        return {
            offset: i,
            line: -1,
            column: -1
        };
    }
    const lines = input.slice(0, i).split("\n");
    // Note that unlike the character offset, the line and column offsets are
    // 1-based.
    const lineWeAreUpTo = lines.length;
    const columnWeAreUpTo = lines[lines.length - 1].length + 1;
    return {
        offset: i,
        line: lineWeAreUpTo,
        column: columnWeAreUpTo
    };
}

// Returns the sorted set union of two arrays of strings
function union(xs, ys) {
    const obj = {};
    for (let i = 0; i < xs.length; i++) {
        obj[xs[i]] = true;
    }
    for (let j = 0; j < ys.length; j++) {
        obj[ys[j]] = true;
    }
    const keys = [];
    for (let k in obj) {
        if ({}.hasOwnProperty.call(obj, k)) {
            keys.push(k);
        }
    }
    keys.sort();
    return keys;
}

function assertParser(p) {
    if (!isParser(p)) {
        throw new Error(`not a parser: ${p}`);
    }
}

function get(input, i) {
    if (typeof input === "string") {
        return input.charAt(i);
    }
    return input[i];
}

// TODO[ES5]: Switch to Array.isArray eventually.
function assertArray(x) {
    if (!isArray(x)) {
        throw new Error(`not an array: ${x}`);
    }
}

function assertNumber(x) {
    if (typeof x !== "number") {
        throw new Error(`not a number: ${x}`);
    }
}

function assertRegexp(x) {
    if (!(x instanceof RegExp)) {
        throw new Error(`not a regexp: ${x}`);
    }
    const f = flags(x);
    for (let i = 0; i < f.length; i++) {
        const c = f.charAt(i);
        // Only allow regexp flags [imu] for now, since [g] and [y] specifically
        // mess up Parsimmon. If more non-stateful regexp flags are added in the
        // future, this will need to be revisited.
        if (c !== "i" && c !== "m" && c !== "u") {
            throw new Error(`unsupported regexp flag "${c}": ${x}`);
        }
    }
}

function assertFunction(x) {
    if (typeof x !== "function") {
        throw new Error(`not a function: ${x}`);
    }
}

function assertString(x) {
    if (typeof x !== "string") {
        throw new Error(`not a string: ${x}`);
    }
}

// -*- Error Formatting -*-

const linesBeforeStringError = 2;
const linesAfterStringError = 3;
const bytesPerLine = 8;
const bytesBefore = bytesPerLine * 5;
const bytesAfter = bytesPerLine * 4;
const defaultLinePrefix = "  ";

function repeat(string, amount) {
    return new Array(amount + 1).join(string);
}

function formatExpected(expected) {
    if (expected.length === 1) {
        return `Expected:

${expected[0]}`;
    }
    return `Expected one of the following: 

${expected.join(", ")}`;
}

function leftPad(str, pad, char) {
    const add = pad - str.length;
    if (add <= 0) {
        return str;
    }
    return repeat(char, add) + str;
}

function toChunks(arr, chunkSize) {
    const length = arr.length;
    const chunks = [];
    let chunkIndex = 0;

    if (length <= chunkSize) {
        return [arr.slice()];
    }

    for (let i = 0; i < length; i++) {
        if (!chunks[chunkIndex]) {
            chunks.push([]);
        }

        chunks[chunkIndex].push(arr[i]);

        if ((i + 1) % chunkSize === 0) {
            chunkIndex++;
        }
    }

    return chunks;
}

// Get a range of indexes including `i`-th element and `before` and `after` amount of elements from `arr`.
function rangeFromIndexAndOffsets(i, before, after, length) {
    return {
        // Guard against the negative upper bound for lines included in the output.
        from: i - before > 0 ? i - before : 0,
        to: i + after > length ? length : i + after
    };
}

function byteRangeToRange(byteRange) {
    // Exception for inputs smaller than `bytesPerLine`
    if (byteRange.from === 0 && byteRange.to === 1) {
        return {
            from: byteRange.from,
            to: byteRange.to
        };
    }

    return {
        from: byteRange.from / bytesPerLine,
        // Round `to`, so we don't get float if the amount of bytes is not divisible by `bytesPerLine`
        to: Math.floor(byteRange.to / bytesPerLine)
    };
}

function formatGot(input, error) {
    const index = error.index;
    const i = index.offset;

    let verticalMarkerLength = 1;
    let column;
    let lineWithErrorIndex;
    let lines;
    let lineRange;
    let lastLineNumberLabelLength;

    if (i === input.length) {
        return "Got the end of the input";
    }

    if (isBuffer(input)) {
        const byteLineWithErrorIndex = i - (i % bytesPerLine);
        const columnByteIndex = i - byteLineWithErrorIndex;
        const byteRange = rangeFromIndexAndOffsets(
            byteLineWithErrorIndex,
            bytesBefore,
            bytesAfter + bytesPerLine,
            input.length
        );
        const bytes = input.slice(byteRange.from, byteRange.to);
        const bytesInChunks = toChunks(bytes.toJSON().data, bytesPerLine);

        const byteLines = map(byteRow => map(byteValue => {
            // Prefix byte values with a `0` if they are shorter than 2 characters.
            return leftPad(byteValue.toString(16), 2, "0");
        }, byteRow), bytesInChunks);

        lineRange = byteRangeToRange(byteRange);
        lineWithErrorIndex = byteLineWithErrorIndex / bytesPerLine;
        column = columnByteIndex * 3;

        // Account for an extra space.
        if (columnByteIndex >= 4) {
            column += 1;
        }

        verticalMarkerLength = 2;
        lines = map(byteLine => byteLine.length <= 4
            ? byteLine.join(" ")
            : `${byteLine.slice(0, 4).join(" ")}  ${byteLine.slice(4).join(" ")}`, byteLines);
        lastLineNumberLabelLength = (
            (lineRange.to > 0 ? lineRange.to - 1 : lineRange.to) * 8
        ).toString(16).length;

        if (lastLineNumberLabelLength < 2) {
            lastLineNumberLabelLength = 2;
        }
    } else {
        const inputLines = input.split(/\r\n|[\n\r\u2028\u2029]/);
        column = index.column - 1;
        lineWithErrorIndex = index.line - 1;
        lineRange = rangeFromIndexAndOffsets(
            lineWithErrorIndex,
            linesBeforeStringError,
            linesAfterStringError,
            inputLines.length
        );

        lines = inputLines.slice(lineRange.from, lineRange.to);
        lastLineNumberLabelLength = lineRange.to.toString().length;
    }

    const lineWithErrorCurrentIndex = lineWithErrorIndex - lineRange.from;

    if (isBuffer(input)) {
        lastLineNumberLabelLength = (
            (lineRange.to > 0 ? lineRange.to - 1 : lineRange.to) * 8
        ).toString(16).length;

        if (lastLineNumberLabelLength < 2) {
            lastLineNumberLabelLength = 2;
        }
    }

    const linesWithLineNumbers = reduce(lines, [], (acc, lineSource, index) => {
            const isLineWithError = index === lineWithErrorCurrentIndex;
            const prefix = isLineWithError ? "> " : defaultLinePrefix;
            let lineNumberLabel;

            if (isBuffer(input)) {
                lineNumberLabel = leftPad(
                    ((lineRange.from + index) * 8).toString(16),
                    lastLineNumberLabelLength,
                    "0"
                );
            } else {
                lineNumberLabel = leftPad(
                    (lineRange.from + index + 1).toString(),
                    lastLineNumberLabelLength,
                    " "
                );
            }

            return [].concat(
                acc,
                [`${prefix + lineNumberLabel} | ${lineSource}`],
                isLineWithError
                    ? [
                        `${defaultLinePrefix +
                           repeat(" ", lastLineNumberLabelLength)} | ${leftPad("", column, " ")}${repeat("^", verticalMarkerLength)}`
                    ]
                    : []
            );
        }
    );

    return linesWithLineNumbers.join("\n");
}

function formatError(input, error) {
    return [
        "\n",
        `-- PARSING FAILED ${repeat("-", 50)}`,
        "\n\n",
        formatGot(input, error),
        "\n\n",
        formatExpected(error.expected),
        "\n"
    ].join("");
}

function flags(re) {
    const s = `${re}`;
    return s.slice(s.lastIndexOf("/") + 1);
}

function anchoredRegexp(re) {
    return RegExp(`^(?:${re.source})`, flags(re));
}

// -*- Combinators -*-

function seq() {
    const parsers = [].slice.call(arguments);
    const numParsers = parsers.length;
    for (var j = 0; j < numParsers; j += 1) {
        assertParser(parsers[j]);
    }
    return Parsimmon((input, i) => {
        let result;
        const accum = new Array(numParsers);
        for (let j = 0; j < numParsers; j += 1) {
            result = mergeReplies(parsers[j]._(input, i), result);
            if (!result.status) {
                return result;
            }
            accum[j] = result.value;
            i = result.index;
        }
        return mergeReplies(makeSuccess(i, accum), result);
    });
}

function seqObj() {
    const seenKeys = {};
    let totalKeys = 0;
    const parsers = toArray(arguments);
    const numParsers = parsers.length;
    for (var j = 0; j < numParsers; j += 1) {
        const p = parsers[j];
        if (isParser(p)) {
            continue;
        }
        if (isArray(p)) {
            const isWellFormed =
                p.length === 2 && typeof p[0] === "string" && isParser(p[1]);
            if (isWellFormed) {
                const key = p[0];
                if (Object.prototype.hasOwnProperty.call(seenKeys, key)) {
                    throw new Error(`seqObj: duplicate key ${key}`);
                }
                seenKeys[key] = true;
                totalKeys++;
                continue;
            }
        }
        throw new Error("seqObj arguments must be parsers or [string, parser] array pairs.");
    }
    if (totalKeys === 0) {
        throw new Error("seqObj expects at least one named parser, found zero");
    }
    return Parsimmon((input, i) => {
        let result;
        const accum = {};
        for (let j = 0; j < numParsers; j += 1) {
            let name;
            let parser;
            if (isArray(parsers[j])) {
                name = parsers[j][0];
                parser = parsers[j][1];
            } else {
                name = null;
                parser = parsers[j];
            }
            result = mergeReplies(parser._(input, i), result);
            if (!result.status) {
                return result;
            }
            if (name) {
                accum[name] = result.value;
            }
            i = result.index;
        }
        return mergeReplies(makeSuccess(i, accum), result);
    });
}

function seqMap() {
    const args = [].slice.call(arguments);
    if (args.length === 0) {
        throw new Error("seqMap needs at least one argument");
    }
    const mapper = args.pop();
    assertFunction(mapper);
    return seq.apply(null, args).map(results => mapper.apply(null, results));
}

// TODO[ES5]: Revisit this with Object.keys and .bind.
function createLanguage(parsers) {
    const language = {};
    for (let key in parsers) {
        if ({}.hasOwnProperty.call(parsers, key)) {
            (key => {
                const func = () => parsers[key](language);
                language[key] = lazy(func);
            })(key);
        }
    }
    return language;
}

function alt() {
    const parsers = [].slice.call(arguments);
    const numParsers = parsers.length;
    if (numParsers === 0) {
        return fail("zero alternates");
    }
    for (let j = 0; j < numParsers; j += 1) {
        assertParser(parsers[j]);
    }
    return Parsimmon((input, i) => {
        let result;
        for (let j = 0; j < parsers.length; j += 1) {
            result = mergeReplies(parsers[j]._(input, i), result);
            if (result.status) {
                return result;
            }
        }
        return result;
    });
}

// Argument asserted by sepBy1
let sepBy = (parser, separator) => sepBy1(parser, separator).or(succeed([]));

function sepBy1(parser, separator) {
    assertParser(parser);
    assertParser(separator);
    const pairs = separator.then(parser).many();
    return seqMap(parser, pairs, (r, rs) => [r].concat(rs));
}

const eof = Parsimmon((input, i) => i < input.length ? makeFailure(i, "EOF") : makeSuccess(i, null));
// -*- Core Parsing Methods -*-

_.parse = function (input) {
    if (typeof input !== "string" && !isBuffer(input)) {
        throw new Error(".parse must be called with a string or Buffer as its argument");
    }
    const result = this.skip(eof)._(input, 0);
    if (result.status) {
        return {
            status: true,
            value: result.value
        };
    }
    return {
        status: false,
        index: makeLineColumnIndex(input, result.furthest),
        expected: result.expected
    };
};

// -*- Other Methods -*-

_.tryParse = function (str) {
    const result = this.parse(str);
    if (result.status) {
        return result.value;
    } else {
        const msg = formatError(str, result);
        const err = new Error(msg);
        err.type = "ParsimmonError";
        err.result = result;
        throw err;
    }
};

_.assert = function (condition, errorMessage) {
    return this.chain(value => condition(value) ? succeed(value) : fail(errorMessage));
};

_.or = function (alternative) {
    return alt(this, alternative);
};

_.trim = function (parser) {
    return this.wrap(parser, parser);
};

_.wrap = function (leftParser, rightParser) {
    return seqMap(leftParser, this, rightParser, (left, middle) => middle);
};

_.thru = function (wrapper) {
    return wrapper(this);
};

_.then = function (next) {
    assertParser(next);
    return seq(this, next).map(results => results[1]);
};

_.many = function () {
    const self = this;

    return Parsimmon((input, i) => {
        const accum = [];
        let result = undefined;

        for (; ;) {
            result = mergeReplies(self._(input, i), result);
            if (result.status) {
                if (i === result.index) {
                    throw new Error(
                        "infinite loop detected in .many() parser --- calling .many() on " +
                        "a parser which can accept zero characters is usually the cause"
                    );
                }
                i = result.index;
                accum.push(result.value);
            } else {
                return mergeReplies(makeSuccess(i, accum), result);
            }
        }
    });
};

_.tieWith = function (separator) {
    assertString(separator);
    return this.map(args => {
        assertArray(args);
        if (args.length) {
            assertString(args[0]);
            let s = args[0];
            for (let i = 1; i < args.length; i++) {
                assertString(args[i]);
                s += separator + args[i];
            }
            return s;
        } else {
            return "";
        }
    });
};

_.tie = function () {
    return this.tieWith("");
};

_.times = function (min, max) {
    const self = this;
    if (arguments.length < 2) {
        max = min;
    }
    assertNumber(min);
    assertNumber(max);
    return Parsimmon((input, i) => {
        const accum = [];
        let result = undefined;
        let prevResult = undefined;
        for (var times = 0; times < min; times += 1) {
            result = self._(input, i);
            prevResult = mergeReplies(result, prevResult);
            if (result.status) {
                i = result.index;
                accum.push(result.value);
            } else {
                return prevResult;
            }
        }
        for (; times < max; times += 1) {
            result = self._(input, i);
            prevResult = mergeReplies(result, prevResult);
            if (result.status) {
                i = result.index;
                accum.push(result.value);
            } else {
                break;
            }
        }
        return mergeReplies(makeSuccess(i, accum), prevResult);
    });
};

_.result = function (res) {
    return this.map(() => res);
};

_.atMost = function (n) {
    return this.times(0, n);
};

_.atLeast = function (n) {
    return seqMap(this.times(n), this.many(), (init, rest) => init.concat(rest));
};

_.map = function (fn) {
    assertFunction(fn);
    const self = this;
    return Parsimmon((input, i) => {
        const result = self._(input, i);
        if (!result.status) {
            return result;
        }
        return mergeReplies(makeSuccess(result.index, fn(result.value)), result);
    });
};

_.contramap = function (fn) {
    assertFunction(fn);
    const self = this;
    return Parsimmon((input, i) => {
        const result = self.parse(fn(input.slice(i)));
        if (!result.status) {
            return result;
        }
        return makeSuccess(i + input.length, result.value);
    });
};

_.promap = function (f, g) {
    assertFunction(f);
    assertFunction(g);
    return this.contramap(f).map(g);
};

_.skip = function (next) {
    return seq(this, next).map(results => results[0]);
};

_.mark = function () {
    return seqMap(index, this, index, (start, value, end) => ({
        start: start,
        value: value,
        end: end
    }));
};

_.node = function (name) {
    return seqMap(index, this, index, (start, value, end) => ({
        name: name,
        value: value,
        start: start,
        end: end
    }));
};

_.sepBy = function (separator) {
    return sepBy(this, separator);
};

_.sepBy1 = function (separator) {
    return sepBy1(this, separator);
};

_.lookahead = function (x) {
    return this.skip(lookahead(x));
};

_.notFollowedBy = function (x) {
    return this.skip(notFollowedBy(x));
};

_.desc = function (expected) {
    if (!isArray(expected)) {
        expected = [expected];
    }
    const self = this;
    return Parsimmon((input, i) => {
        const reply = self._(input, i);
        if (!reply.status) {
            reply.expected = expected;
        }
        return reply;
    });
};

_.fallback = function (result) {
    return this.or(succeed(result));
};

_.ap = function (other) {
    return seqMap(other, this, (f, x) => f(x));
};

_.chain = function (f) {
    const self = this;
    return Parsimmon((input, i) => {
        const result = self._(input, i);
        if (!result.status) {
            return result;
        }
        const nextParser = f(result.value);
        return mergeReplies(nextParser._(input, result.index), result);
    });
};

// -*- Constructors -*-

function string(str) {
    assertString(str);
    const expected = `'${str}'`;
    return Parsimmon((input, i) => {
        const j = i + str.length;
        const head = input.slice(i, j);
        if (head === str) {
            return makeSuccess(j, head);
        } else {
            return makeFailure(i, expected);
        }
    });
}

function byte(b) {
    ensureBuffer();
    assertNumber(b);
    if (b > 0xff) {
        throw new Error(`Value specified to byte constructor (${b}=0x${b.toString(16)}) is larger in value than a single byte.`);
    }
    const expected = `${b > 0xf ? "0x" : "0x0"}${b.toString(16)}`;
    return Parsimmon((input, i) => {
        const head = get(input, i);
        return head === b ? makeSuccess(i + 1, head) : makeFailure(i, expected);
    });
}

function regexp(re, group) {
    assertRegexp(re);
    if (arguments.length >= 2) {
        assertNumber(group);
    } else {
        group = 0;
    }
    const anchored = anchoredRegexp(re);
    const expected = `${re}`;
    return Parsimmon((input, i) => {
        const match = anchored.exec(input.slice(i));
        if (match) {
            if (0 <= group && group <= match.length) {
                const fullMatch = match[0];
                const groupMatch = match[group];
                return makeSuccess(i + fullMatch.length, groupMatch);
            }
            const message = `valid match group (0 to ${match.length}) in ${expected}`;
            return makeFailure(i, message);
        }
        return makeFailure(i, expected);
    });
}

function succeed(value) {
    return Parsimmon((input, i) => makeSuccess(i, value));
}

function fail(expected) {
    return Parsimmon((input, i) => makeFailure(i, expected));
}

function lookahead(x) {
    if (isParser(x)) {
        return Parsimmon((input, i) => {
            const result = x._(input, i);
            result.index = i;
            result.value = "";
            return result;
        });
    } else if (typeof x === "string") {
        return lookahead(string(x));
    } else if (x instanceof RegExp) {
        return lookahead(regexp(x));
    }
    throw new Error(`not a string, regexp, or parser: ${x}`);
}

function notFollowedBy(parser) {
    assertParser(parser);
    return Parsimmon((input, i) => {
        const result = parser._(input, i);
        const text = input.slice(i, result.index);
        return result.status
            ? makeFailure(i, `not "${text}"`)
            : makeSuccess(i, null);
    });
}

function test(predicate) {
    assertFunction(predicate);
    return Parsimmon((input, i) => {
        const char = get(input, i);
        if (i < input.length && predicate(char)) {
            return makeSuccess(i + 1, char);
        } else {
            return makeFailure(i, `a character/byte matching ${predicate}`);
        }
    });
}

function oneOf(str) {
    const expected = str.split("");
    for (let idx = 0; idx < expected.length; idx++) {
        expected[idx] = `'${expected[idx]}'`;
    }
    return test(ch => str.indexOf(ch) >= 0).desc(expected);
}

function noneOf(str) {
    return test(ch => str.indexOf(ch) < 0).desc(`none of '${str}'`);
}

function custom(parsingFunction) {
    return Parsimmon(parsingFunction(makeSuccess, makeFailure));
}

// TODO[ES5]: Improve error message using JSON.stringify eventually.
function range(begin, end) {
    return test(ch => begin <= ch && ch <= end).desc(`${begin}-${end}`);
}

function takeWhile(predicate) {
    assertFunction(predicate);

    return Parsimmon((input, i) => {
        let j = i;
        while (j < input.length && predicate(get(input, j))) {
            j++;
        }
        return makeSuccess(j, input.slice(i, j));
    });
}

function lazy(desc, f) {
    if (arguments.length < 2) {
        f = desc;
        desc = undefined;
    }

    const parser = Parsimmon((input, i) => {
        parser._ = f()._;
        return parser._(input, i);
    });

    return desc ? parser.desc(desc) : parser;
}

// -*- Fantasy Land Extras -*-

let empty = () => fail("fantasy-land/empty");

_.concat = _.or;
_.empty = empty;
_.of = succeed;
_["fantasy-land/ap"] = _.ap;
_["fantasy-land/chain"] = _.chain;
_["fantasy-land/concat"] = _.concat;
_["fantasy-land/empty"] = _.empty;
_["fantasy-land/of"] = _.of;
_["fantasy-land/map"] = _.map;

const any = Parsimmon((input, i) => {
    if (i >= input.length) {
        return makeFailure(i, "any character/byte");
    }
    return makeSuccess(i + 1, get(input, i));
});

const all = Parsimmon((input, i) => makeSuccess(input.length, input.slice(i)));

const digit = regexp(/[0-9]/).desc("a digit");
const digits = regexp(/[0-9]*/).desc("optional digits");
const letter = regexp(/[a-z]/i).desc("a letter");
const letters = regexp(/[a-z]*/i).desc("optional letters");
const optWhitespace = regexp(/\s*/).desc("optional whitespace");
const whitespace = regexp(/\s+/).desc("whitespace");
const cr = string("\r");
const lf = string("\n");
const crlf = string("\r\n");
const newline = alt(crlf, lf, cr).desc("newline");
var end = alt(newline, eof);

Parsimmon.all = all;
Parsimmon.alt = alt;
Parsimmon.any = any;
Parsimmon.cr = cr;
Parsimmon.createLanguage = createLanguage;
Parsimmon.crlf = crlf;
Parsimmon.custom = custom;
Parsimmon.digit = digit;
Parsimmon.digits = digits;
Parsimmon.empty = empty;
Parsimmon.end = end;
Parsimmon.eof = eof;
Parsimmon.fail = fail;
Parsimmon.formatError = formatError;
Parsimmon.index = index;
Parsimmon.isParser = isParser;
Parsimmon.lazy = lazy;
Parsimmon.letter = letter;
Parsimmon.letters = letters;
Parsimmon.lf = lf;
Parsimmon.lookahead = lookahead;
Parsimmon.makeFailure = makeFailure;
Parsimmon.makeSuccess = makeSuccess;
Parsimmon.newline = newline;
Parsimmon.noneOf = noneOf;
Parsimmon.notFollowedBy = notFollowedBy;
Parsimmon.of = succeed;
Parsimmon.oneOf = oneOf;
Parsimmon.optWhitespace = optWhitespace;
Parsimmon.Parser = Parsimmon;
Parsimmon.range = range;
Parsimmon.regex = regexp;
Parsimmon.regexp = regexp;
Parsimmon.sepBy = sepBy;
Parsimmon.sepBy1 = sepBy1;
Parsimmon.seq = seq;
Parsimmon.seqMap = seqMap;
Parsimmon.seqObj = seqObj;
Parsimmon.string = string;
Parsimmon.succeed = succeed;
Parsimmon.takeWhile = takeWhile;
Parsimmon.test = test;
Parsimmon.whitespace = whitespace;
Parsimmon["fantasy-land/empty"] = empty;
Parsimmon["fantasy-land/of"] = succeed;

Parsimmon.Binary = {
    bitSeq: bitSeq,
    bitSeqObj: bitSeqObj,
    byte: byte,
    buffer: parseBuffer,
    encodedString: encodedString,
    uintBE: uintBE,
    uint8BE: uintBE(1),
    uint16BE: uintBE(2),
    uint32BE: uintBE(4),
    uintLE: uintLE,
    uint8LE: uintLE(1),
    uint16LE: uintLE(2),
    uint32LE: uintLE(4),
    intBE: intBE,
    int8BE: intBE(1),
    int16BE: intBE(2),
    int32BE: intBE(4),
    intLE: intLE,
    int8LE: intLE(1),
    int16LE: intLE(2),
    int32LE: intLE(4),
    floatBE: floatBE(),
    floatLE: floatLE(),
    doubleBE: doubleBE(),
    doubleLE: doubleLE()
};

module.exports = Parsimmon;
