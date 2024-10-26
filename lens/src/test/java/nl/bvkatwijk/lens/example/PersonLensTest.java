package nl.bvkatwijk.lens.example;

import nl.bvkatwijk.lens.LensOps;
import nl.bvkatwijk.lens.Lenses;
import nl.bvkatwijk.lens.gen.AddressLens;
import nl.bvkatwijk.lens.gen.PersonLens;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static nl.bvkatwijk.lens.gen.AddressLens.NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonLensTest {
    @lombok.With // Generate with functions (or you can write your own)
    @Lenses // Generate Lens helper class
    public record Person(String name, Address address, Address work, List<Person> friends)
        implements LensOps<Person> /* (optional) add convenience methods on record */ {
    }

    @lombok.With
    @Lenses
    public record Address(String street, int number, City city) implements LensOps<Address> {

    }

    @lombok.With
    @Lenses
    public record City(String name) {
    }

    @Nested
    class ReadmeExamples {
        @Test
        void lenses_are_objects() {
            /// A Lens is an immutable object
            var nameLens = PersonLens.NAME;
        }

        @Test
        void use_with_to_transform() {
            /// Using Lens#with you get a UnaryOperator
            var renameToBob = PersonLens.NAME.with("bob");

            /// You can apply this to instances to perform internal transformations
            assertEquals("bob", renameToBob.apply(ALICE).name());
        }

        @Test
        void use_chain_to_deep_transform() {
            /// Using ROOT you can call method chain to get deep lenses
            var moveToNewYork = PersonLens.ROOT.address().city().name().with("New York");

            /// This can be useful to transform a value deep within nested records
            assertEquals("New York", moveToNewYork.apply(ALICE).address().city().name());
        }
    }

    @Nested
    class Modify {
        @Test
        void nameLens() {
            assertEquals(
                ALICE.withName(ALICE.name().toUpperCase()),
                ALICE.modify(PersonLens.NAME, String::toUpperCase));
        }

        @Test
        void addressLens() {
            assertEquals(
                ALICE.withAddress(HOME),
                ALICE.modify(PersonLens.ADDRESS, i -> HOME));
        }

        @Test
        void workLens() {
            assertEquals(
                ALICE.withWork(HOME),
                ALICE.modify(PersonLens.WORK, i -> HOME));
        }

        @Test
        void addressNumberLens() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.modify(PersonLens.ADDRESS.andThen(NUMBER), i -> HOUSE_NUMBER)
            );
        }

        @Test
        void addressNumberLensComposed() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.modify(AddressLens.NUMBER.compose(PersonLens.ADDRESS), i -> HOUSE_NUMBER)
            );
        }

        @Test
        void friendsTest() {
            assertEquals(
                ALICE.withFriends(List.of()),
                ALICE.modify(PersonLens.FRIENDS, i -> List.of()));
        }
    }

    @Nested
    class Z {
        @Test
        void address() {
            assertEquals(
                ALICE.withAddress(HOME),
                ALICE.with(PersonLens.ROOT.address(), HOME));
        }

        @Test
        void addressNumber() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.with(PersonLens.ROOT.address().number(), HOUSE_NUMBER));
        }

        @Test
        void addressNumberModify() {
            var address = ALICE.address();
            assertEquals(
                ALICE.withAddress(address.withNumber(address.number() + 1)),
                ALICE.modify(PersonLens.ROOT.address().number(), i -> i + 1));
        }

        @Test
        void addressStreet() {
            String newStreet = "new street";
            assertEquals(
                ALICE.withAddress(ALICE.address().withStreet(newStreet)),
                ALICE.with(PersonLens.ROOT.address().street(), newStreet));
        }

        @Test
        void addressCityName() {
            String newCity = "new city";
            assertEquals(
                ALICE.withAddress(ALICE.address().withCity(new City(newCity))),
                ALICE.with(PersonLens.ROOT.address().city().name(), newCity));
        }
    }

    @Nested
    class With {
        @Test
        void nameLens() {
            assertEquals(
                ALICE.withName("bob"),
                ALICE.with(PersonLens.NAME, "bob"));
        }

        @Test
        void addressLens() {
            assertEquals(
                ALICE.withAddress(WORK),
                ALICE.with(PersonLens.ADDRESS, WORK));
        }

        @Test
        void addressNumberLens() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.with(PersonLens.ADDRESS.andThen(NUMBER), HOUSE_NUMBER)
            );
        }

        @Test
        void addressNumberLensComposed() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.with(NUMBER.compose(PersonLens.ADDRESS), HOUSE_NUMBER)
            );
        }

        @Test
        void friendsTest() {
            assertEquals(
                ALICE.withFriends(List.of(BOB)),
                ALICE.with(PersonLens.FRIENDS, List.of(BOB)));
        }
    }

    @Nested
    class AddressTest {

        @Test
        void addressNumberModify() {
            assertEquals(
                new Address(STREET, ADDRESS.number() + 1, CITY),
                ADDRESS.modify(AddressLens.NUMBER, i -> i + 1)
            );
        }

        @Test
        void addressStreetModify() {
            assertEquals(
                new Address(STREET.toUpperCase(), ADDRESS.number(), CITY),
                ADDRESS.modify(AddressLens.STREET, String::toUpperCase)
            );
        }

        @Test
        void addressBothStreetAndNumberModify() {
            assertEquals(
                new Address(STREET.toUpperCase(), ADDRESS.number(), CITY),
                ADDRESS.modify(AddressLens.STREET, String::toUpperCase)
            );
        }

        @Nested
        class WithTests {
            @Test
            void with_and_apply() {
                assertEquals(
                    new Address(STREET, 2, CITY),
                    AddressLens.NUMBER.with().apply(ADDRESS, 2)
                );
            }

            @Test
            void with_target_and_apply() {
                assertEquals(
                    new Address(STREET, 2, CITY),
                    AddressLens.NUMBER.with(2).apply(ADDRESS)
                );
            }
        }

        @Nested
        class ModifyTests {
            @Test
            void modify_and_apply() {
                assertEquals(
                    new Address(STREET, ADDRESS.number() + 1, CITY),
                    AddressLens.NUMBER.modify().apply(ADDRESS, i -> i + 1)
                );
            }

            @Test
            void modify_target_and_apply() {
                assertEquals(
                    new Address(STREET, ADDRESS.number() + 1, CITY),
                    AddressLens.NUMBER.modify(i -> i + 1).apply(ADDRESS)
                );
            }
        }
    }

    public static final Person ALICE = Instancio.create(Person.class)
        .withName("alice");
    public static final Person BOB = Instancio.create(Person.class)
        .withName("bob");
    public static final Address HOME = Instancio.create(Address.class);
    public static final Address WORK = Instancio.create(Address.class);
    public static final int HOUSE_NUMBER = 10;
    public static final Random RANDOM = new Random();
    public static final String STREET = UUID.randomUUID().toString();
    public static final City CITY = Instancio.create(City.class);
    public static final Address ADDRESS = new Address(STREET, RANDOM.nextInt(), CITY);
}
