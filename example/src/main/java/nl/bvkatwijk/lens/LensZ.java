package nl.bvkatwijk.lens;

import io.vavr.Function1;
import io.vavr.Function2;
import nl.bvkatwijk.lens.gen.AddressLens;
import nl.bvkatwijk.lens.gen.PersonLens;
import nl.bvkatwijk.lens.kind.ILens;
import nl.bvkatwijk.lens.kind.Lens;

public class LensZ {
    public static final PersonZ PERSON = new PersonZ();

    public record PersonZ() {
        public PersonAddressZ address() {
            return new PersonAddressZ(PersonLens.ADDRESS);
        }
    }

    public record PersonAddressZ(Lens<Person, Address> personZ) implements ILens<Person, Address> {
        public ILens<Person, Integer> number() {
            return personZ.andThen(AddressLens.NUMBER);
        }
        public ILens<Person, String> street() {
            return personZ.andThen(AddressLens.STREET);
        }

        @Override
        public Function2<Person, Address, Person> with() {
            return personZ.with();
        }

        @Override
        public Function1<Person, Address> get() {
            return personZ.get();
        }
    }
}
