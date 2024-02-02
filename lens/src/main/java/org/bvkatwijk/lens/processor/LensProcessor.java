package org.bvkatwijk.lens.processor;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import lombok.SneakyThrows;
import org.bvkatwijk.lens.Const;
import org.bvkatwijk.lens.Lenses;
import org.bvkatwijk.lens.ast.Type;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

@SupportedSourceVersion(SourceVersion.RELEASE_19)
@SupportedAnnotationTypes("org.bvkatwijk.lens.Lenses")
public class LensProcessor extends AbstractProcessor {
    protected static final String LENS = "Lens";

    @Override
    @SneakyThrows
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty() && roundEnv.processingOver()) {
            return true;
        }
        lensElements(roundEnv)
            .filter(it -> ElementKind.RECORD.equals(it.getKind()))
            .forEach(this::writeSourceFile);
        return true;
    }

    @SneakyThrows
    private void writeSourceFile(Element el) {
        Type type = new Type("org.bvkatwijk.lens.gen", el.getSimpleName().toString());
        var fields = List.ofAll(el.getEnclosedElements()
            .stream()
            .filter(RecordComponentElement.class::isInstance)
            .map(RecordComponentElement.class::cast)
            .toList());

        var isoConstants = fields
            .map(it -> isoConstant(type.name(), it.getSimpleName().toString(), it.getSimpleName().toString()))
            .map(this::indent)
            .toList();

        writeSourceFile(type, String.join(
            "\n",
            List.of(
                    "package " + type.pack() + ";",
                    "",
                    "import org.bvkatwijk.lens.Address;",
                    "import org.bvkatwijk.lens.kind.Lens;",
                    "import org.bvkatwijk.lens.Person;",
                    "",
                    "import java.util.function.Function;",
                    "",
                    "public class " + type.name() + Const.LENS + " {")
                .appendAll(isoConstants)
                .append("}")
                .toJavaList()));
    }

    // Address person Integer
    public String isoConstant(String record, String field, String fieldType) {
        return "public static final " + Const.LENS + "<" + record + ", " + fieldType + "> " + isoName(field) + " = new " + Const.LENS + "<>(" + record + "::" + witherName(
            field) + ", " + record + "::" + field + ");";
    }

    private static String isoName(String field) {
        return field.toUpperCase();
    }

    public String witherName(String fieldName) {
        return "with" + capitalize(fieldName);
    }

    public String indent(String string) {
        return Const.INDENT + string;
    }

    public String capitalize(String string) {
        return Pattern.compile("^.")
            .matcher(string)
            .replaceFirst(m -> m.group().toUpperCase());
    }

    public List<String> indent(List<String> strings) {
        return strings.map(this::indent);
    }

    private void writeSourceFile(Type it, String content) throws IOException {
        processingEnv.getFiler()
            .createSourceFile(it.qualified() + LENS)
            .openWriter()
            .append(content)
            .close();
    }

    private static HashSet<? extends Element> lensElements(RoundEnvironment roundEnv) {
        return HashSet.ofAll(roundEnv.getElementsAnnotatedWith(Lenses.class));
    }
}
