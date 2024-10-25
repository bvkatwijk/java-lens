package nl.bvkatwijk.lens;

import io.vavr.Function1;
import io.vavr.Function2;
import nl.bvkatwijk.lens.gen.AddressLens;
import nl.bvkatwijk.lens.gen.CityLens;
import nl.bvkatwijk.lens.gen.PersonLens;
import nl.bvkatwijk.lens.kind.ILens;
import nl.bvkatwijk.lens.kind.Lens;

public class LensZ {
    public static final PersonZ PERSON = new PersonZ();

    public record PersonZ() {
        public AddressLens<Person> address() {
            return new AddressLens<>(PersonLens.ADDRESS);
        }
    }

    public record CityZ<T>(Lens<T, City> inner) implements ILens<T, City> {
        @Override
        public Function1<T, City> get() {
            return inner.get();
        }

        @Override
        public Function2<T, City, T> with() {
            return inner.with();
        }

        public ILens<T, String> name() {
            return inner.andThen(CityLens.NAME);
        }
    }
}
