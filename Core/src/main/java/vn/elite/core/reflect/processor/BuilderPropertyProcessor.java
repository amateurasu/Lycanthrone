package vn.elite.core.reflect.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("vn.elite.core.reflect.annotation.BuilderProperty")
public class BuilderPropertyProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            Map<Boolean, List<Element>> annotatedMethods = annotatedElements.stream()
                .collect(Collectors.partitioningBy(element -> {
                    ExecutableType type = (ExecutableType) element.asType();
                    return type.getParameterTypes().size() == 1
                        && element.getSimpleName().toString().startsWith("set");
                }));

            List<Element> setters = annotatedMethods.get(true);
            List<Element> otherMethods = annotatedMethods.get(false);

            otherMethods.forEach(element -> messager
                .printMessage(
                    Diagnostic.Kind.ERROR,
                    "@BuilderProperty must be applied to a setXxx method with a single argument",
                    element));

            if (setters.isEmpty()) continue;

            Map<String, String> setterMap = setters.stream()
                .collect(Collectors.toMap(
                    setter -> setter.getSimpleName().toString(),
                    setter -> ((ExecutableType) setter.asType()).getParameterTypes().get(0).toString()));

            try {
                String className = setters.get(0).getEnclosingElement().toString();
                writeBuilderFile(className, setterMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private void writeBuilderFile(String className, Map<String, String> setterMap) throws IOException {

        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        System.out.println(packageName);

        System.out.println(className);
        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Builder";
        System.out.println(builderClassName);
        String builderSimpleClassName = builderClassName.substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (packageName != null) {
                out.format("package %s;\n\n", packageName);
            }

            out.format("public class %s {\n\n", builderSimpleClassName);
            out.format("    private %s object = new %1$s();\n\n", simpleClassName);
            out.format("    public %s build() { return object; }\n", simpleClassName);

            setterMap.forEach((methodName, argumentType) -> {
                out.println();
                out.format("    public %s %s(%s value) {\n", builderSimpleClassName, methodName, argumentType);
                out.format("        object.%s(value);\n", methodName);
                out.format("        return this;\n");
                out.format("    }\n");
            });

            out.println("}");
        }
    }
}
