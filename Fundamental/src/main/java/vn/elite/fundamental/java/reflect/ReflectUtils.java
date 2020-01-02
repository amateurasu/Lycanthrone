package vn.elite.fundamental.java.reflect;

import java.lang.reflect.Field;

public class ReflectUtils {

    private ReflectUtils() {
    }

    @SuppressWarnings("rawtypes")
    public static Object getValueOf(Object instance, String lookingForValue) throws Exception {
        Field field = instance.getClass().getDeclaredField(lookingForValue);
        if (!field.isAccessible()) {
            System.out.println();
            field.setAccessible(true);
        }
        Class type = field.getType();
        System.out.println(type);
        switch (type.toString()) {
            case "double":
                return field.getDouble(instance);
            case "int":
                return field.getInt(instance);
            default:
                return field.get(instance);
        }
    }

    public static void main(String[] args) throws Exception {
        TestClass test = new TestClass();
        System.out.println(ReflectUtils.getValueOf(test, "firstValue"));
        System.out.println(ReflectUtils.getValueOf(test, "secondValue"));
        System.out.println(ReflectUtils.getValueOf(test, "thirdValue"));
    }

    static class TestClass {
        public double firstValue = 3.1416;
        private int secondValue = 42;
        public String thirdValue = "Hello world";
    }
}
