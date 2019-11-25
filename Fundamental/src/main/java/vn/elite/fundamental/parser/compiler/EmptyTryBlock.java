//package vn.elite.fundamental.parser.compiler;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.util.Arrays;
//
//import javax.tools.DiagnosticCollector;
//import javax.tools.JavaCompiler;
//import javax.tools.JavaCompiler.CompilationTask;
//import javax.tools.JavaFileObject;
//import javax.tools.StandardJavaFileManager;
//import javax.tools.ToolProvider;
//
//public class EmptyTryBlock {
//    public static void main(String[] args) throws IOException, URISyntaxException {
//        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
//        final EmptyTryBlockScanner scanner = new EmptyTryBlockScanner();
//        final EmptyTryBlockProcessor processor = new EmptyTryBlockProcessor(scanner);
//
//        try (
//            final StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null)
//        ) {
//            final Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles(
//                Arrays.asList(
//                    new File(CompilerExample.class.getResource("SampleClassToParse.java").toURI()),
//                    new File(CompilerExample.class.getResource("SampleClass.java").toURI())
//                )
//            );
//
//            final CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sources);
//            task.setProcessors(Arrays.asList(processor));
//            task.call();
//        }
//
//        System.out.format("Empty try/catch blocks: %d", scanner.getNumberOfEmptyTryBlocks());
//    }
//}
