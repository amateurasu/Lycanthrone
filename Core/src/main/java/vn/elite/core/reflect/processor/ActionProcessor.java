package vn.elite.core.reflect.processor;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;

@Slf4j
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"vn.elite.core.reflect.annotation.Action"})
public class ActionProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public void init(ProcessingEnvironment env) {
        filer = env.getFiler();
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        log.trace("#process(...) in {}, count = {}", this.getClass().getSimpleName(), annotations.size());

        for (TypeElement ann : annotations) {
            log.trace("TypeElement ann = {}", ann);
            log.trace("{}", ann.getQualifiedName());

            List<? extends Element> es = ann.getEnclosedElements();
            log.trace("ann.getEnclosedElements() count = {}", es.size());
            es.forEach(e -> log.trace("EnclosedElement: {}", e));

            Element enclosingElement = ann.getEnclosingElement();
            log.trace("ann.getEnclosingElement() = {}", enclosingElement);

            ElementKind kind = ann.getKind();
            log.trace("ann.getKind() = {}", kind);

            Set<? extends Element> e2s = env.getElementsAnnotatedWith(ann);
            log.trace("env.getElementsAnnotatedWith(ann) count = {}", e2s.size());

            // The name of the method is annotated by @Action
            // (ExecutableElement described for method, constructor,...)
            // @Action Only used for method returns the String
            // Notify if misuse
            e2s.forEach(e2 -> {
                ExecutableElement method = (ExecutableElement) e2;
                TypeMirror retType = method.getReturnType();
                log.trace("- {} {}: {}", e2.getKind(), e2, retType);

                if (String.class.getName().equals(retType.toString())) return;

                String msg = "Method using @Action must return String";
                log.trace(msg);
                messager.printMessage(Diagnostic.Kind.ERROR, msg, e2);
            });
        }
        return true;
    }
}
