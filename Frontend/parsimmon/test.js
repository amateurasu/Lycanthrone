let times = (n, f) => {
    for (let i = 0; i < n; i++) f(i);
};

const forEach = (arr, f) => times(arr.length, i => f(arr[i], i, arr));

forEach([1, 2, 3, 4, 5, 6], i => console.log(i));
