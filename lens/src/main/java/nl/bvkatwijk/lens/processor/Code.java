package nl.bvkatwijk.lens.processor;

import io.vavr.Value;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;
import nl.bvkatwijk.lens.Const;

import java.util.regex.Pattern;

/**
 * Code generation for generic java
 */
public final class Code {
    public static final String PSF = "public static final ";

    static String params(String... args) {
        return Vector.of(args)
            .mkString(", ");
    }

    /**
     * @param typeName type name
     * @param method   method name
     * @return method reference
     */
    static String reference(String typeName, String method) {
        return typeName + "::" + method;
    }

    static String importStatement(String qualifiedTypeName) {
        return "import " + qualifiedTypeName + ";";
    }

    static String unqualify(String qualifiedType) {
        return qualifiedType.substring(qualifiedType.lastIndexOf(".") + 1);
    }

    static String indent(String string) {
        return Const.INDENT + string;
    }

    static Value<String> indent(Value<String> strings) {
        return strings.map(Code::indent);
    }

    static String removeGenerics(String arg) {
        return arg.indexOf('<') > 0 ? arg.substring(0, arg.indexOf('<')) : arg;
    }

    static String ret(String arg) {
        return "return " + arg + ";";
    }

    static String capitalize(String string) {
        return Pattern.compile("^.")
            .matcher(string)
            .replaceFirst(m -> m.group().toUpperCase());
    }

    static Seq<String> with(String typeName, int index, List<Field> fields) {
        var fieldName = fields.get(index).fieldName();
        return List.of(withDeclareMethod(typeName, index, fields, fieldName))
            .appendAll(Code.indent(withBody(typeName, index, fields)))
            .append("}");
    }

    private static String withDeclareMethod(String typeName, int index, List<Field> fields, String fieldName) {
        return "public static " + typeName + " with" + Code.capitalize(fieldName) + "(" + withParams(typeName, index, fields, fieldName) + ") {";
    }

    private static String withParams(String typeName, int index, List<Field> fields, String fieldName) {
        return typeName + " " + typeName.toLowerCase() + ", " + fields.get(index).typeName() + " " + fieldName;
    }

    private static Value<String> withBody(String typeName, int index, List<Field> fields) {
        var fieldNames = fields.map(Field::fieldName);
        return List.of("return " + eq(fields.get(index), typeName))
            .append(Code.indent("? " + typeName.toLowerCase()))
            .append(Code.indent(": new " + typeName + "(" + params(typeName, index, fieldNames) + ");"));
    }

    private static String eq(Field field, String typeName) {
        var fieldName = field.fieldName();
        return switch (field.paramKind()) {
            case PRIMITIVE -> access(typeName, fieldName) + " == " + fieldName;
            case DECLARED -> access(typeName, fieldName) + " != null && " +
                          access(typeName, fieldName) + ".equals(" + fieldName + ")";
        };
    }

    /**
     * Record constructor params in wither
     */
    private static String params(String typeName, int index, List<String> fieldNames) {
        return fieldNames
            .zipWithIndex()
            .map(i -> i._2() == index ? fieldNames.get(i._2()) : access(typeName, fieldNames.get(i._2())))
            .mkString(", ");
    }

    /**
     * Record accessor
     */
    public static String access(String typeName, String fieldName) {
        return typeName.toLowerCase() + "." + fieldName + "()";
    }

    public static String render(Traversable<String> code) {
        return code.mkString("\n");
    }
}
