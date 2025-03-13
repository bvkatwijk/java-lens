package nl.bvkatwijk.lens.processor;

import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldLensTest {
    public static final FieldLens FIELD_LENS = Instancio.create(FieldLens.class);

    @Test
    void typeLens() {
        assertEquals(FIELD_LENS.pack() + "." + Code.unqualify(FIELD_LENS.qualifiedType()) + "Lens", FIELD_LENS.typeLens());
    }

    @Nested
    class LensMethod {
        @Test
        void primitive() {
            FieldLens lens = FIELD_LENS.withLensKind(LensKind.PRIMITIVE);
            assertEquals("public ILens<SOURCE, " + lens.qualifiedType() + "> " + lens.fieldName() + "() {\treturn inner.andThen(" + lens.fieldName() + ");}",
                lens.lensMethod().mkString());
        }

        @Test
        void other() {
            FieldLens lens = FIELD_LENS.withLensKind(LensKind.OTHER);
            assertEquals("public ILens<SOURCE, " + lens.qualifiedType() + "> " + lens.fieldName() + "() {\treturn inner.andThen(" + lens.fieldName() + ");}",
                lens.lensMethod().mkString());
        }
    }
}
