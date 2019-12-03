let foo = [1, 2, 3];
let foo2 = foo;
foo = [];

let bar = [1, 2, 3];
bar2 = bar;
bar.length = 0;

console.log(`foo [${foo}] foo2 [${foo2}] bar [${bar}] bar2 [${bar2}]`);

const input = "aHR0cHM6Ly93d3cueW91dHViZS5jb20=";
console.log(Buffer.from(input, "base64").toString("utf-8"));

const s = "https://www.youtube.com";
console.log(Buffer.from(s).toString("base64"));
console.log(input);

const compose = (...fns) => fns.reduce((f, g) => x => f(g(x)));
const pipe = (...fns) => fns.reduce((f, g) => x => g(f(x)));
const pipe2 = (...fns) => fns.reduceRight((f, g) => x => g(f(x)));
const curry = (fn, ...args) => (fn.length > args.length) ? ((...more) => curry(fn, ...args, ...more)) : fn(...args);

function f4(a1, a2, a3, a4) {
    console.log(`f4 ${this.length}`);
    console.log(`${a1} ${a2} ${a3} ${a4}`);
}

const a = curry(f4);

a(1, 2, 3, 4);
a(1, 2)(3, 4);
a(1, 2, 3)(4);
a(1, 2, 3)(4);

const f1 = i => `${i} go 1`;
const f2 = i => `${i} go 2`;
const f3 = i => `${i} go 3`;

console.log("compose:", compose(f1, f2, f3)("a", "b"));
console.log("pipe:   ", pipe(f1, f2, f3)("a"));
console.log("pipe2:  ", pipe2(f1, f2, f3)("a"));

const Flock = function (n) {
    this.seagulls = n;
};

Flock.prototype = {
    conjoin: function (other) {
        return new Flock(this.seagulls + other.seagulls);
    },

    breed: function (other) {
        return new Flock(this.seagulls * other.seagulls);
    }
};

var flock_a = new Flock(4);
var flock_b = new Flock(2);
var flock_c = new Flock(0);

var result = flock_a.conjoin(flock_c).breed(flock_b).conjoin(flock_a.breed(flock_b)).seagulls;
console.log(result);
//=> 32
