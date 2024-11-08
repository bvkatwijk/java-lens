package nl.bvkatwijk.lens.processor;

import io.vavr.collection.List;
import io.vavr.collection.Vector;
import nl.bvkatwijk.lens.Const;

import javax.lang.model.element.Element;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.type.TypeMirror;
import java.util.regex.Pattern;

/**
 * Code generation for generic java
 */
public class Code {
    public static final String PSF = "public static final ";

    static String params(String... args) {
        return Vector.of(args)
            .mkString(", ");
    }

    /**
     * @param typeName type name
     * @param method method name
     * @return method reference
     */
    static String reference(String typeName, String method) {
        return typeName + "::" + method;
    }

    static String importStatement(String qualifiedTypeName) {
        return "import " + qualifiedTypeName + ";";
    }

    // todo not very elegant
    static String unqualify(String qualifiedType) {
        return qualifiedType.substring(qualifiedType.lastIndexOf(".") + 1);
    }

    static String indent(String string) {
        return Const.INDENT + string;
    }

    static List<String> indent(List<String> strings) {
        return strings.map(Code::indent);
    }

    static String fieldName(RecordComponentElement it) {
        return it.getSimpleName().toString();
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

    static String typeName(Element it) {
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
}
