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
            .compile(load("nl/bvkatwijk/lens/example/Person.java"));
        assertThat(compile).succeeded();
        Approvals.verify(compile.generatedSourceFiles()
            .getFirst()
            .getCharContent(true)
            .toString());
    }

    @SneakyThrows
    static JavaFileObject load(String packagePath) {
        Path path = Paths.get("src/test/java", packagePath);
        return JavaFileObjects.forSourceString(
            packagePath.replace('/', '.').replace(".java", ""),
            Files.readString(path));
    }
}
