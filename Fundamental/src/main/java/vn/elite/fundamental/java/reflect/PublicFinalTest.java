package vn.elite.fundamental.java.reflect;

import vn.elite.core.reflect.annotation.PublicFinal;

public class PublicFinalTest {

    @PublicFinal
    public final static int ABC = 100;

    @PublicFinal
    private static String MODULE_NAME = "APT";

    public static void main(String[] args) {
        System.out.println(MODULE_NAME);
    }
}
