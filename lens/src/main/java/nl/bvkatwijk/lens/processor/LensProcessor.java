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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

@SupportedSourceVersion(SourceVersion.RELEASE_19)
@SupportedAnnotationTypes("nl.bvkatwijk.lens.Lenses")
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
        var pack = "nl.bvkatwijk.lens.gen";
        var name = el.getSimpleName().toString();
        var fields = List.ofAll(el.getEnclosedElements()
            .stream()
            .filter(RecordComponentElement.class::isInstance)
            .map(RecordComponentElement.class::cast)
            .toList());
        var isoConstants = fields
            .map(it -> isoConstant(name, it.getSimpleName().toString(), typeName(it)))
            .map(this::indent)
            .toList();
        writeSourceFile(pack, name, String.join(
            "\n",
            List.of("package " + pack + ";", "")
                .append(importLens())
                .appendAll(imports(List.of(el).appendAll(fields)))
                .append("")
                .append("public class " + name + Const.LENS + " {")
                .appendAll(isoConstants)
                .append("}")
                .toJavaList()));
    }

    private String importLens() {
        return "import nl.bvkatwijk.lens.kind.Lens;";
    }

    private Iterable<String> imports(List<? extends Element> fields) {
        return fields
            .map(LensProcessor::typeName)
            .map(it -> it.indexOf('<') > 0 ? it.substring(0, it.indexOf('<')) : it)
            .map(LensProcessor::importElement);
    }

    private static String importElement(String qualifiedTypeName) {
        return "import " + qualifiedTypeName + ";";
    }

    private static String typeName(Element it) {
        TypeMirror type = it.asType();
        return switch (type.getKind()) {
            case BOOLEAN -> "java.lang.Boolean";
            case BYTE -> "java.lang.Byte";
            case SHORT -> "java.lang.Short";
            case INT -> "java.lang.Integer";
            case LONG -> "java.lang.Long";
            case CHAR -> "java.lang.Character";
            case FLOAT -> "java.lang.Float";
            case DOUBLE -> "java.lang.Double";
            case VOID -> "java.lang.Void";
            case DECLARED, OTHER -> type.toString();
            case NONE, MODULE, INTERSECTION, UNION, EXECUTABLE, PACKAGE, WILDCARD, TYPEVAR, ERROR, ARRAY, NULL -> throw new IllegalArgumentException("Type " + it + " (" + it.getKind() + " " + type.getKind() + ") not yet supported.");
        };
    }

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
