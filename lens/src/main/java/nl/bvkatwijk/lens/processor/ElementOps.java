package nl.bvkatwijk.lens.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Utility functions on {@link Element} and subclasses
 */
public final class ElementOps {

    static String fieldName(RecordComponentElement it) {
        return it.getSimpleName().toString();
    }

    static String qualifiedType(Element element) {
        return switch (ParamKind.of(element)) {
            case PRIMITIVE -> classOf(element).getName();
            case DECLARED -> element.asType().toString();
        };
    }

    static String unqualifiedType(Element it) {
        return switch (ParamKind.of(it)) {
            case PRIMITIVE -> classOf(it).getSimpleName();
            case DECLARED -> ((DeclaredType) it.asType()).asElement().getSimpleName().toString();
        };
    }

    static String packageElement(Element element) {
        return switch (element.getKind()) {
            case PACKAGE -> element.toString();
            case RECORD_COMPONENT -> switch (ParamKind.of(element)) {
                case DECLARED -> packageElement(((DeclaredType) element.asType()).asElement());
                case PRIMITIVE -> "java.lang";
            };
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
            default ->
                throw new IllegalArgumentException("Type " + it + " (" + it.getKind() + " " + type.getKind() + ") not yet supported.");
        };
    }
}
