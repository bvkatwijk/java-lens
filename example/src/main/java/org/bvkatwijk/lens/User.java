package org.bvkatwijk.lens;

import lombok.With;

import java.util.function.Function;

@With
public record User(String name, Address address) {
    public <TARGET> Focus<User, TARGET> focus(Function<User, TARGET> f, Function<TARGET, User> with) {
        return new Focus<>(this, with, f);
    }
}
