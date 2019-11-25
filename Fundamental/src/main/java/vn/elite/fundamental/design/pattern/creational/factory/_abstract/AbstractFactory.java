package vn.elite.fundamental.design.pattern.creational.factory._abstract;

import vn.elite.fundamental.design.pattern.creational.factory.shape.Shape;

public abstract class AbstractFactory {
    abstract Shape getShape(String shapeType);
}