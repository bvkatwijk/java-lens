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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
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
            .filter(it -> ElementKind.RECORD.equals(it.getKind()))
            .forEach(it -> writeSourceFile(new Type("org.bvkatwijk.lens.gen", it.getSimpleName() + "Lens")));
        return true;
    }

    @SneakyThrows
    private void writeSourceFile(Type it) {
        writeSourceFile(it, String.join(
            "\n",
            "package " + it.pack() + ";",
            "",
            "import org.bvkatwijk.lens.Address;",
            "import org.bvkatwijk.lens.Focus;",
            "import org.bvkatwijk.lens.IFocus;",
            "",
            "import java.util.function.Function;",
            "",
            "public class " + it.name() + " {",
            "    public <TARGET> IFocus<Address, TARGET> focus(Function<Address, TARGET> f, Function<TARGET, Address> with) {",
            "        return new Focus<>(this, with, f);",
            "    }",
            "}"
        ));
    }

    private void writeSourceFile(Type it, String content) throws IOException {
        processingEnv.getFiler()
            .createSourceFile(it.qualified())
            .openWriter()
            .append(content)
            .close();
    }

    private static HashSet<? extends Element> lensElements(RoundEnvironment roundEnv) {
        return HashSet.ofAll(roundEnv.getElementsAnnotatedWith(Lens.class));
    }
}
