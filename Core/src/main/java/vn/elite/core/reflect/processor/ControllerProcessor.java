package vn.elite.core.reflect.processor;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import java.util.List;
import java.util.Set;

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
    public boolean process(
        Set<? extends TypeElement> annotations,
        RoundEnvironment env
    ) {
        log.info("\n\n");
        log.info(" ======================================================== ");
        log.info("#process(...) in " + this.getClass().getSimpleName());
        log.info(" ======================================================== ");

        for (TypeElement ann : annotations) {
            log.info(" ==> TypeElement ann = " + ann);
            //
            List<? extends Element> es = ann.getEnclosedElements();
            log.info(" ====> ann.getEnclosedElements() count = " + es.size());
            for (Element e : es) {
                log.info(" ========> EnclosedElement: " + e);
            }
            Element enclosingElement = ann.getEnclosingElement();

            log.info(" ====> ann.getEnclosingElement() = " + enclosingElement);

            ElementKind kind = ann.getKind();
            log.info(" ====> ann.getKind() = " + kind);
            Set<? extends Element> e2s = env.getElementsAnnotatedWith(ann);

            log.info(" ====> env.getElementsAnnotatedWith(ann) count = "
                       + e2s.size());
            for (Element e2 : e2s) {
                log.info(" ========> ElementsAnnotatedWith: " + e2);
                log.info("           - Kind : " + e2.getKind());

                // @Controller only use for Class
                // Notify if misuse
                if (e2.getKind() != ElementKind.CLASS) {
                    log.info("           - Error!!!");
                    messager.printMessage(Kind.ERROR,
                        "@Controller using for class only ", e2);
                } else {

                    // The name of the class is annotated by @Controller
                    String className = e2.getSimpleName().toString();

                    // @Controller using for class with suffix Controller
                    // Notify if misuse
                    if (!className.endsWith("Controller")) {
                        log.info("           - Error!!!");
                        messager.printMessage(
                            Kind.ERROR,
                            "Class using @Controller must have suffix Controller",
                            e2);
                    }
                }
            }
        }

        return true;
    }
}
