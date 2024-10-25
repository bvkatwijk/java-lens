package nl.bvkatwijk.lens;

import nl.bvkatwijk.lens.gen.AddressLens;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddressLensTest {
    public static final Random RANDOM = new Random();
    public static final String STREET = UUID.randomUUID().toString();
    public static final City CITY = City.rand();
    public static final Address ADDRESS = new Address(STREET, RANDOM.nextInt(), CITY);

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
