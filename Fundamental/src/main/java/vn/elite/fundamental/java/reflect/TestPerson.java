package vn.elite.fundamental.java.reflect;

import vn.elite.core.reflect.annotation.BuilderProperty;

public class TestPerson {

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
            Class<?> builder = Class.forName(Person.class.getCanonicalName() + "Builder");
            System.out.println(builder);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Person person = new PersonBuilder().setAge(25).setName("John").build();
    }
}
