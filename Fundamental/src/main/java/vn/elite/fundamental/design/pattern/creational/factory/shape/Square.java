package vn.elite.fundamental.design.pattern.creational.factory.shape;

public class Square implements Shape {

    @Override
    public void draw() {
        System.out.println("Inside Square::draw() method.");
    }
}