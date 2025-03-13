package nl.bvkatwijk.lens.integration;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import lombok.SneakyThrows;
import nl.bvkatwijk.lens.processor.LensProcessor;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.testing.compile.CompilationSubject.assertThat;

public class LensesProcessorTest {
    @Test
    @SneakyThrows
    void processedRecordComponentElement() {
        var lensProcessor = new LensProcessor();
        Class<?> lombokAnnotationProcessor = getClass().getClassLoader().loadClass("lombok.launch.AnnotationProcessorHider$AnnotationProcessor");
        Class<?> lombokClaimingProcessor = getClass().getClassLoader().loadClass("lombok.launch.AnnotationProcessorHider$ClaimingProcessor");
        Compilation compile = Compiler.javac()
            .withProcessors(
                (Processor) lombokAnnotationProcessor.getDeclaredConstructor().newInstance(),
                (Processor) lombokClaimingProcessor.getDeclaredConstructor().newInstance(),
                lensProcessor)
            .compile(loadJavaFileFromTestSrc("nl/bvkatwijk/lens/example/Person.java"));
        assertThat(compile).succeeded();
        Approvals.verify(compile.generatedSourceFiles().get(0).getCharContent(true).toString());
    }

    @SneakyThrows
    static JavaFileObject loadJavaFileFromTestSrc(String packagePath) {
        // Convert package name to path (e.g., "test/Person.java" → "src/test/java/test/Person.java")
        Path path = Paths.get("src/test/java", packagePath);
        String content = Files.readString(path);

        // Convert file path to qualified class name (e.g., "test/Person.java" → "test.Person")
        String qualifiedName = packagePath.replace('/', '.').replace(".java", "");

        return JavaFileObjects.forSourceString(qualifiedName, content);
    }
}
