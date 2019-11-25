package vn.elite.fundamental.java.reflect;

import java.lang.reflect.Field;

public class ReflectUtils {

    private ReflectUtils() {
    }

    @SuppressWarnings("rawtypes")
    public static Object getValueOf(Object clazz, String lookingForValue) throws Exception {
        Field field = clazz.getClass().getField(lookingForValue);
        Class clazzType = field.getType();
        if (clazzType.toString().equals("double")) {
            return field.getDouble(clazz);
        } else if (clazzType.toString().equals("int")) {
            return field.getInt(clazz);
        }
        return field.get(clazz);
    }

    public static void main(String[] args) throws Exception {
        TestClass test = new TestClass();
        System.out.println(ReflectUtils.getValueOf(test, "firstValue"));
        System.out.println(ReflectUtils.getValueOf(test, "secondValue"));
        System.out.println(ReflectUtils.getValueOf(test, "thirdValue"));
    }
}

class TestClass {
    public double firstValue = 3.1416;
    public int secondValue = 42;
    public String thirdValue = "Hello world";
}
