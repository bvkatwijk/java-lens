package nl.bvkatwijk.lens.processor;

import nl.bvkatwijk.lens.Const;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeTest {
    @Nested
    class MethodReferenceTest {
        @Test
        void generate() {
            assertEquals("A::b", Code.reference("A", "b"));
        }
    }

    @Nested
    class ImportStatementTest {
        @Test
        void generate() {
            assertEquals("import a.B;", Code.importStatement("a.B"));
        }
    }

    @Nested
    class ILens {
        @Test
        void generate() {
            assertEquals("ILens<A, B>", Code.iLens("A", "B"));
        }

        @Test
        void generate_const() {
            assertEquals("ILens<" + Const.PARAM_SOURCE_TYPE + ", A>", Code.iLens("A"));
        }
    }
}