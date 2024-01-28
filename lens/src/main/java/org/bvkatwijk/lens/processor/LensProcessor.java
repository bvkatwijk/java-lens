package org.bvkatwijk.lens.processor;

import io.vavr.collection.HashSet;
import lombok.SneakyThrows;
import org.bvkatwijk.lens.Lens;
import org.bvkatwijk.lens.ast.Type;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_19)
@SupportedAnnotationTypes("org.bvkatwijk.lens.Lens")
public class LensProcessor extends AbstractProcessor {
    @Override
    @SneakyThrows
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty() && roundEnv.processingOver()) {
            return true;
        }
        lensElements(roundEnv)
            .forEach(it -> {
                System.out.println("it = " + it);
                HashSet.of(new Type("org.bvkatwijk.lens.gen", "PersonLens1"))
                    .forEach(this::writeSourceFile);
            });
        return true;
    }

    @SneakyThrows
    private void writeSourceFile(Type it) {
        processingEnv.getFiler()
            .createSourceFile(it.qualified())
            .openWriter()
            .append("public class PersonLens1 {}")
            .close();
    }

    private static HashSet<? extends Element> lensElements(RoundEnvironment roundEnv) {
        return HashSet.ofAll(roundEnv.getElementsAnnotatedWith(Lens.class));
    }
}
