package vn.elite.snatcher;

import java.io.File;
import java.util.Arrays;

public class HCafe {
    public static void main(String[] args) {
        String[] files = new File("E:\\Pictures\\697d0f2554859c3e95577f0d9e9373e7").list();
        System.out.println(Arrays.toString(files));
    }
}
