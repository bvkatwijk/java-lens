package nl.bvkatwijk.lens.example;

import nl.bvkatwijk.lens.LensOps;
import nl.bvkatwijk.lens.Lenses;

@lombok.With
@Lenses
public record Address(String street, int number, City city) implements LensOps<Address> {

}
