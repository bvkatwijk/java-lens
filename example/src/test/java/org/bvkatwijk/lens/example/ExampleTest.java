package org.bvkatwijk.lens.example;

import net.datafaker.providers.entertainment.NewGirl;
import org.bvkatwijk.lens.Address;
import org.bvkatwijk.lens.User;
import org.bvkatwijk.lens.gen.AddressLens;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleTest {
    public static final Random RANDOM = new Random();
    public static final String NAME = UUID.randomUUID().toString();
    public static final String NAME_NEW = UUID.randomUUID().toString();
    public static final String STREET = UUID.randomUUID().toString();
    public static final Address ADDRESS = new Address(STREET, RANDOM.nextInt());
    public static final int NUMBER_NEW = RANDOM.nextInt();
    public static final User USER = new User(NAME, ADDRESS);
    public static final Address NEW_ADDRESS = new Address(STREET, NUMBER_NEW);

    @Test
    void userAddressNumberReplace() {
        var addressLens = USER.focus(User::address, USER::withAddress);
        assertEquals(
            new User(NAME, NEW_ADDRESS),
            addressLens
                .focus(USER.address()::withNumber, Address::number)
                .replace(NUMBER_NEW)
        );
    }

    @Test
    void userAddressNumberDirectReplace() {
        var addressNumberLens = USER.focus(it -> it.address().number(), it -> USER.withAddress(USER.address().withNumber(it)));
        assertEquals(
            new User(NAME, NEW_ADDRESS),
            addressNumberLens
                .replace(NUMBER_NEW)
        );
    }

    @Test
    void userAddressNameReplace() {
        assertEquals(
            new User(NAME_NEW, ADDRESS),
            USER.focus(User::name, USER::withName)
                .replace(NAME_NEW)
        );
    }

    @Test
    void userAddressNameModify() {
        assertEquals(
            new User(NAME.toUpperCase(), ADDRESS),
            USER.focus(User::name, USER::withName)
                .modify(String::toUpperCase)
        );
    }

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
