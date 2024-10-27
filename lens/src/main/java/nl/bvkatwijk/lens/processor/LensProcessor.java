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
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

@SupportedSourceVersion(SourceVersion.RELEASE_23)
@SupportedAnnotationTypes(Const.LENS_ANNOTATION_QUALIFIED)
public class LensProcessor extends AbstractProcessor {
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
    private void writeSourceFile(Element element) {
        var name = element.getSimpleName().toString();
        var fields = List.ofAll(element.getEnclosedElements()
            .stream()
            .filter(RecordComponentElement.class::isInstance)
            .map(RecordComponentElement.class::cast)
            .toList());

        writeSourceFile(Const.PACK, name, String.join(
            "\n",
            List.of("package " + Const.PACK + ";", "")
                .append(importLens())
                .appendAll(imports(List.of(element)))
                .append("")
                .append("public record " + name + Const.LENS + "<T>(Lens<T, " + name + "> inner) implements " + iLens(name) + " {")
                .append(rootLens(name))
                .appendAll(lensConstants(fields, name))
                .appendAll(lensMethods(fields))
                .appendAll(innerDelegation(name))
                .append("}")
                .toJavaList()));
    }

    private String rootLens(String name) {
        return indent("public static final " + name + "Lens<" + name + "> ROOT = new " + name + "Lens<>(Lens.identity());");
    }

    private static String iLens(String name) {
        return "ILens<T, " + name + ">";
    }

    private List<String> lensConstants(List<RecordComponentElement> fields, String name) {
        return fields
            .map(it -> lensConstant(name, fieldName(it), typeName(it)))
            .map(this::indent)
            .toList();
    }

    private static String fieldName(RecordComponentElement it) {
        return it.getSimpleName().toString();
    }

    /**
     * This is not accurate, not sure how to retrieve unqualified type name
     */
    public static String fieldTypeUnqualified(RecordComponentElement it) {
        var qualifiedType = it.asType().toString();
        return qualifiedType.substring(qualifiedType.lastIndexOf(".") + 1);
    }

    private Iterable<String> lensMethods(List<RecordComponentElement> fields) {
        return fields.flatMap(field -> {
            var lensType = qualifiedLens(field);
            return indent(List.of(
                "",
                    "public " + lensType.returnValue() + " " + fieldName(field) + "() {",
                indent(lensType.returnStatement()),
                "}"
            ));
        });
    }

    enum LensKind {
        PRIMITIVE,
        LENSED,
        OTHER;
    }

    record FieldLens(String qualifiedType, LensKind lensKind, RecordComponentElement field) {
        public String returnValue() {
            return switch (lensKind) {
                case LENSED -> Const.PACK + "." + fieldTypeUnqualified(field) + Const.LENS + "<T>";
                case PRIMITIVE, OTHER -> "ILens<T, " + qualifiedType + ">";
            };
        }

        public String returnStatement() {
            var chainInner = "inner.andThen(" + isoName(field) + ")";
            return switch (lensKind) {
                case LENSED -> "return new " + Const.PACK + "." + fieldTypeUnqualified(field) + Const.LENS + "<>(" + chainInner + ");";
                case PRIMITIVE, OTHER -> "return " + chainInner + ";";
            };
        }
    }

    private static FieldLens qualifiedLens(RecordComponentElement field) {
        TypeMirror type = field.asType();
        return switch (type.getKind()) {
            case BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT, DOUBLE, VOID ->
                new FieldLens(typeName(field), LensKind.PRIMITIVE, field);
            case DECLARED -> declared(field);
            case OTHER, NONE, MODULE, INTERSECTION, UNION, EXECUTABLE, PACKAGE, WILDCARD, TYPEVAR, ERROR, ARRAY, NULL ->
                throw new IllegalArgumentException("Type " + field + " (" + field.getKind() + " " + type.getKind() + ") not yet supported.");
        };
    }

    private static FieldLens declared(RecordComponentElement field) {
        return List.ofAll(((DeclaredType) field.asType()).asElement().getAnnotationMirrors())
            .map(AnnotationMirror::toString)
            .contains("@" + Const.LENS_ANNOTATION_QUALIFIED)
            ? new FieldLens("unused?", LensKind.LENSED, field)
            : new FieldLens(typeName(field), LensKind.OTHER, field);
    }

    static String isoName(RecordComponentElement field) {
        return isoName(fieldName(field));
    }

    private Iterable<String> innerDelegation(String typeName) {
        return indent(List.of(
            "",
            "public io.vavr.Function2<T, " + typeName + ", T> with() {",
            indent("return inner.with();"),
            "}",
            "",
            "public io.vavr.Function1<T, " + typeName + "> get() {",
            indent("return inner.get();"),
            "}"
        ));
    }

    private String importLens() {
        return "import nl.bvkatwijk.lens.kind.ILens;\nimport nl.bvkatwijk.lens.kind.Lens;";
    }

    private Iterable<String> imports(List<? extends Element> fields) {
        return fields
            .map(LensProcessor::typeName)
            .map(LensProcessor::removeGenerics)
            .map(LensProcessor::importElement);
    }

    private static String removeGenerics(String it) {
        return it.indexOf('<') > 0 ? it.substring(0, it.indexOf('<')) : it;
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
            case DECLARED -> type.toString();
            case OTHER, NONE, MODULE, INTERSECTION, UNION, EXECUTABLE, PACKAGE, WILDCARD, TYPEVAR, ERROR, ARRAY, NULL ->
                throw new IllegalArgumentException("Type " + it + " (" + it.getKind() + " " + type.getKind() + ") not yet supported.");
        };
    }

    String lensConstant(String record, String field, String fieldType) {
        return "public static final " + Const.LENS + "<" + record + ", " + fieldType + "> " + isoName(field) + " = new " + Const.LENS + "<>(" + record + "::" + witherName(
            field) + ", " + record + "::" + field + ");";
    }

    private static String isoName(String field) {
        return field.toUpperCase();
    }

    String witherName(String fieldName) {
        return "with" + capitalize(fieldName);
    }

    String indent(String string) {
        return Const.INDENT + string;
    }

    static String capitalize(String string) {
        return Pattern.compile("^.")
            .matcher(string)
            .replaceFirst(m -> m.group().toUpperCase());
    }

    List<String> indent(List<String> strings) {
        return strings.map(this::indent);
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
