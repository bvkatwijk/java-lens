package nl.bvkatwijk.lens.processor;

import io.vavr.collection.List;
import nl.bvkatwijk.lens.Const;

import javax.lang.model.element.RecordComponentElement;

record FieldLens(String qualifiedType, LensKind lensKind, RecordComponentElement field) {
    public List<String> lensMethod() {
        return Code.indent(List.of(
            "",
            "public " + returnValue() + " " + Code.fieldName(field) + "() {",
            Code.indent(returnStatement()),
            "}"
        ));
    }

    public String returnValue() {
        return switch (lensKind) {
            case LENSED -> typeLens(field) + "<" + Const.PARAM_SOURCE_TYPE + ">";
            case PRIMITIVE, OTHER -> Code.iLens(qualifiedType);
        };
    }

    public String returnStatement() {
        var chainInner = "inner.andThen(" + LensProcessor.lensName(field) + ")";
        return switch (lensKind) {
            case LENSED -> "return new " + typeLens(field) + "<>(" + chainInner + ");";
            case PRIMITIVE, OTHER -> "return " + chainInner + ";";
        };
    }

    static String typeLens(RecordComponentElement element) {
        return Const.PACK + "." + fieldTypeUnqualified(element) + Const.LENS;
    }

    // todo not very elegant
    static String fieldTypeUnqualified(RecordComponentElement it) {
        return Code.unqualify(it.asType().toString());
    }
}
