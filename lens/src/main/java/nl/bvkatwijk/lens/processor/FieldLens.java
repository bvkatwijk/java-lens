package nl.bvkatwijk.lens.processor;

import io.vavr.collection.List;
import lombok.With;
import nl.bvkatwijk.lens.Const;

import javax.lang.model.element.RecordComponentElement;

@With
record FieldLens(String fieldName, String qualifiedType, LensKind lensKind, String pack) {
    public static FieldLens from(RecordComponentElement element) {
        return new FieldLens(
            Code.fieldName(element),
            Code.typeName(element),
            LensKind.from(element),
            LensProcessor.packageElement(element));
    }

    public List<String> lensMethod() {
        return List.of(
            "",
            "public " + returnType() + " " + fieldName + "() {",
            Code.indent(returnStatement()),
            "}"
        );
    }

    private String returnType() {
        return switch (lensKind) {
            case LENSED -> typeLens() + "<" + Const.PARAM_SOURCE_TYPE + ">";
            case PRIMITIVE, OTHER -> LensCode.iLens(qualifiedType);
        };
    }

    private String returnStatement() {
        var chainInner = "inner.andThen(" + LensCode.lensName(fieldName) + ")";
        return Code.ret(switch (lensKind) {
            case LENSED -> "new " + typeLens() + "<>(" + chainInner + ")";
            case PRIMITIVE, OTHER -> chainInner;
        });
    }

    String typeLens() {
        return pack + "." + Code.unqualify(qualifiedType) + Const.LENS;
    }
}
