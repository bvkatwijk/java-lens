package org.bvkatwijk.lens.example;

import lombok.With;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleTest {

    public static final String NAME = "Alice";
    public static final Address ADDRESS = new Address("molslaan", 107);
    public static final User USER = new User(NAME, ADDRESS);
    public static final int NUMBER_NEW = 108;
    public static final Address NEW_ADDRESS = new Address("molslaan", NUMBER_NEW);
    private static final String NAME_NEW = "Bob";

    @With
    public record Address(String street, int number) {
        public <TARGET> Lens<Address, TARGET> focus(Function<Address, TARGET> f, Function<TARGET, Address> with) {
            return new Lens<>(this, with, f);
        }
    }

    @With
    public record User(String name, Address address) {
        public <TARGET> Lens<User, TARGET> focus(Function<User, TARGET> f, Function<TARGET, User> with) {
            return new Lens<>(this, with, f);
        }
    }

    public record Lens<SOURCE, TARGET>(SOURCE source, Function<TARGET, SOURCE> with, Function<SOURCE, TARGET> zoom) {
        public <NEW_TARGET> Lens<SOURCE, NEW_TARGET> focus(
            Function<NEW_TARGET, TARGET> nextWith,
            Function<TARGET, NEW_TARGET> nextZoom
        ) {
            return new Lens<>(source, with.compose(nextWith), zoom.andThen(nextZoom));
        }

        public SOURCE replace(TARGET i) {
            return with.apply(i);
        }

        public SOURCE modify(UnaryOperator<TARGET> f) {
            return with.apply(this.zoom.andThen(f).apply(this.source));
        }
    }

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
    void userAddressNameReplace() {
        assertEquals(
            new User(NAME_NEW, ADDRESS),
            USER.focus(User::name, USER::withName)
                .replace(NAME_NEW)
        );
    }

    @Test
    void addressNumberReplace() {
        assertEquals(
            NEW_ADDRESS,
            ADDRESS.focus(Address::number, ADDRESS::withNumber)
                .replace(NUMBER_NEW)
        );
    }

    @Test
    void addressNumberModify() {
        assertEquals(
            NEW_ADDRESS,
            ADDRESS.focus(Address::number, ADDRESS::withNumber)
                .modify(i -> i + 1)
        );
    }
}
