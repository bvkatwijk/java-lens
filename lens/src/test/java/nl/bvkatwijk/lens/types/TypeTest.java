package nl.bvkatwijk.lens.types;

import lombok.With;
import nl.bvkatwijk.lens.Lenses;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeTest {
    @Nested
    class BoolTest {
        @With
        @Lenses
        public record BoolRec(boolean bool) { }

        @Test
        void booleanLens() {
            assertEquals(true, nl.bvkatwijk.lens.types.BoolRecLens.µ.bool().with(true).apply(new BoolRec(false)).bool());
        }
    }
}
