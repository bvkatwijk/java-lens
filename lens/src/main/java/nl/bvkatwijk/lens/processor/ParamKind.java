package nl.bvkatwijk.lens.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;

@Getter
@RequiredArgsConstructor
public enum ParamKind {
    PRIMITIVE,
    DECLARED;

    public static ParamKind of(Element it) {
        return switch (it.asType()) {
            case PrimitiveType primitiveType -> PRIMITIVE;
            case DeclaredType declaredType -> DECLARED;
            default -> throw  new IllegalStateException("Unsupported param kind: " + it);
        };
    }
}
