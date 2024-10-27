package nl.bvkatwijk.lens.processor;

import nl.bvkatwijk.lens.Const;

import javax.lang.model.element.RecordComponentElement;

record FieldLens(String qualifiedType, LensKind lensKind, RecordComponentElement field) {

    public String returnValue() {
        return switch (lensKind) {
            case LENSED ->
                Const.PACK + "." + fieldTypeUnqualified(field) + Const.LENS + "<" + Const.PARAM_SOURCE_TYPE + ">";
            case PRIMITIVE, OTHER -> Code.iLens(qualifiedType);
        };
    }

    public String returnStatement() {
        var chainInner = "inner.andThen(" + LensProcessor.lensName(field) + ")";
        return switch (lensKind) {
            case LENSED ->
                "return new " + Const.PACK + "." + fieldTypeUnqualified(field) + Const.LENS + "<>(" + chainInner + ");";
            case PRIMITIVE, OTHER -> "return " + chainInner + ";";
        };
    }

    // todo not very elegant
    public static String fieldTypeUnqualified(RecordComponentElement it) {
        return Code.unqualify(it.asType().toString());
    }
}
