package org.bvkatwijk.lens;

@lombok.With
@Lenses
public record Person(String name) implements ApplyLens<Address> {
}
