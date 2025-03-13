package nl.bvkatwijk.lens.test;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import lombok.SneakyThrows;
import nl.bvkatwijk.lens.processor.LensProcessor;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.testing.compile.CompilationSubject.assertThat;

public class TestUtil {
    @SneakyThrows
    public static JavaFileObject load(String packagePath) {
        String qualifiedPath = "nl/bvkatwijk/lens/" + packagePath;
        Path path = Paths.get("src/test/java/", qualifiedPath);
        return JavaFileObjects.forSourceString(
            qualifiedName(qualifiedPath),
            Files.readString(path));
    }


    @SneakyThrows
    public static Compilation compile(LensProcessor lensProcessor, JavaFileObject load) {
        Class<?> lombokAnnotationProcessor = TestUtil.class.getClassLoader().loadClass("lombok.launch.AnnotationProcessorHider$AnnotationProcessor");
        Class<?> lombokClaimingProcessor = TestUtil.class.getClassLoader().loadClass("lombok.launch.AnnotationProcessorHider$ClaimingProcessor");
        Compilation compile = Compiler.javac()
            .withProcessors(
                (Processor) lombokAnnotationProcessor.getDeclaredConstructor().newInstance(),
                (Processor) lombokClaimingProcessor.getDeclaredConstructor().newInstance(),
                lensProcessor)
            .compile(load);
        assertThat(compile).succeeded();
        return compile;
    }

    private static String qualifiedName(String packagePath) {
        return packagePath.replace('/', '.').replace(".java", "");
    }
}
