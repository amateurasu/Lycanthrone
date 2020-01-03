package vn.elite.fundamental.java.reflect;

import lombok.val;
import vn.elite.core.reflect.annotation.Action;
import vn.elite.core.reflect.annotation.Controller;
import vn.elite.core.reflect.annotation.PublicFinal;

@Controller
public class AptController {

    @PublicFinal
    public final static int ABC = 100;

    @PublicFinal
    // private static String MODULE_NAME = "APT";
    public static final String MODULE_NAME = "APT";

    @Action
    public String exit() {
        return null;
    }

    @Action
    // public void print() {
    public String print() {
        System.out.println(AptController.class.getCanonicalName() + "#print");
        return null;
    }

    @Action
    // public int error() {
    public String error() {
        return null;
    }

    public static void main(String[] args) {
        val test = new AptController();
        test.print();
        test.error();
        test.exit();
        System.out.println(MODULE_NAME);
    }
}
