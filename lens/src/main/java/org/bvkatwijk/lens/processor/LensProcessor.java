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
            .forEach(this::writeLens);
        return true;
    }

    private void writeLens(Element it) {
        var fields = it.getEnclosedElements()
            .stream()
            .filter(RecordComponentElement.class::isInstance)
            .map(RecordComponentElement.class::cast)
            .toList();
        writeSourceFile(new Type("org.bvkatwijk.lens.gen", it.getSimpleName().toString()));
    }

    @SneakyThrows
    private void writeSourceFile(Type it) {
        writeSourceFile(it, String.join(
            "\n",
            "package " + it.pack() + ";",
            "",
            "import org.bvkatwijk.lens.Address;",
            "import org.bvkatwijk.lens.kind.Lens;",
            "import org.bvkatwijk.lens.Person;",
            "",
            "import java.util.function.Function;",
            "",
            "public class " + it.name() + Const.LENS + " {",
            indent(iso(it.name(), "name", "String")),
            "}"
        ));
    }

    // Address person Integer
    public String iso(String record, String field, String fieldType) {
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
