package nl.bvkatwijk.lens.example;

import io.vavr.collection.List;
import nl.bvkatwijk.lens.LensOps;
import nl.bvkatwijk.lens.Lenses;
import nl.bvkatwijk.lens.gen.AddressLens;
import nl.bvkatwijk.lens.gen.PersonLens;
import nl.bvkatwijk.lens.kind.ILens;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static nl.bvkatwijk.lens.gen.AddressLens.NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleTest {
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

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("Readme Examples")
    class ReadmeExamples {
        @Test
        void lenses_are_objects() {
            /// A Lens is an immutable object
            ILens<Person, String> nameLens = PersonLens.NAME;
        }

        @Test
        void use_with_to_transform() {
            /// Using Lens#with you get a UnaryOperator
            UnaryOperator<Person> renameToBob = PersonLens.NAME.with("bob");

            /// You can apply this to instances to perform internal transformations
            assertEquals("bob", renameToBob.apply(ALICE).name());
        }

        @Test
        void use_chain_to_deep_transform() {
            /// Using ROOT you can call method chain to get deep lenses
            UnaryOperator<Person> moveToNewYork = PersonLens.ROOT.address().city().name().with("New York");

            /// This can be useful to transform a value deep within nested records
            assertEquals("New York", moveToNewYork.apply(ALICE).address().city().name());
        }

        @Test
        void use_lens_ops_to_perform_multiple_transformations() {
            /// Adding LensOps interface can be useful to apply multiple transformations on a single instance
            ALICE
                .with(PersonLens.NAME, "SuperAlice")
                .modify(PersonLens.FRIENDS, friends -> friends.append(BOB));
        }
    }

    @Nested
    @DisplayName("Modify")
    class ModifyTest {
        @Test
        void name() {
            assertEquals(
                ALICE.withName(ALICE.name().toUpperCase()),
                ALICE.modify(PersonLens.NAME, String::toUpperCase));
        }

        @Test
        void address() {
            assertEquals(
                ALICE.withAddress(HOME),
                ALICE.modify(PersonLens.ADDRESS, i -> HOME));
        }

        @Test
        void work() {
            assertEquals(
                ALICE.withWork(HOME),
                ALICE.modify(PersonLens.WORK, i -> HOME));
        }

        @Test
        void address_number() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.modify(PersonLens.ADDRESS.andThen(NUMBER), i -> HOUSE_NUMBER)
            );
        }

        @Test
        void address_number_composed() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.modify(AddressLens.NUMBER.compose(PersonLens.ADDRESS), i -> HOUSE_NUMBER)
            );
        }

        @Test
        void friends() {
            assertEquals(
                ALICE.withFriends(List.of(BOB)),
                ALICE.modify(PersonLens.FRIENDS, i -> List.of(BOB)));
        }
    }

    @Nested
    @DisplayName("Root")
    class RootTest {
        @Test
        void address() {
            assertEquals(
                ALICE.withAddress(HOME),
                ALICE.with(PersonLens.ROOT.address(), HOME));
        }

        @Test
        void address_number() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.with(PersonLens.ROOT.address().number(), HOUSE_NUMBER));
        }

        @Test
        void address_number_modify() {
            var address = ALICE.address();
            assertEquals(
                ALICE.withAddress(address.withNumber(address.number() + 1)),
                ALICE.modify(PersonLens.ROOT.address().number(), i -> i + 1));
        }

        @Test
        void address_street() {
            String newStreet = "new street";
            assertEquals(
                ALICE.withAddress(ALICE.address().withStreet(newStreet)),
                ALICE.with(PersonLens.ROOT.address().street(), newStreet));
        }

        @Test
        void address_city_name() {
            String newCity = "new city";
            assertEquals(
                ALICE.withAddress(ALICE.address().withCity(new City(newCity))),
                ALICE.with(PersonLens.ROOT.address().city().name(), newCity));
        }
    }

    @Nested
    @DisplayName("With")
    class WithTest {
        @Test
        void name() {
            assertEquals(
                ALICE.withName("bob"),
                ALICE.with(PersonLens.NAME, "bob"));
        }

        @Test
        void address() {
            assertEquals(
                ALICE.withAddress(WORK),
                ALICE.with(PersonLens.ADDRESS, WORK));
        }

        @Test
        void address_number() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.with(PersonLens.ADDRESS.andThen(NUMBER), HOUSE_NUMBER)
            );
        }

        @Test
        void address_number_lens_composed() {
            assertEquals(
                ALICE.withAddress(ALICE.address().withNumber(HOUSE_NUMBER)),
                ALICE.with(NUMBER.compose(PersonLens.ADDRESS), HOUSE_NUMBER)
            );
        }

        @Test
        void friends() {
            assertEquals(
                ALICE.withFriends(List.of(BOB)),
                ALICE.with(PersonLens.FRIENDS, List.of(BOB)));
        }
    }

    @Nested
    @DisplayName("Address")
    class AddressTest {
        @Test
        void modify_number() {
            assertEquals(
                new Address(STREET, ADDRESS.number() + 1, CITY),
                ADDRESS.modify(AddressLens.NUMBER, i -> i + 1)
            );
        }

        @Test
        void modify_street() {
            assertEquals(
                new Address(STREET.toUpperCase(), ADDRESS.number(), CITY),
                ADDRESS.modify(AddressLens.STREET, String::toUpperCase)
            );
        }

        @Test
        void modify_street_and_number() {
            assertEquals(
                new Address(STREET.toUpperCase(), ADDRESS.number() + 1, CITY),
                ADDRESS.modify(AddressLens.STREET, String::toUpperCase)
                    .modify(AddressLens.NUMBER, i -> i + 1)
            );
        }

        @Nested
        @DisplayName("With")
        class WithTests {
            int newNumber = 2;
            Address newAddress = new Address(STREET, newNumber, CITY);

            @Test
            void with_t_s() {
                assertEquals(newAddress, AddressLens.NUMBER.with().apply(ADDRESS, newNumber));
            }

            @Test
            void with_t_apply_s() {
                assertEquals(newAddress, AddressLens.NUMBER.with(newNumber).apply(ADDRESS));
            }

            @Test
            void with_apply_t_s() {
                assertEquals(newAddress, AddressLens.NUMBER.with(ADDRESS, newNumber));
            }
        }

        @Nested
        @DisplayName("Modify")
        class ModifyTests {
            Address expected = new Address(STREET, ADDRESS.number() + 1, CITY);

            @Test
            void modify_apply_s_f_t() {
                assertEquals(expected, NUMBER.modify().apply(ADDRESS, i -> i + 1));
            }

            @Test
            void modify_f_t_apply_s() {
                assertEquals(expected, NUMBER.modify(i -> i + 1).apply(ADDRESS));
            }
        }
    }

    public static final Person ALICE = Instancio.create(Person.class)
        .withName("alice")
        .withFriends(List.of());
    public static final Person BOB = Instancio.create(Person.class)
        .withName("bob")
        .withFriends(List.of());
    public static final Address HOME = Instancio.create(Address.class);
    public static final Address WORK = Instancio.create(Address.class);
    public static final int HOUSE_NUMBER = 10;
    public static final Random RANDOM = new Random();
    public static final String STREET = UUID.randomUUID().toString();
    public static final City CITY = Instancio.create(City.class);
    public static final Address ADDRESS = new Address(STREET, RANDOM.nextInt(), CITY);
}
