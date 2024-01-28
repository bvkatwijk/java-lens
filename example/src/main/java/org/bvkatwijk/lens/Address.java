package org.bvkatwijk.lens;

import lombok.With;

import java.util.function.Function;

@With
@Lens
public record Address(String street, int number) {
    public <TARGET> IFocus<Address, TARGET> focus(Function<Address, TARGET> f, Function<TARGET, Address> with) {
        return new Focus<>(this, with, f);
    }
}
