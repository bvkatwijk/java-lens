package nl.bvkatwijk.lens.integration;

import com.google.testing.compile.Compilation;
import lombok.SneakyThrows;
import nl.bvkatwijk.lens.processor.LensProcessor;
import nl.bvkatwijk.lens.test.TestUtil;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

public class LensesProcessorTest {
    @Test
    @SneakyThrows
    void processedRecordComponentElement() {
        var lensProcessor = new LensProcessor();
        JavaFileObject load = TestUtil.load("example/Person.java");
        Compilation compile = TestUtil.compile(lensProcessor, load);
        Approvals.verify(compile.generatedSourceFiles()
            .getFirst()
            .getCharContent(true)
            .toString());
    }
}
