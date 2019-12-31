package vn.elite.core.reflect.processor;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import java.util.List;
import java.util.Set;

@Slf4j
@SupportedAnnotationTypes({"vn.elite.core.reflect.annotation.Action"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
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
        log.info("#process(...) in " + this.getClass().getSimpleName());

        for (TypeElement ann : annotations) {
            log.info(" ==> TypeElement ann = {}", ann);

            List<? extends Element> es = ann.getEnclosedElements();
            log.info(" ====> ann.getEnclosedElements() count = {}", es.size());
            for (Element e : es) {
                log.info(" ========> EnclosedElement: {}", e);
            }
            Element enclosingElement = ann.getEnclosingElement();

            log.info(" ====> ann.getEnclosingElement() = {}", enclosingElement);

            ElementKind kind = ann.getKind();
            log.info(" ====> ann.getKind() = " + kind);
            Set<? extends Element> e2s = env.getElementsAnnotatedWith(ann);

            log.info(" ====> env.getElementsAnnotatedWith(ann) count = {}", e2s.size());
            for (Element e2 : e2s) {
                log.info(" ========> ElementsAnnotatedWith: {}", e2);
                log.info("           - Kind : {}", e2.getKind());

                // @Action use for method only
                // notify if misuse
                if (e2.getKind() != ElementKind.METHOD) {
                    log.info("           - Error!!!");
                    messager.printMessage(Kind.ERROR, "@Action using for method only ", e2);
                    continue;
                }

                // The name of the method is annotated by @Action
                String methodName = e2.getSimpleName().toString();

                // (ExecutableElement described for method, constructor,...)
                ExecutableElement method = (ExecutableElement) e2;

                log.info("           - method : {}", method);
                TypeMirror retType = method.getReturnType();
                log.info("           -- method.getReturnType() : {}", retType);

                // @Action Only used for method returns the String
                // Notify if misuse
                if (!String.class.getName().equals(retType.toString())) {
                    log.info("           - Error!!!");
                    messager.printMessage(Kind.ERROR, "Method using @Action must return String", e2);
                }
            }
        }
        return true;
    }
}
