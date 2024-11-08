package nl.bvkatwijk.lens.processor;

import io.vavr.collection.List;
import nl.bvkatwijk.lens.Const;

import javax.lang.model.element.RecordComponentElement;

record FieldLens(String qualifiedType, LensKind lensKind, RecordComponentElement field) {
    public List<String> lensMethod() {
        return Code.indent(List.of(
            "",
            "public " + returnType() + " " + Code.fieldName(field) + "() {",
            Code.indent(returnStatement()),
            "}"
        ));
    }

    public String returnType() {
        return switch (lensKind) {
            case LENSED -> typeLens(field) + "<" + Const.PARAM_SOURCE_TYPE + ">";
            case PRIMITIVE, OTHER -> LensCode.iLens(qualifiedType);
        };
    }

    public String returnStatement() {
        var chainInner = "inner.andThen(" + LensCode.lensName(field) + ")";
        return Code.ret(switch (lensKind) {
            case LENSED -> "new " + typeLens(field) + "<>(" + chainInner + ")";
            case PRIMITIVE, OTHER -> chainInner;
        });
    }

    static String typeLens(RecordComponentElement element) {
        return LensProcessor.packageElement(element) + "." + fieldTypeUnqualified(element) + Const.LENS;
    }

    static String fieldTypeUnqualified(RecordComponentElement it) {
        return Code.unqualify(it.asType().toString());
    }
}
