package nl.bvkatwijk.lens;

@lombok.With
@Lenses
public record Address(String street, int number) implements LensOps<Address> {
}
