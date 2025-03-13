package nl.bvkatwijk.lens.processor;

import io.vavr.collection.List;
import nl.bvkatwijk.lens.Lenses;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

enum LensKind {
    PRIMITIVE,
    LENSED,
    OTHER;

    public static LensKind from(RecordComponentElement field) {
        TypeMirror type = field.asType();
        return switch (type.getKind()) {
            case BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT, DOUBLE, VOID -> PRIMITIVE;
            case DECLARED -> hasLensAnnotation(field) ? LENSED : OTHER;
            case OTHER, NONE, MODULE, INTERSECTION, UNION, EXECUTABLE, PACKAGE, WILDCARD, TYPEVAR, ERROR, ARRAY, NULL ->
                throw new IllegalArgumentException("Type " + field + " (" + field.getKind() + " " + type.getKind() + ") not yet supported.");
        };
    }

    static boolean hasLensAnnotation(RecordComponentElement field) {
        return List.ofAll(((DeclaredType) field.asType()).asElement().getAnnotationMirrors())
            .map(AnnotationMirror::toString)
            .contains("@" + Lenses.class.getName());
    }
}
