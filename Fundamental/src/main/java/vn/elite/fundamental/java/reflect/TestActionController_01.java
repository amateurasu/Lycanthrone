package vn.elite.fundamental.java.reflect;

import vn.elite.core.reflect.annotation.Action;
import vn.elite.core.reflect.annotation.Controller;

@Controller
public class TestActionController_01 {

    @Action
    public String exit() {
        return null;
    }

    @Action
    public void print() {
        System.out.println(TestActionController_01.class.getCanonicalName() + "#print");
    }

    @Action
    public int error() {
        return 0;
    }

    public static void main(String[] args) {
        new TestActionController_01().print();
    }
}
