package nl.bvkatwijk.lens;

import nl.bvkatwijk.lens.gen.AddressLens;
import nl.bvkatwijk.lens.gen.PersonLens;

public class LensZ {
    public static final PersonZ PERSON = new PersonZ();

    public record PersonZ() {
        public AddressLens<Person> address() {
            return new AddressLens<>(PersonLens.ADDRESS);
        }
    }
}
