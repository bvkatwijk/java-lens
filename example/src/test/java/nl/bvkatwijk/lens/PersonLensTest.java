package nl.bvkatwijk.lens;

import nl.bvkatwijk.lens.gen.PersonLens;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonLensTest {
    public static final Person PERSON = Instancio.create(Person.class);
    public static final Address ADDRESS = Instancio.create(Address.class);

    @Test
    void nameLens() {
        assertEquals(
            PERSON.withName(PERSON.name().toUpperCase()),
            PERSON.with(PersonLens.NAME, String::toUpperCase));
    }

    @Test
    void addressLens() {
        assertEquals(
            PERSON.withAddress(ADDRESS),
            PERSON.with(PersonLens.ADDRESS, i -> ADDRESS));
    }

    @Test
    void friendsTest() {
        assertEquals(
            PERSON.withFriends(List.of()),
            PERSON.with(PersonLens.FRIENDS, i -> List.of()));
    }
}
