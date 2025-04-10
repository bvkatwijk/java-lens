package nl.bvkatwijk.lens.processor;

import io.vavr.Tuple2;
import io.vavr.Value;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Traversable;
import nl.bvkatwijk.lens.Const;
import nl.bvkatwijk.lens.api.ILens;
import nl.bvkatwijk.lens.api.Lens;

import javax.lang.model.element.Element;
import javax.lang.model.element.RecordComponentElement;

/**
 * Code generation specific for lens library
 */
public class LensCode {
    static String lensConstant(String record, String field, String fieldType) {
        return Code.PSF + iLens(record, fieldType) + " " + lensName(field) + " = new " + Const.BASE_LENS + "<>("
               + lensParams(record, field)
               + ");";
    }

    private static String lensParams(String record, String field) {
        return Code.params(
            Code.reference(record, field),
            Code.reference(record + Const.LENS, witherName(field))
        );
    }

    static String witherName(String fieldName) {
        return "with" + Code.capitalize(fieldName);
    }

    static String lensName(String field) {
        return field
            .replaceAll("([a-z])([A-Z])", "$1_$2")
            .toUpperCase();
    }

    static Value<String> imports(Seq<Element> fields) {
        return qualified(fields)
            .append(ILens.class.getName())
            .append(Lens.class.getName())
            .map(Code::importStatement);
    }

    private static Seq<String> qualified(Seq<Element> fields) {
        return fields
            .map(ElementOps::qualifiedType)
            .map(Code::removeGenerics);
    }

    static Value<String> innerDelegation(String typeName) {
        return List.of("")
            .appendAll(delegateWith(typeName))
            .append("")
            .appendAll(delegateGet(typeName));
    }

    static Seq<String> delegateWith(String typeName) {
        var S_T_S = Code.params(Const.PARAM_SOURCE_TYPE, typeName, Const.PARAM_SOURCE_TYPE);
        return List.of(
            "public java.util.function.BiFunction<" + S_T_S + "> with() {",
            Code.indent("return inner.with()"),
            "}"
        );
    }

    static Seq<String> delegateGet(String typeName) {
        var S_T = Code.params(Const.PARAM_SOURCE_TYPE, typeName);
        return List.of(
            "public java.util.function.Function<" + S_T + "> get() {",
            Code.indent("return inner.get()"),
            "}"
        );
    }

    static List<String> lensConstants(List<RecordComponentElement> fields, String name) {
        return fields.map(it -> lensConstant(name, it));
    }

    static String lensConstant(String name, RecordComponentElement it) {
        return lensConstant(name, ElementOps.fieldName(it), ElementOps.qualifiedType(it));
    }

    static String rootLens(String name) {
        return Code.PSF + name + Const.LENS + "<" + name + "> " + Const.ROOT_LENS_NAME + " = new " + name + Const.LENS + "<>(" + Const.BASE_LENS + ".identity());";
    }

    static String iLens(String name) {
        return iLens(Const.PARAM_SOURCE_TYPE, name);
    }

    static String iLens(String from, String to) {
        return Const.ILENS + "<" + Code.params(from, to) + ">";
    }

    public static Value<String> withers(String name, List<RecordComponentElement> fields) {
        return fields
            .zipWithIndex()
            .flatMap(field -> wither(name, field, fields).prepend(""));
    }

    private static Seq<String> wither(String name, Tuple2<RecordComponentElement, Integer> field, List<RecordComponentElement> fields) {
        return Code.with(
            name,
            field._2(),
            fields.map(it -> new Field(
                ElementOps.qualifiedType(it),
                ElementOps.fieldName(it),
                ParamKind.of(it)))
        );
    }
}
