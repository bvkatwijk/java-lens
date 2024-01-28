package org.bvkatwijk.lens.example;

import lombok.With;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleTest {

    public static final String NAME = "Alice";
    public static final Address ADDRESS = new Address("molslaan", 107);
    public static final User USER = new User(NAME, ADDRESS);
    public static final int NUMBER_NEW = 108;
    public static final Address NEW_ADDRESS = new Address("molslaan", NUMBER_NEW);
    private static final String NAME_NEW = "Bob";

    @With
    public record Address(String street, int number) { }

    @With
    public record User(String name, Address address) {
        public <TARGET> X<TARGET> focus(Function<User, TARGET> f, Function<TARGET, User> with) {
            return new X<>(this, with, f);
        }
    }

    public record X<TARGET>(User user, Function<TARGET, User> withX, Function<User, TARGET> f) {
        public <NEW_TARGET> X<NEW_TARGET> focus(
            Function<NEW_TARGET, TARGET> with,
            Function<TARGET, NEW_TARGET> number
        ) {
            return new X<>(user, withX.compose(with), f.andThen(number));
        }

        public User replace(TARGET i) {
            return withX.apply(i);
        }
    }

    @Test
    void lens() {
        var addressLens = USER.focus(User::address, USER::withAddress);
        assertEquals(
            new User(NAME, NEW_ADDRESS),
            addressLens
                .focus(USER.address()::withNumber, Address::number)
                .replace(NUMBER_NEW)
        );
    }

    @Test
    void lensName() {
        assertEquals(
            new User(NAME_NEW, ADDRESS),
            USER.focus(User::name, USER::withName)
                .replace(NAME_NEW)
        );
    }
}
