let times = (n, f) => {
    for (let i = 0; i < n; i++) f(i);
};

const forEach = (arr, f) => times(arr.length, i => f(arr[i], i, arr));

const reduce = (arr, seed, f) => {
    forEach(arr, (elem, i, arr) => seed = f(seed, elem, i, arr));
    return seed;
};

const array = [1, 2, 3, 4, 5, 6];
forEach(array, i => console.log(i));
const reduce1 = reduce(array, 0, (e, i, a) => e + i);
console.log(reduce1);

