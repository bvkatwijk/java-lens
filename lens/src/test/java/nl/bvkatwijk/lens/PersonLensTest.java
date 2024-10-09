package nl.bvkatwijk.lens;

import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.bvkatwijk.lens.gen.AddressLens.NUMBER;
import static nl.bvkatwijk.lens.gen.PersonLens.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonLensTest {
    public static final Person ALICE = Instancio.create(Person.class)
        .withName("alice");
    public static final Address HOME = Instancio.create(Address.class);
    public static final Address WORK = Instancio.create(Address.class);
    public static final int HOUSE_NUMBER = 10;

    @Nested
    class Modify {
        @Test
        void nameLens() {
            assertEquals(
                ALICE.withName(ALICE.name().toUpperCase()),
                ALICE.modify(NAME, String::toUpperCase));
        }

        @Test
        void addressLens() {
            assertEquals(
                ALICE.withAddress(HOME),
                ALICE.modify(ADDRESS, i -> HOME));
        }

        @Test
        void addressNumberLens() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.modify(ADDRESS.andThen(NUMBER), i -> HOUSE_NUMBER)
            );
        }

        @Test
        void addressNumberLensComposed() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.modify(NUMBER.compose(ADDRESS), i -> HOUSE_NUMBER)
            );
        }

        @Test
        void friendsTest() {
            assertEquals(
                ALICE.withFriends(List.of()),
                ALICE.modify(FRIENDS, i -> List.of()));
        }
    }

    @Nested
    class Z {
        @Test
        void address() {
            assertEquals(
                ALICE.withAddress(HOME),
                ALICE.with(LensZ.PERSON.address(), HOME));
        }

        @Test
        void addressNumber() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.with(LensZ.PERSON.address().number(), HOUSE_NUMBER));
        }

        @Test
        void addressNumberModify() {
            var address = ALICE.address();
            assertEquals(
                ALICE.withAddress(address.withNumber(address.number() + 1)),
                ALICE.modify(LensZ.PERSON.address().number(), i -> i + 1));
        }

        @Test
        void addressStreet() {
            String newStreet = "new street";
            assertEquals(
                ALICE.withAddress(ALICE.address().withStreet(newStreet)),
                ALICE.with(LensZ.PERSON.address().street(), newStreet));
        }
    }

    @Nested
    class With {
        @Test
        void nameLens() {
            assertEquals(
                ALICE.withName("bob"),
                ALICE.with(NAME, "bob"));
        }

        @Test
        void addressLens() {
            assertEquals(
                ALICE.withAddress(WORK),
                ALICE.with(ADDRESS, WORK));
        }

        @Test
        void addressNumberLens() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.with(ADDRESS.andThen(NUMBER), HOUSE_NUMBER)
            );
        }

        @Test
        void addressNumberLensComposed() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.with(NUMBER.compose(ADDRESS), HOUSE_NUMBER)
            );
        }

        @Test
        void friendsTest() {
            assertEquals(
                ALICE.withFriends(List.of()),
                ALICE.with(FRIENDS, List.of()));
        }
    }
}
