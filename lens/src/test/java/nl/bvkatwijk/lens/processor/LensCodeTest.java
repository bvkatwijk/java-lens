package nl.bvkatwijk.lens.processor;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LensCodeTest {
    @Nested
    class RootLens {
        @Test
        void test() {
            assertEquals(
                    "\tpublic static final bLens<b> µ = new bLens<>(Lens.identity());",
                    LensCode.rootLens("b"));
        }
    }

    @Nested
    class WitherName {
        @Test
        void test() {
            assertEquals(
                    "withFieldName",
                    LensCode.witherName("fieldName"));
        }
    }
}
