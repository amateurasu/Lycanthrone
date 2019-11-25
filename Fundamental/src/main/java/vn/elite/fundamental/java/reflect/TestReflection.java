package vn.elite.fundamental.java.reflect;

import java.lang.reflect.Field;

public class TestReflection {
    public int dasdasdas;
    protected int dasdasdas3;
    private int dasdasdas2;

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println(new Person().toString());
        System.out.println(new Person2().toString());

        Package _package = TestReflection.class.getPackage();
        Class clazz = Class.forName(_package.getName() + ".Person");
        for (Field f : clazz.getDeclaredFields()) {
            System.out.println(f.getName());
        }
        System.out.println("=====================");
        clazz = Class.forName(_package.getName() + ".TestReflection$Person2");
        System.out.println(clazz.getCanonicalName());
        for (Field f : clazz.getDeclaredFields()) {
            System.out.println(f.getName());
        }
    }

    static class Person2 {
        private String firstName;
        private String lastName;
        private int age;
        //accessor methods

        @Override
        public String toString() {
            return this.getClass().getCanonicalName() + " " + Person2.class.getName();
        }
    }
}

class Person {
    private String firstName;
    private String lastName;
    private int age;
    //accessor methods

    @Override
    public String toString() {
        return this.getClass().getCanonicalName();
    }
}
