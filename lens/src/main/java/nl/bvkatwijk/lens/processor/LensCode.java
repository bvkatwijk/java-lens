package nl.bvkatwijk.lens.processor;

import io.vavr.Value;
import io.vavr.collection.List;
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
            + Code.params(Code.reference(record, field), Code.reference(record, witherName(field)))
            + ");";
    }

    static String witherName(String fieldName) {
        return "with" + Code.capitalize(fieldName);
    }

    static String lensName(String field) {
        return field.replaceAll("([a-z])([A-Z])", "$1_$2")
            .toUpperCase();
    }

    static Iterable<String> imports(List<? extends Element> fields) {
        return fields
            .map(ElementOps::typeName)
            .map(Code::removeGenerics)
            .append(ILens.class.getName())
            .append(Lens.class.getName())
            .map(Code::importStatement);
    }

    static Value<String> innerDelegation(String typeName) {
        return List.of("")
            .appendAll(delegateWith(typeName))
            .append("")
            .appendAll(delegateGet(typeName));
    }

    static Value<String> delegateWith(String typeName) {
        return List.of(
            "public java.util.function.BiFunction<" + Code.params(
                Const.PARAM_SOURCE_TYPE,
                typeName,
                Const.PARAM_SOURCE_TYPE) + "> with() {",
            Code.indent(Code.ret("inner.with()")),
            "}"
        );
    }

    static Value<String> delegateGet(String typeName) {
        return List.of(
            "public java.util.function.Function<" + Code.params(Const.PARAM_SOURCE_TYPE, typeName) + "> get() {",
            Code.indent(Code.ret("inner.get()")),
            "}"
        );
    }

    static List<String> lensConstants(List<RecordComponentElement> fields, String name) {
        return fields.map(it -> lensConstant(name, ElementOps.fieldName(it), ElementOps.typeName(it)));
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
}
