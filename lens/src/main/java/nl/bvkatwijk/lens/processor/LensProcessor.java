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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@Getter
@SupportedSourceVersion(SourceVersion.RELEASE_23)
@SupportedAnnotationTypes(Const.LENS_ANNOTATION_QUALIFIED)
public class LensProcessor extends AbstractProcessor {
    @NonFinal
    List<RecordComponentElement> elements = List.of();

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
            .append(lensRecordDeclaration(name))
            .appendAll(Code.indent(lensContent(name, fields)))
            .append("}");
    }

    private static String lensRecordDeclaration(String name) {
        var lens = LensCode.iLens(name);
        return "public record " + name + Const.LENS + "<" + Const.PARAM_SOURCE_TYPE + ">(" + lens + " inner) implements " + lens + " {";
    }

    private static Value<String> lensContent(String name, List<RecordComponentElement> fields) {
        return List.of(LensCode.rootLens(name))
            .appendAll(LensCode.lensConstants(fields, name))
            .appendAll(lensMethods(fields))
            .appendAll(LensCode.innerDelegation(name))
            .appendAll(LensCode.withers(name, fields));
    }

    static Value<String> lensMethods(List<RecordComponentElement> fields) {
        return fields
            .map(FieldLens::from)
            .flatMap(FieldLens::lensMethod);
    }

    private void writeSourceFile(String pack, String name, String content) throws IOException {
        try (var writer = sourceWriter(pack, name)) {
            writer.append(content);
        }
    }

    private Writer sourceWriter(String pack, String name) throws IOException {
        return processingEnv.getFiler()
            .createSourceFile(pack + "." + name + Const.LENS)
            .openWriter();
    }

    private static HashSet<? extends Element> lensElements(RoundEnvironment roundEnv) {
        return HashSet.ofAll(roundEnv.getElementsAnnotatedWith(Lenses.class));
    }
}
