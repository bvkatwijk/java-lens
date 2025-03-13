package nl.bvkatwijk.lens.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import nl.bvkatwijk.lens.test.TestUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.RecordComponentElement;
import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ElementOpsTest {

    @Nested
    class OnString {
        public static final RecordComponentElement STRING_STREET = processedRecordComponentElement();

        @Test
        void unqualifiedTypeName() {
            assertEquals("String", ElementOps.unqualifiedTypeName(STRING_STREET));
        }

        @Test
        void typeName() {
            assertEquals("java.lang.String", ElementOps.typeName(STRING_STREET));
        }

        @Test
        void packageElement() {
            assertEquals("java.lang", ElementOps.packageElement(STRING_STREET));
        }

        @Test
        void fieldName() {
            assertEquals("street", ElementOps.fieldName(STRING_STREET));
        }

        private static RecordComponentElement processedRecordComponentElement() {
            var lensProcessor = new LensProcessor();
            TestUtil.compile(lensProcessor, JavaFileObjects.forSourceString("pack.Address", """
                    package pack;
                
                    @nl.bvkatwijk.lens.Lenses
                    @lombok.With
                    public record Address(String street) { }
                """));
            return lensProcessor
                .elements()
                .head();
        }
    }
}
