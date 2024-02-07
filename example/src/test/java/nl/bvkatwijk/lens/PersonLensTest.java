package nl.bvkatwijk.lens;

import nl.bvkatwijk.lens.gen.AddressLens;
import nl.bvkatwijk.lens.gen.PersonLens;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PersonLensTest {
    public static final Person PERSON = Instancio.create(Person.class);
    public static final Address ADDRESS = Instancio.create(Address.class);
    public static final Address NEW_ADDRESS = Instancio.create(Address.class);
    public static final int NUMBER = new Random().nextInt();

    @Nested
    class Modify {
        @Test
        void nameLens() {
            assertEquals(
                PERSON.withName(PERSON.name().toUpperCase()),
                PERSON.modify(PersonLens.NAME, String::toUpperCase));
        }

        @Test
        void addressLens() {
            assertEquals(
                PERSON.withAddress(ADDRESS),
                PERSON.modify(PersonLens.ADDRESS, i -> ADDRESS));
        }

        @Test
        void addressNumberLens() {
            assertEquals(
                PERSON.withAddress(PERSON.address().withNumber(NUMBER)),
                PERSON.modify(PersonLens.ADDRESS.andThen(AddressLens.NUMBER), i -> NUMBER)
            );
        }

        @Test
        void addressNumberLensComposed() {
            assertEquals(
                PERSON.withAddress(PERSON.address().withNumber(NUMBER)),
                PERSON.modify(AddressLens.NUMBER.compose(PersonLens.ADDRESS), i -> NUMBER)
            );
        }

        @Test
        void friendsTest() {
            assertEquals(
                PERSON.withFriends(List.of()),
                PERSON.modify(PersonLens.FRIENDS, i -> List.of()));
        }
    }

    @Nested
    class With {

        @Test
        void nameLens() {
            assertEquals(
                PERSON.withName("bob"),
                PERSON.with(PersonLens.NAME, "bob"));
        }

        @Test
        void addressLens() {
            assertEquals(
                PERSON.withAddress(NEW_ADDRESS),
                PERSON.with(PersonLens.ADDRESS, NEW_ADDRESS));
        }

        @Test
        void addressNumberLens() {
            assertEquals(
                PERSON.withAddress(PERSON.address().withNumber(NUMBER)),
                PERSON.with(PersonLens.ADDRESS.andThen(AddressLens.NUMBER), NUMBER)
            );
        }

        @Test
        void addressNumberLensComposed() {
            assertEquals(
                PERSON.withAddress(PERSON.address().withNumber(NUMBER)),
                PERSON.with(AddressLens.NUMBER.compose(PersonLens.ADDRESS), NUMBER)
            );
        }

        @Test
        void friendsTest() {
            assertEquals(
                PERSON.withFriends(List.of()),
                PERSON.with(PersonLens.FRIENDS, List.of()));
        }
    }
}
