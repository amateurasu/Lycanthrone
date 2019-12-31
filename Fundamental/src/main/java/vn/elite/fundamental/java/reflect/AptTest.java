package vn.elite.fundamental.java.reflect;

import lombok.val;
import vn.elite.core.reflect.annotation.Action;
import vn.elite.core.reflect.annotation.Controller;
import vn.elite.core.reflect.annotation.PublicFinal;

@Controller
public class AptTest {

    @PublicFinal
    public final static int ABC = 100;

    @PublicFinal
    private static String MODULE_NAME = "APT";

    @Action
    public String exit() {
        return null;
    }

    @Action
    public void print() {
        System.out.println(AptTest.class.getCanonicalName() + "#print");
    }

    @Action
    public int error() {
        return 0;
    }

    public static void main(String[] args) {
        val test = new AptTest();
        test.print();
        test.error();
        test.exit();
        System.out.println(MODULE_NAME);
    }
}
