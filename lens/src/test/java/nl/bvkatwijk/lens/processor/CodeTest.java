package nl.bvkatwijk.lens.processor;

import nl.bvkatwijk.lens.Const;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeTest {
    @Nested
    @DisplayName("Reference")
    class MethodReferenceTest {
        @Test
        void generate() {
            assertEquals("A::b", Code.reference("A", "b"));
        }
    }

    @Nested
    @DisplayName("Import")
    class ImportStatementTest {
        @Test
        void generate() {
            assertEquals("import a.B;", Code.importStatement("a.B"));
        }
    }

    @Nested
    @DisplayName("Unqualify")
    class UnqualifyTest {
        @Test
        void generate() {
            assertEquals("C", Code.unqualify("a.b.C"));
        }
    }

    @Nested
    @DisplayName("Remove Generics")
    class RemoveGenericsTest{
        @Test
        void generate() {
            assertEquals("A", Code.removeGenerics("A<B, C<D>>"));
        }
    }
}
