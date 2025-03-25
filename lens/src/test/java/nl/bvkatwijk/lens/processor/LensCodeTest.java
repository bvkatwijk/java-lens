package nl.bvkatwijk.lens.processor;

import nl.bvkatwijk.lens.Const;
import org.approvaltests.Approvals;
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

    @Nested
    class LensConstant {
        @Test
        void works() {
            assertEquals(
                "public static final ILens<Person, Boolean> COOL = new Lens<>(Person::cool, PersonLens::withCool);",
                LensCode.lensConstant("Person", "cool", "Boolean"));
        }
    }

    @Nested
    class DelegateGet {
        @Test
        void approve() {
            Approvals.verify(Code.render(LensCode.delegateGet("TypeName")));
        }
    }

    @Nested
    class DelegateWith {
        @Test
        void approve() {
            Approvals.verify(Code.render(LensCode.delegateWith("TypeName")));
        }
    }
}
