package nl.bvkatwijk.lens.api;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import nl.bvkatwijk.lens.Lenses;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static nl.bvkatwijk.lens.api.Each.*;

class EachTest {

    @Lenses
    record WrapList(List<String> wrapped) {
    }

    @Test
    void wrapList() {
        Assertions.assertEquals("A", WrapListLens.µ
            .wrapped()
            .modify(mapEach(String::toUpperCase))
            .apply(new WrapList(List.of("a")))
            .wrapped()
            .head());
    }

    @Lenses
    record WrapSet(Set<String> wrapped) {
    }

    @Test
    void wrapSet() {
        Assertions.assertEquals("A", WrapSetLens.µ
            .wrapped()
            .modify(mapEach(String::toUpperCase))
            .apply(new WrapSet(HashSet.of("a")))
            .wrapped()
            .head());
    }
}
