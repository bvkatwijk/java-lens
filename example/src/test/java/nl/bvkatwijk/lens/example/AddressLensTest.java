package nl.bvkatwijk.lens.example;

import nl.bvkatwijk.lens.Address;
import nl.bvkatwijk.lens.gen.AddressLens;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddressLensTest {
    public static final Random RANDOM = new Random();
    public static final String STREET = UUID.randomUUID().toString();
    public static final Address ADDRESS = new Address(STREET, RANDOM.nextInt());

    @Test
    void addressNumberModify() {
        assertEquals(
            new Address(STREET, ADDRESS.number() + 1),
            ADDRESS.modify(AddressLens.NUMBER, i -> i + 1)
        );
    }

    @Test
    void addressStreetModify() {
        assertEquals(
            new Address(STREET.toUpperCase(), ADDRESS.number()),
            ADDRESS.modify(AddressLens.STREET, String::toUpperCase)
        );
    }

    @Test
    void addressBothStreetAndNumberModify() {
        assertEquals(
            new Address(STREET.toUpperCase(), ADDRESS.number()),
            ADDRESS.modify(AddressLens.STREET, String::toUpperCase)
        );
    }
}
