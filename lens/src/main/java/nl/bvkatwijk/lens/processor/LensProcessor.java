package nl.bvkatwijk.lens.processor;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import lombok.SneakyThrows;
import nl.bvkatwijk.lens.Const;
import nl.bvkatwijk.lens.Lenses;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import java.io.IOException;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_23)
@SupportedAnnotationTypes(Const.LENS_ANNOTATION_QUALIFIED)
public class LensProcessor extends AbstractProcessor {
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

        writeSourceFile(packageElement(element), name, String.join(
            "\n",
            lensSourceCode(element, name, fields)
                .toJavaList()));
    }

    public static String packageElement(Element element) {
        return switch (element.getKind()) {
            case PACKAGE -> element.toString();
            case RECORD_COMPONENT -> {
                if (element.asType() instanceof DeclaredType declaredType) {
                    yield packageElement((declaredType).asElement());
                }
                yield "can this happen? primitives maybe?";
            }
            default -> packageElement(element.getEnclosingElement());
        };
    }

    private static List<String> lensSourceCode(Element element, String name, List<RecordComponentElement> fields) {
        return List.of("package " + packageElement(element) + ";", "")
            .appendAll(LensCode.imports(List.of(element)))
            .append("")
            .append("public record " + name + Const.LENS + "<" + Const.PARAM_SOURCE_TYPE + ">(" + LensCode.iLens(name) + " inner) implements " + LensCode.iLens(
                name) + " {")
            .append(LensCode.rootLens(name))
            .appendAll(LensCode.lensConstants(fields, name))
            .appendAll(lensMethods(fields))
            .appendAll(LensCode.innerDelegation(name))
            .append("}");
    }

    static Iterable<String> lensMethods(List<RecordComponentElement> fields) {
        return fields.flatMap(LensProcessor::lensMethod);
    }

    static Iterable<String> lensMethod(RecordComponentElement element) {
        return Code.indent(FieldLens.from(element).lensMethod());
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
