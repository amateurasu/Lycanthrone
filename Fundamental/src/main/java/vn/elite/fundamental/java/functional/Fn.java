package vn.elite.fundamental.java.functional;

import java.util.function.Function;

public class Fn {
    public static void main(String[] args) {
        System.out.println(a(10, i -> i * i + ""));
    }

    static String a(int inp, Function<Integer, String> fn) {
        return fn.apply(inp);
    }
}
