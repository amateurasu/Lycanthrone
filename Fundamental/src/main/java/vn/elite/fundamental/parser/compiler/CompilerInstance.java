package vn.elite.fundamental.parser.compiler;

import javax.lang.model.SourceVersion;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class CompilerInstance {
    public static void main(String[] args) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        for (final SourceVersion version : compiler.getSourceVersions()) {
            System.out.println(version);
        }
    }
}
