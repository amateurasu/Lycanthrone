package vn.elite.core.reflect.processor;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.tools.Diagnostic.Kind.ERROR;

@Slf4j
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("vn.elite.core.reflect.annotation.BuilderProperty")
public class BuilderPropertyProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            Map<Boolean, List<Element>> annotatedMethods = annotatedElements.stream()
                .collect(Collectors.partitioningBy(element -> {
                    ExecutableType type = (ExecutableType) element.asType();
                    String name = element.getSimpleName().toString();

                    return type.getParameterTypes().size() == 1 && name.startsWith("set");
                }));

            List<Element> setters = annotatedMethods.get(true);
            List<Element> otherMethods = annotatedMethods.get(false);

            otherMethods.forEach(element -> messager.printMessage(
                ERROR, "@BuilderProperty must be applied to a setXxx method with a single argument", element));

            if (setters.isEmpty()) continue;

            Map<TypeMirror, Integer> typeMap = new HashMap<>();
            Map<String, ? extends TypeMirror> setterMap = setters.stream()
                .collect(Collectors.toMap(
                    setter -> setter.getSimpleName().toString(),
                    setter -> {
                        TypeMirror type = ((ExecutableType) setter.asType()).getParameterTypes().get(0);
                        if (!type.getKind().isPrimitive()) {
                            typeMap.compute(type, (key, value) -> value == null ? 1 : value + 1);
                        }
                        return type;
                    }));
            log.trace("Setters: {}", setterMap);

            try {
                String className = setters.get(0).getEnclosingElement().toString();
                writeBuilderFile(className, setterMap, typeMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private void writeBuilderFile(
        String className,
        Map<String, ? extends TypeMirror> setterMap,
        Map<TypeMirror, Integer> typeMap
    ) throws IOException {

        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Builder";
        String builderSimpleClassName = builderClassName.substring(lastDot + 1);

        // System.out.println(packageName);
        // System.out.println(className);
        // System.out.println(builderClassName);

        JavaFileObject builderFile = filer.createSourceFile(builderClassName);
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
