package org.bvkatwijk.lens.example;

import org.bvkatwijk.lens.Address;
import org.bvkatwijk.lens.gen.AddressLens;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleTest {
    public static final Random RANDOM = new Random();
    public static final String STREET = UUID.randomUUID().toString();
    public static final Address ADDRESS = new Address(STREET, RANDOM.nextInt());
    public static final int NUMBER_NEW = RANDOM.nextInt();

    @Test
    void addressNumberModify() {
        assertEquals(
            new Address(STREET, ADDRESS.number() + 1),
            ADDRESS.with(AddressLens.NUMBER, i -> i + 1)
        );
    }

    @Test
    void addressStreetModify() {
        assertEquals(
            new Address(STREET.toUpperCase(), ADDRESS.number()),
            ADDRESS.with(AddressLens.STREET, String::toUpperCase)
        );
    }

    @Test
    void addressBothStreetAndNumberModify() {
        assertEquals(
            new Address(STREET.toUpperCase(), ADDRESS.number()),
            ADDRESS.with(AddressLens.STREET, String::toUpperCase)
        );
    }
}
