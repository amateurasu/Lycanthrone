package vn.elite.fundamental.parser.javacc;

import lombok.val;
import vn.elite.core.compiler.CC;

import java.io.InputStream;

@CC(javacc = "vn/elite/fundamental/parser/javacc/ExampleCompiler.jj")
public class DemoJavaCC {
    public static void main(String[] args) throws ParseException, InterpreterException {
        InputStream resource = DemoJavaCC.class.getResourceAsStream("example.l");
        ExampleCompiler compiler = new ExampleCompiler(resource);

        val unit = compiler.compile();
        unit.dump("");
        System.out.println("----------------------------------------------");
        unit.eval();
    }
}
