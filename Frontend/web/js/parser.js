//region CLASSES
class Stream {
    constructor(iterable, cursor, length) {
        this.iterable = iterable;
        this.cursor = cursor || 0;
        this.length = length === undefined ? iterable.length - this.cursor : length
    }

    // Get the first value from the iterable.
    head() {
        if (this.length <= 0) throw new TypeError('Index out of range');
        return this.iterable[this.cursor]
    }

    // Consume the stream by moving the cursor.
    move(distance) {
        return new Stream(
            this.iterable,
            this.cursor + distance,
            this.length - distance
        )
    }

    // Same interface as Array.slice but returns a new Stream
    slice(start, stop) {
        if (stop < start) throw new Error('stop < start');
        if (stop && stop > this.length) throw new TypeError('index out of range');

        return new Stream(this.iterable, this.cursor + start, (stop || this.length) - start)
    }
}

class Result {
    constructor(value, rest) {
        this.value = value;
        this.rest = rest
    }
}

class Success extends Result {
    map(fn) {
        return new Success(fn(this.value), this.rest)
    }

    bimap(s, f) {
        return new Success(s(this.value), this.rest)
    }

    chain(fn) {
        return fn(this.value, this.rest)
    }

    fold(s, f) {
        return s(this.value, this.rest)
    }
}

class Failure extends Result {
    map(fn) {return this}

    bimap(s, f) {
        return new Failure(f(this.value), this.rest)
    }

    chain(fn) {
        return this
    }

    fold(s, f) {
        return f(this.value, this.rest)
    }
}

class Parser {
    constructor(parse) {
        this.parse = parse
    }

    run(iterable) {
        if (iterable instanceof Stream) {
            return this.parse(iterable)
        } else {
            return this.parse(new Stream(iterable))
        }
    }

    map(f) {
        return new Parser(stream => this.parse(stream).map(f))
    }

    bimap(s, f) {
        return new Parser(stream => this.parse(stream).bimap(s, f))
    }

    chain(f) {
        return new Parser(stream => this.parse(stream).chain((v, s) => f(v).run(s)))
    }

    fold(s, f) {
        return new Parser(stream => this.parse(stream).fold(s, f))
    }
}
//endregion CLASSES

const compose = (...fns) => fns.reduce((f, g) => x => f(g(x)));
const pipe = (...fns) => fns.reduce((f, g) => (x) => g(f(x)));

const char = c => where(x => x === c);
const sequence = list => list.reduce((acc, parser) => append(acc, parser), always([]));
const string = str => sequence(str.split('').map(char));

const where = predicate => new Parser(stream => {
    if (stream.length === 0) {
        return new Failure('unexpected end', stream)
    }
    const value = stream.head();
    if (predicate(value)) {
        return new Success(value, stream.move(1))
    }
    return new Failure('predicate did not match', stream)
});

const always = value => new Parser(stream => new Success(value, stream));

const never = value => new Parser(stream => new Failure(value, stream));

const append = (p1, p2) => p1.chain(vs => p2.map(v => vs.concat([v])));

const concat = (p1, p2) => p1.chain(xs => p2.map(ys => xs.concat(ys)));

const maybe = parser => new Parser(stream => parser.run(stream)
    .fold((v, s) => new Success(v, s), (v, s) => new Success(null, stream)));

const lookahead = parser => new Parser(stream => parser.run(stream)
    .fold(v => new Success(v, stream), v => new Failure(v, stream)));

const zeroOrMore = parser => new Parser(stream => parser.run(stream).fold(
    (value, s) => zeroOrMore(parser).map(rest => [value].concat(rest)).run(s),
    (value, s) => new Success([], stream))
);

const not = parser => new Parser(stream => parser.run(stream).fold(
    (value, s) => new Failure('NOT failed', stream),
    (value, s) => stream.length > 0
        ? new Success(stream.head(), stream.move(1))
        : new Failure('unexpected end', stream))
);

const between = (l, p, r) => sequence([l, p, r]).map(v => v[1]);

const s = "Tôi có link này <here>68747470733A2F2F7777772E796F75747562652E636F6D</here> và <here>aHR0cHM6Ly93d3cueW91dHViZS5jb20=</here>";
(between(
    compose(zeroOrMore, not, string)('<here>'),
    between(
        string('<here>'),
        compose(zeroOrMore, not, string)('</here>'),
        string('</here>')
    ),
    always()
)).run(s).fold(
    v => v.map(s => console.log(s0)),
    e => console.log('error:', e)
);
//
// zeroOrMore().run(s).
console.log(s);
