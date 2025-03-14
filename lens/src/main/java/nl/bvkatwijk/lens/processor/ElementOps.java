package nl.bvkatwijk.lens.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Utiilty functions on {@link javax.lang.model.element.Element} and subclasses
 */
public final class ElementOps {
    static String fieldName(RecordComponentElement it) {
        return it.getSimpleName().toString();
    }

    static String qualifiedType(Element it) {
        TypeMirror type = it.asType();
        return switch (type.getKind()) {
            case BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT, DOUBLE, VOID -> classOf(it).getName();
            case DECLARED -> type.toString();
            case OTHER, NONE, MODULE, INTERSECTION, UNION, EXECUTABLE, PACKAGE, WILDCARD, TYPEVAR, ERROR, ARRAY, NULL ->
                throw new IllegalArgumentException("Type " + it + " (" + it.getKind() + " " + type.getKind() + ") not yet supported.");
        };
    }

    static String unqualifiedType(Element it) {
        TypeMirror type = it.asType();
        return switch (type.getKind()) {
            case BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT, DOUBLE, VOID -> classOf(it).getSimpleName();
            case DECLARED -> ((DeclaredType) type).asElement().getSimpleName().toString();
            case OTHER, NONE, MODULE, INTERSECTION, UNION, EXECUTABLE, PACKAGE, WILDCARD, TYPEVAR, ERROR, ARRAY, NULL ->
                throw new IllegalArgumentException("Type " + it + " (" + it.getKind() + " " + type.getKind() + ") not yet supported.");
        };
    }

    static String packageElement(Element element) {
        return switch (element.getKind()) {
            case PACKAGE -> element.toString();
            case RECORD_COMPONENT -> {
                if (element.asType() instanceof DeclaredType declaredType) {
                    yield packageElement(declaredType.asElement());
                }
                yield packageElement(element.getEnclosingElement());
            }
            default -> packageElement(element.getEnclosingElement());
        };
    }

    static Class<?> classOf(Element it) {
        TypeMirror type = it.asType();
        return switch (type.getKind()) {
            case BOOLEAN -> Boolean.class;
            case BYTE -> Byte.class;
            case SHORT -> Short.class;
            case INT -> Integer.class;
            case LONG -> Long.class;
            case CHAR -> Character.class;
            case FLOAT -> Float.class;
            case DOUBLE -> Double.class;
            case VOID -> Void.class;
            default ->
                throw new IllegalArgumentException("Type " + it + " (" + it.getKind() + " " + type.getKind() + ") not yet supported.");
        };
    }
}
