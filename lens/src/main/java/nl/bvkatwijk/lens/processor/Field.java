package nl.bvkatwijk.lens.processor;

import javax.lang.model.element.RecordComponentElement;

public record Field(String typeName, String fieldName, ParamKind paramKind, RecordComponentElement unused) {
    public boolean isPrimitive() {
        return ParamKind.of(unused).equals(ParamKind.PRIMITIVE);
    }
}
