package vn.elite.fundamental.gui.image;

import lombok.val;

import java.io.IOException;
import java.net.URLConnection;

public class ImageTest {
    public static void main(String[] args) throws IOException {
        foo("Sunflower-fake.gif");
        foo("Sunflower.png");
        foo("Sunflower.jpg");
        foo("Doc1.pdf");
    }

    static void foo(String name) throws IOException {
        val jpg = ImageTest.class.getResourceAsStream(name);
        jpg.read();
        String mimeType = URLConnection.guessContentTypeFromStream(jpg);
        System.out.println(mimeType);
    }
}
