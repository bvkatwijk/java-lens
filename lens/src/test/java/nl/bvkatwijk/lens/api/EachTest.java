package nl.bvkatwijk.lens.api;

import io.vavr.Value;
import io.vavr.collection.List;
import nl.bvkatwijk.lens.Lenses;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EachTest {
    @Lenses
    record WrapValue(Value<String> wrapped) {
    }

    @Test
    void wrap() {
        var it = new WrapValue(List.of("some"));
        Assertions.assertEquals("SOME", WrapValueLens.µ
            .wrapped()
            .modify(e -> e.map(String::toUpperCase))
            .apply(it)
            .wrapped()
            .get());
    }
}
