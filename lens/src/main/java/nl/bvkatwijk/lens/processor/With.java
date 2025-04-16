package nl.bvkatwijk.lens.processor;

import io.vavr.Value;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

import static nl.bvkatwijk.lens.processor.Code.indent;

public record With(String typeName, String typeParam, int index, List<Field> fields) {
    With(String typeName, int index, List<Field> fields) {
        this(typeName, Code.uncapitalize(typeName) + "$", index, fields);
    }

    public Seq<String> render() {
        var fieldName = fields.get(index).fieldName();
        return List.of(withDeclareMethod(fieldName))
            .appendAll(indent(withBody()))
            .append("}");
    }

    String withDeclareMethod(String fieldName) {
        return "public static " + typeName + " with" + Code.capitalize(fieldName) + "(" + withParams(fieldName) + ") {";
    }

    private String withParams(String fieldName) {
        return typeName + " " + typeParam + ", " + fields.get(index).typeName() + " " + fieldName;
    }

    private Value<String> withBody() {
        var fieldNames = fields.map(Field::fieldName);
        return List.of("return " + Code.eq(fields.get(index), typeParam))
            .append(indent("? " + typeParam))
            .append(indent(": new " + typeName + "(" + params(typeParam, fieldNames) + ");"));
    }

    String params(String typeName, List<String> fieldNames) {
        return fieldNames
            .zipWithIndex()
            .map(i -> i._2() == index ? fieldNames.get(i._2()) : Code.access(typeName, fieldNames.get(i._2())))
            .mkString(", ");
    }
}
