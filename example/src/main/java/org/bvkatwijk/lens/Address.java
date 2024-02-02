package org.bvkatwijk.lens;

@lombok.With
@Lenses
public record Address(String street, int number) implements ApplyLens<Address> {
}
