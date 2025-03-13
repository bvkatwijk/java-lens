package nl.bvkatwijk.lens.processor;

import nl.bvkatwijk.lens.Const;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LensCodeTest {
    @Nested
    class RootLens {
        @Test
        void test() {
            assertEquals(
                    "public static final bLens<b> µ = new bLens<>(Lens.identity());",
                    LensCode.rootLens("b"));
        }
    }

    @Nested
    @DisplayName("ILens")
    class ILensTest {
        @Test
        void generate() {
            assertEquals("ILens<A, B>", LensCode.iLens("A", "B"));
        }

        @Test
        void generate_const() {
            assertEquals("ILens<" + Const.PARAM_SOURCE_TYPE + ", A>", LensCode.iLens("A"));
        }
    }

    @Nested
    class WitherName {
        @Test
        void name() {
            assertEquals(
                "withName",
                LensCode.witherName("name"));
        }

        @Test
        void fieldName() {
            assertEquals(
                    "withFieldName",
                    LensCode.witherName("fieldName"));
        }
    }

    @Nested
    class LensName {
        @Test
        void name() {
            assertEquals("NAME", LensCode.lensName("name"));
        }

        @Test
        void lensName() {
            assertEquals("LENS_NAME", LensCode.lensName("lensName"));
        }

        @Test
        void anotherLensName() {
            assertEquals("ANOTHER_LENS_NAME", LensCode.lensName("anotherLensName"));
        }
    }
}
