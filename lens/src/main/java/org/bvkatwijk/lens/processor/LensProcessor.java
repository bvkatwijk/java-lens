package org.bvkatwijk.lens.processor;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import lombok.SneakyThrows;
import org.bvkatwijk.lens.Const;
import org.bvkatwijk.lens.Lenses;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

@SupportedSourceVersion(SourceVersion.RELEASE_19)
@SupportedAnnotationTypes("org.bvkatwijk.lens.Lenses")
public class LensProcessor extends AbstractProcessor {
    protected static final String LENS = "Lens";

    @Override
    @SneakyThrows
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty() && roundEnv.processingOver()) {
            return true;
        }
        lensElements(roundEnv)
            .filter(it -> ElementKind.RECORD.equals(it.getKind()))
            .forEach(this::writeSourceFile);
        return true;
    }

    @SneakyThrows
    private void writeSourceFile(Element el) {
        var pack = "org.bvkatwijk.lens.gen";
        var name = el.getSimpleName().toString();
        var fields = List.ofAll(el.getEnclosedElements()
            .stream()
            .filter(RecordComponentElement.class::isInstance)
            .map(RecordComponentElement.class::cast)
            .toList());

        var isoConstants = fields
            .map(it -> {
                String typeName = typeName(it);
                return isoConstant(name, it.getSimpleName().toString(), typeName);
            })
            .map(this::indent)
            .toList();

        writeSourceFile(pack, name, String.join(
            "\n",
            List.of(
                    "package " + pack + ";",
                    "",
                    "import org.bvkatwijk.lens.Address;",
                    "import org.bvkatwijk.lens.kind.Lens;",
                    "import org.bvkatwijk.lens.Person;",
                    "",
                    "public class " + name + Const.LENS + " {")
                .appendAll(isoConstants)
                .append("}")
                .toJavaList()));
    }

    private static String typeName(RecordComponentElement it) {
        TypeMirror type = it.asType();
        return switch (type.getKind()) {
            case BOOLEAN -> "Boolean";
            case BYTE -> "Byte";
            case SHORT -> "Short";
            case INT -> "Integer";
            case LONG -> "Long";
            case CHAR -> "Character";
            case FLOAT -> "Float";
            case DOUBLE -> "Double";
            case VOID -> "Void";
            case DECLARED, OTHER -> type.toString();
            case NONE, MODULE, INTERSECTION, UNION, EXECUTABLE, PACKAGE, WILDCARD, TYPEVAR, ERROR, ARRAY, NULL -> throw new IllegalArgumentException("Type " + it + " (" + it.getKind() + " " + type.getKind() + ") not yet supported.");
        };
    }

    // Address person Integer
    public String isoConstant(String record, String field, String fieldType) {
        return "public static final " + Const.LENS + "<" + record + ", " + fieldType + "> " + isoName(field) + " = new " + Const.LENS + "<>(" + record + "::" + witherName(
            field) + ", " + record + "::" + field + ");";
    }

    private static String isoName(String field) {
        return field.toUpperCase();
    }

    public String witherName(String fieldName) {
        return "with" + capitalize(fieldName);
    }

    public String indent(String string) {
        return Const.INDENT + string;
    }

    public String capitalize(String string) {
        return Pattern.compile("^.")
            .matcher(string)
            .replaceFirst(m -> m.group().toUpperCase());
    }

    public List<String> indent(List<String> strings) {
        return strings.map(this::indent);
    }

    private void writeSourceFile(String pack, String name, String content) throws IOException {
        processingEnv.getFiler()
            .createSourceFile(pack + "." + name + LENS)
            .openWriter()
            .append(content)
            .close();
    }

    private static HashSet<? extends Element> lensElements(RoundEnvironment roundEnv) {
        return HashSet.ofAll(roundEnv.getElementsAnnotatedWith(Lenses.class));
    }
}
