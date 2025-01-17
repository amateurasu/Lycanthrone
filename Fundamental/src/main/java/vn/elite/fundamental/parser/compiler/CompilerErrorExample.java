package vn.elite.fundamental.parser.compiler;

import lombok.val;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class CompilerErrorExample {
    public static void main(String[] args) throws IOException {
        val compiler = ToolProvider.getSystemJavaCompiler();
        val diagnostics = new DiagnosticCollector<JavaFileObject>();

        try (val manager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
            String path = CompilerErrorExample.class.getResource("SampleClassWithErrors.java").getPath();
            val sources = manager.getJavaFileObjectsFromFiles(Collections.singletonList(new File(path)));

            val task = compiler.getTask(null, manager, diagnostics, null, null, sources);
            task.call();
        }

        for (val diagnostic : diagnostics.getDiagnostics()) {
            System.out.format(
                "%s, line %d:%d in %s\n",
                diagnostic.getMessage(null),
                diagnostic.getLineNumber(),
                diagnostic.getColumnNumber(),
                diagnostic.getCode());
        }
    }
}
