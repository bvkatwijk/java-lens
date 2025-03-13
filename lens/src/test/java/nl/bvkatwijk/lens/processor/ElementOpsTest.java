package nl.bvkatwijk.lens.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.RecordComponentElement;

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
            assertThat(Compiler.javac()
                .withProcessors(lensProcessor)
                .compile(JavaFileObjects.forSourceString("pack.Address", """
                    package pack;
                
                    @nl.bvkatwijk.lens.Lenses
                    public record Address(String street) {
                        public Address withStreet(String newName) {
                            return new Address(newName);
                        }
                    }
                """))).succeeded();
            return lensProcessor
                .elements()
                .head();
        }
    }
}
