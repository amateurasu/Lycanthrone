package vn.elite.fundamental.design.pattern.creational.factory._abstract;

import vn.elite.fundamental.design.pattern.creational.factory.shape.Rectangle;
import vn.elite.fundamental.design.pattern.creational.factory.shape.Shape;
import vn.elite.fundamental.design.pattern.creational.factory.shape.Square;

public class ShapeFactory extends AbstractFactory {
    @Override
    public Shape getShape(String shapeType) {
        if (shapeType.equalsIgnoreCase("RECTANGLE")) {
            return new Rectangle();
        } else if (shapeType.equalsIgnoreCase("SQUARE")) {
            return new Square();
        }
        return null;
    }
}
