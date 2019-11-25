package vn.elite.fundamental.parser.compiler;

import lombok.val;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class CompilerErrorExample {
    public static void main(String[] args) throws IOException {
        val compiler = ToolProvider.getSystemJavaCompiler();
        val diagnostics = new DiagnosticCollector<JavaFileObject>();

        try (
            val manager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)
        ) {
            URL url = CompilerErrorExample.class.getResource("SampleClassWithErrors.java");
            System.out.println(url);
            String resource = url.getPath();
            System.out.println(resource);
            val file = new File(resource);
            val sources = manager.getJavaFileObjectsFromFiles(Collections.singletonList(file));

            val task = compiler.getTask(null, manager, diagnostics, null, null, sources);
            task.call();
        }

        for (val diagnostic : diagnostics.getDiagnostics()) {
            System.out.format("%s, line %d in %s",
                diagnostic.getMessage(null),
                diagnostic.getLineNumber(),
                diagnostic.getSource().getName());
        }
    }
}
