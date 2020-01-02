package vn.elite.core.compiler;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class JavaCCProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return null;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        SourceVersion[] versions = SourceVersion.values();
        System.out.println(Arrays.toString(versions));

        return versions[versions.length - 1];
    }

    /**
     * This method is a simplified version of {@link lombok.javac.apt.LombokProcessor.getJavacProcessingEnvironment} It
     * simply returns the processing environment, but in case of gradle incremental compilation, the delegate
     * ProcessingEnvironment of the gradle wrapper is returned.
     */
    public static ProcessingEnvironment getJavacProcessingEnvironment(
        ProcessingEnvironment procEnv,
        List<String> delayedWarnings
    ) {
        return null;
    }
}
