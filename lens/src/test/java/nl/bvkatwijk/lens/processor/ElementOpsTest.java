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
        public static final RecordComponentElement ELEMENT = processedRecordComponentElement();

        @Test
        void unqualifiedTypeName() {
            assertEquals("String", ElementOps.unqualifiedTypeName(ELEMENT));
        }

        @Test
        void packageElement() {
            assertEquals("java.lang", ElementOps.packageElement(ELEMENT));
        }

        @Test
        void fieldName() {
            assertEquals("street", ElementOps.fieldName(ELEMENT));
        }
    }

    private static RecordComponentElement processedRecordComponentElement() {
        LensProcessor lensProcessor = new LensProcessor();
        Compilation compilation = Compiler.javac()
            .withProcessors(
                lensProcessor)
            .compile(JavaFileObjects.forSourceString("pack.Address", """
                    package pack;
                
                    @nl.bvkatwijk.lens.Lenses
                    public record Address(String street) {
                        public Address withStreet(String newName) {
                            return new Address(newName);
                        }
                    }
                """));
        assertThat(compilation).succeeded();
        return lensProcessor
            .elements()
            .head();
    }
}
