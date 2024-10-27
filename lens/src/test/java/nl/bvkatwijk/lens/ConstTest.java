package nl.bvkatwijk.lens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstTest {
    @Test
    void qualified_lenses_path() {
        assertEquals(Lenses.class.getName(), Const.LENS_ANNOTATION_QUALIFIED);
    }
}
