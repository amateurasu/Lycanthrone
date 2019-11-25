package vn.elite.fundamental.design.pattern.creational.factory;

import vn.elite.fundamental.design.pattern.creational.factory.shape.Circle;
import vn.elite.fundamental.design.pattern.creational.factory.shape.Rectangle;
import vn.elite.fundamental.design.pattern.creational.factory.shape.Shape;
import vn.elite.fundamental.design.pattern.creational.factory.shape.Square;

public class ShapeFactory {

    //use getShape method to get object of type shape
    public Shape getShape(String shapeType) {
        if (shapeType == null) {
            return null;
        }
        if (shapeType.equalsIgnoreCase("CIRCLE")) {
            return new Circle();
        } else if (shapeType.equalsIgnoreCase("RECTANGLE")) {
            return new Rectangle();
        } else if (shapeType.equalsIgnoreCase("SQUARE")) {
            return new Square();
        }

        return null;
    }
}
