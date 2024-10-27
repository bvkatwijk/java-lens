package nl.bvkatwijk.lens.processor;

import io.vavr.collection.Vector;
import nl.bvkatwijk.lens.Const;

public class Code {
    static String params(String... args) {
        return Vector.of(args)
            .mkString(", ");
    }

    /**
     * @param typeName type name
     * @param method method name
     * @return method reference
     */
    static String reference(String typeName, String method) {
        return typeName + "::" + method;
    }

    static String importStatement(String qualifiedTypeName) {
        return "import " + qualifiedTypeName + ";";
    }

    static String importStatements(String... args) {
        return Vector.of(args)
            .map(Code::importStatement)
            .mkString("\n");
    }

    static String iLens(String from, String to) {
        return Const.ILENS + "<" + params(from, to) + ">";
    }

    static String iLens(String name) {
        return iLens("T", name);
    }
}