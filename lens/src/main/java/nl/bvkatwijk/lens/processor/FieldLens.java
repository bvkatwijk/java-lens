package nl.bvkatwijk.lens.processor;

import io.vavr.collection.List;
import lombok.With;
import nl.bvkatwijk.lens.Const;

import javax.lang.model.element.RecordComponentElement;

/**
 * @param fieldName
 * @param qualifiedType
 * @param lensKind      {@link LensKind}
 * @param pack
 */
@With
record FieldLens(String fieldName, String qualifiedType, String unqualifiedType, LensKind lensKind, String pack) {
    public static FieldLens from(RecordComponentElement element) {
        return new FieldLens(
            ElementOps.fieldName(element),
            ElementOps.qualifiedType(element),
            ElementOps.unqualifiedType(element),
            LensKind.from(element),
            ElementOps.packageElement(element));
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
            case LENSED -> qualifiedLens() + "<" + Const.PARAM_SOURCE_TYPE + ">";
            case PRIMITIVE, OTHER -> LensCode.iLens(qualifiedType);
        };
    }

    private String returnStatement() {
        var chainInner = "inner.andThen(" + LensCode.lensName(fieldName) + ")";
        return Code.ret(switch (lensKind) {
            case LENSED -> "new " + qualifiedLens() + "<>(" + chainInner + ")";
            case PRIMITIVE, OTHER -> chainInner;
        });
    }

    String qualifiedLens() {
        return pack + "." + Code.unqualify(qualifiedType) + Const.LENS;
    }
}
