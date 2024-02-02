package org.bvkatwijk.lens;

import org.bvkatwijk.lens.kind.Lens;

import java.util.function.Function;

@lombok.With
@Lenses
public record Address(String street, int number) implements With<Address> {
    public static final Lens<Address, Integer> NUMBER = new Lens<>(Address::withNumber, Address::number);
    public static final Lens<Address, String> STREET = new Lens<>(Address::withStreet, Address::street);

    public <TARGET> IFocus<Address, TARGET> focus(Function<Address, TARGET> f, Function<TARGET, Address> with) {
        return new Focus<>(this, with, f);
    }
}
