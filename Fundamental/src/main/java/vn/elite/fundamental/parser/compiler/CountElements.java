package vn.elite.fundamental.parser.compiler;

import lombok.val;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

public class CountElements {
    public static void main(String[] args) throws IOException, URISyntaxException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final CountClassesMethodsFieldsScanner scanner = new CountClassesMethodsFieldsScanner();
        final CountElementsProcessor processor = new CountElementsProcessor(scanner);

        try (val manager = compiler.getStandardFileManager(diagnostics, null, null)) {
            val file = new File(CompilerExample.class.getResource("SampleClassToParse.java").toURI());
            val sources = manager.getJavaFileObjectsFromFiles(Collections.singletonList(file));

            final CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sources);
            task.setProcessors(Collections.singletonList(processor));
            task.call();
        }

        System.out.format("Classes %d, methods/constructors %d, fields %d", scanner.getNumberOfClasses(),
            scanner.getNumberOfMethods(), scanner.getNumberOfFields());
    }
}
