package vn.elite.core.reflect.processor;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import java.util.Set;

@Slf4j
@SupportedAnnotationTypes({"vn.elite.core.reflect.annotation.PublicFinal"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class PublicFinalProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public void init(ProcessingEnvironment env) {
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        log.debug("#process(...) in {}, annotations count = {}", this.getClass().getSimpleName(), annotations.size());

        for (TypeElement ann : annotations) {
            Set<? extends Element> e2s = env.getElementsAnnotatedWith(ann);
            for (Element e2 : e2s) {
                log.debug("- e2 = {}", e2);

                Set<Modifier> modifiers = e2.getModifiers();

                // @PublicFinal only using for public & final
                // Notify if misuse
                if (!modifiers.contains(Modifier.FINAL) || !modifiers.contains(Modifier.PUBLIC)) {
                    log.debug("- Error!!!");
                    messager.printMessage(Kind.ERROR, "Method/field wasn't public and final", e2);
                }
            }
        }

        // All PublicFinal annotations are handled by this Processor.
        return true;
    }
}
