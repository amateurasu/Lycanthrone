package vn.elite.fundamental.parser.javacc;

import lombok.val;

public class DemoJavaCC {
    public static void main(String[] args) throws ParseException, InterpreterException {
        val resource = DemoJavaCC.class.getResourceAsStream("example.l");
        val compiler = new ExampleCompiler(resource);

        val unit = compiler.compile();
        unit.dump("");
        System.out.println("----------------------------------------------");
        unit.eval();
    }
}
