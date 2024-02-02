package org.bvkatwijk.lens;

@lombok.With
@Lenses
public record Person(String name, Address address) implements ApplyLens<Person> {
}
