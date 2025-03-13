package nl.bvkatwijk.lens.processor;

import io.vavr.Value;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.NonFinal;
import nl.bvkatwijk.lens.Const;
import nl.bvkatwijk.lens.Lenses;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_23)
@SupportedAnnotationTypes(Const.LENS_ANNOTATION_QUALIFIED)
public class LensProcessor extends AbstractProcessor {
    @NonFinal @Getter List<RecordComponentElement> elements = List.of();

    @Override
    @SneakyThrows
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!annotations.isEmpty() || !roundEnv.processingOver()) {
            lensElements(roundEnv)
                .filter(it -> ElementKind.RECORD.equals(it.getKind()))
                .forEach(this::writeSourceFile);
        }
        return true;
    }

    @SneakyThrows
    private void writeSourceFile(Element element) {
        var name = element.getSimpleName().toString();
        var fields = List.ofAll(element.getEnclosedElements()
            .stream()
            .filter(RecordComponentElement.class::isInstance)
            .map(RecordComponentElement.class::cast)
            .toList());

        this.elements = elements.appendAll(fields);

        writeSourceFile(ElementOps.packageElement(element), name, String.join(
            "\n",
            lensSourceCode(element, name, fields)
                .toJavaList()));
    }

    private static List<String> lensSourceCode(Element element, String name, List<RecordComponentElement> fields) {
        return List.of("package " + ElementOps.packageElement(element) + ";", "")
            .appendAll(LensCode.imports(List.of(element)))
            .append("")
            .append("public record " + name + Const.LENS + "<" + Const.PARAM_SOURCE_TYPE + ">(" + LensCode.iLens(name) + " inner) implements " + LensCode.iLens(
                name) + " {")
            .appendAll(Code.indent(lensContent(name, fields)))
            .append("}");
    }

    private static Value<String> lensContent(String name, List<RecordComponentElement> fields) {
        return List.of(LensCode.rootLens(name))
            .appendAll(LensCode.lensConstants(fields, name))
            .appendAll(lensMethods(fields))
            .appendAll(LensCode.innerDelegation(name));
    }

    static Iterable<String> lensMethods(List<RecordComponentElement> fields) {
        return fields
            .map(FieldLens::from)
            .flatMap(FieldLens::lensMethod);
    }

    private void writeSourceFile(String pack, String name, String content) throws IOException {
        processingEnv.getFiler()
            .createSourceFile(pack + "." + name + Const.LENS)
            .openWriter()
            .append(content)
            .close();
    }

    private static HashSet<? extends Element> lensElements(RoundEnvironment roundEnv) {
        return HashSet.ofAll(roundEnv.getElementsAnnotatedWith(Lenses.class));
    }
}
