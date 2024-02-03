package org.bvkatwijk.lens;

import lombok.With;

@With
@Lenses
public record Person(String name, Address address) implements ApplyLens<Person> {
}
