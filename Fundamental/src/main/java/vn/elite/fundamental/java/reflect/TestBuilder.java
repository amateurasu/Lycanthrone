package vn.elite.fundamental.java.reflect;

import vn.elite.core.reflect.annotation.BuilderProperty;

public class TestBuilder {

    private int age;

    private String name;

    public int getAge() {
        return age;
    }

    @BuilderProperty
    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    @BuilderProperty
    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        try {
            Class<?> builder = Class.forName(TestBuilder.class.getCanonicalName() + "Builder");
            System.out.println(builder);
            TestBuilder person = new TestBuilderBuilder().setAge(25).setName("John").build();
            System.out.println(person.age);
            System.out.println(person.name);
            System.out.println(PublicFinalTest.ABC);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
