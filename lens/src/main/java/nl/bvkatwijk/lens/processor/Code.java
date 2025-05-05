package nl.bvkatwijk.lens.processor;

import io.vavr.Value;
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

    static String uncapitalize(String string) {
        return Pattern.compile("^.")
            .matcher(string)
            .replaceFirst(m -> m.group().toLowerCase());
    }

    static String eq(Field field, String typeName) {
        var fieldName = field.fieldName();
        var self = access(typeName, fieldName);
        return switch (field.paramKind()) {
            case PRIMITIVE -> self + " == " + fieldName;
            case DECLARED -> self + " != null && " + self + ".equals(" + fieldName + ")";
        };
    }

    /**
     * Record accessor
     */
    public static String access(String typeName, String fieldName) {
        return typeName + "." + fieldName + "()";
    }

    public static String render(Traversable<String> code) {
        return code.mkString("\n");
    }
}
