package nl.bvkatwijk.lens.example;

import io.vavr.collection.List;
import nl.bvkatwijk.lens.LensOps;
import nl.bvkatwijk.lens.Lenses;

@lombok.With // Generate with functions (or you can write your own)
@Lenses // Generate Lens helper class
public record Person(String name, Address address, Address work, List<Person> friends, boolean cool)
    implements LensOps<Person> /* (optional) add convenience methods on record */ {
}
