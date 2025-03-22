package nl.bvkatwijk.lens.processor;

import io.vavr.Value;
import io.vavr.collection.List;
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

    static Value<String> with(String typeName, int index, List<String> fieldNames) {
        var fieldName = fieldNames.get(index);
        return List.of("")
            .append("public " + typeName + " with" + Code.capitalize(fieldName) + "(" + typeName + " " + typeName.toLowerCase() + ", int " + fieldName + ") {")
            .appendAll(Code.indent(withBody(typeName, index, fieldNames)))
            .append("}");
    }

    private static Value<String> withBody(String typeName, int index, List<String> fieldNames) {
        var fieldName = fieldNames.get(index);
        return List.of("return " + access(typeName, fieldName) + ".equals(" + fieldName + ")")
            .append(Code.indent("? " + typeName.toLowerCase()))
            .append(Code.indent(": new " + typeName + "(" + params(typeName, index, fieldNames) + ");"));
    }

    private static String params(String typeName, int index, List<String> fieldNames) {
        return fieldNames
            .zipWithIndex()
            .map(i -> i._2() == index ? fieldNames.get(i._2()) : access(typeName, fieldNames.get(i._2())))
            .mkString(", ");
    }

    public static String access(String typeName, String fieldName) {
        return typeName.toLowerCase() + "." + fieldName + "()";
    }
}
