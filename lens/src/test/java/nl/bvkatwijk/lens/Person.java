package nl.bvkatwijk.lens;

import lombok.With;

import java.util.List;

@With
@Lenses
public record Person(String name, Address address, Address work, List<Person> friends) implements LensOps<Person> {
}
