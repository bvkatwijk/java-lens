package nl.bvkatwijk.lens.types;

import lombok.With;
import nl.bvkatwijk.lens.Lenses;
import nl.bvkatwijk.lens.gen.BoolRecLens;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeTest {

    @With
    @Lenses
    public record BoolRec(boolean bool) { }

    @Test
    void booleanLens() {
        assertEquals(true, BoolRecLens.BOOL.with(true).apply(new BoolRec(false)).bool());
    }
}
