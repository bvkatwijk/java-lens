package nl.bvkatwijk.lens.processor;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import lombok.SneakyThrows;
import nl.bvkatwijk.lens.Const;
import nl.bvkatwijk.lens.Lenses;
import nl.bvkatwijk.lens.kind.ILens;
import nl.bvkatwijk.lens.kind.Lens;

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
                .append("public record " + name + Const.LENS + "<" + Const.PARAM_SOURCE_TYPE + ">(" + Code.iLens(name) + " inner) implements " + Code.iLens(
                    name) + " {")
                .append(rootLens(name))
                .appendAll(lensConstants(fields, name))
                .appendAll(lensMethods(fields))
                .appendAll(innerDelegation(name))
                .append("}")
                .toJavaList()));
    }

    private String rootLens(String name) {
        return Code.indent("public static final " + name + Const.LENS + "<" + name + "> " + Const.ROOT_LENS_NAME + " = new " + name + Const.LENS + "<>(" + Const.BASE_LENS + ".identity());");
    }

    private List<String> lensConstants(List<RecordComponentElement> fields, String name) {
        return fields
            .map(it -> lensConstant(name, Code.fieldName(it), typeName(it)))
            .map(Code::indent)
            .toList();
    }

    private Iterable<String> lensMethods(List<RecordComponentElement> fields) {
        return fields.flatMap(this::lensMethod);
    }

    private Iterable<String> lensMethod(RecordComponentElement element) {
        return qualifiedLens(element).lensMethod();
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
            .contains("@" + Lenses.class.getName())
            ? new FieldLens("unused?", LensKind.LENSED, field)
            : new FieldLens(typeName(field), LensKind.OTHER, field);
    }

    static String lensName(RecordComponentElement field) {
        return lensName(Code.fieldName(field));
    }

    private Iterable<String> innerDelegation(String typeName) {
        return Code.indent(List.of(
            "",
            "public java.util.function.BiFunction<" + Code.params(
                Const.PARAM_SOURCE_TYPE,
                typeName,
                Const.PARAM_SOURCE_TYPE) + "> with() {",
            Code.indent("return inner.with();"),
            "}",
            "",
            "public java.util.function.Function<" + Code.params(Const.PARAM_SOURCE_TYPE, typeName) + "> get() {",
            Code.indent("return inner.get();"),
            "}"
        ));
    }

    private String importLens() {
        return Code.importStatements(
            ILens.class.getName(),
            Lens.class.getName()
        );
    }

    // Todo too ugly
    private Iterable<String> imports(List<? extends Element> fields) {
        return fields
            .map(LensProcessor::typeName)
            .map(LensProcessor::removeGenerics)
            .map(Code::importStatement);
    }

    // Todo too ugly
    private static String removeGenerics(String it) {
        return it.indexOf('<') > 0 ? it.substring(0, it.indexOf('<')) : it;
    }

    private static String typeName(Element it) {
        TypeMirror type = it.asType();
        return switch (type.getKind()) {
            case BOOLEAN -> Boolean.class.getName();
            case BYTE -> Byte.class.getName();
            case SHORT -> Short.class.getName();
            case INT -> Integer.class.getName();
            case LONG -> Long.class.getName();
            case CHAR -> Character.class.getName();
            case FLOAT -> Float.class.getName();
            case DOUBLE -> Double.class.getName();
            case VOID -> Void.class.getName(); // Does it make sense to support Void type?
            case DECLARED -> type.toString();
            case OTHER, NONE, MODULE, INTERSECTION, UNION, EXECUTABLE, PACKAGE, WILDCARD, TYPEVAR, ERROR, ARRAY, NULL ->
                throw new IllegalArgumentException("Type " + it + " (" + it.getKind() + " " + type.getKind() + ") not yet supported.");
        };
    }

    String lensConstant(String record, String field, String fieldType) {
        return "public static final " + Code.iLens(
            record,
            fieldType) + " " + lensName(field) + " = new " + Const.BASE_LENS + "<>("
            + Code.params(Code.reference(record, field), Code.reference(record, witherName(field)))
            + ");";
    }

    private static String lensName(String field) {
        return field.toUpperCase();
    }

    String witherName(String fieldName) {
        return "with" + capitalize(fieldName);
    }

    static String capitalize(String string) {
        return Pattern.compile("^.")
            .matcher(string)
            .replaceFirst(m -> m.group().toUpperCase());
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
