package vn.elite.core.reflect.processor;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.ERROR;

@Slf4j
@SupportedAnnotationTypes({"vn.elite.core.reflect.annotation.Controller"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ControllerProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public void init(ProcessingEnvironment env) {
        filer = env.getFiler();
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        log.info("#process(...) in {}", this.getClass().getSimpleName());

        for (TypeElement ann : annotations) {
            log.info("TypeElement ann = {}", ann);

            List<? extends Element> es = ann.getEnclosedElements();
            log.info("ann.getEnclosedElements() count = {}", es.size());

            es.forEach(e -> log.info("EnclosedElement: {}", e));

            Element enclosingElement = ann.getEnclosingElement();
            log.info("ann.getEnclosingElement() = {}", enclosingElement);

            ElementKind kind = ann.getKind();
            log.info("ann.getKind() = {}", kind);

            Set<? extends Element> annotated = env.getElementsAnnotatedWith(ann);
            log.info("env.getElementsAnnotatedWith(ann) count = {}", annotated.size());

            annotated.forEach(e2 -> {
                log.info("Annotated element: {} {}", e2.getKind().name().toLowerCase(), e2);
                String className = e2.getSimpleName().toString();

                if (className.endsWith("Controller")) return;

                String msg = "Class using @Controller must have suffix Controller";
                log.info(msg);
                messager.printMessage(ERROR, msg, e2);
            });
        }

        return true;
    }
}
