package nl.bvkatwijk.lens.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Utiilty functions on {@link javax.lang.model.element.Element} and subclasses
 */
public class ElementOps {
    static String fieldName(RecordComponentElement it) {
        return qualifiedName(it);
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

    static String unqualifiedTypeName(RecordComponentElement element) {
        return Code.unqualify(qualifiedName(element));
    }

    private static String qualifiedName(RecordComponentElement element) {
        return element.getSimpleName().toString();
    }

    static String packageElement(Element element) {
        return switch (element.getKind()) {
            case PACKAGE -> element.toString();
            case RECORD_COMPONENT -> {
                if (element.asType() instanceof DeclaredType declaredType) {
                    yield packageElement(declaredType.asElement());
                }
                yield "can this happen? primitives maybe?";
            }
            default -> packageElement(element.getEnclosingElement());
        };
    }
}
