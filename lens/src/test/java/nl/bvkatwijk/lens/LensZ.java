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
        public AddressZ<Person> address() {
            return new AddressZ<>(PersonLens.ADDRESS);
        }
    }

    public record AddressZ<T>(Lens<T, Address> inner) implements ILens<T, Address> {
        public ILens<T, Integer> number() {
            return inner.andThen(AddressLens.NUMBER);
        }
        public ILens<T, String> street() {
            return inner.andThen(AddressLens.STREET);
        }

        public CityZ<T> city() {
            return new CityZ<>(inner.andThen(AddressLens.CITY));
        }

        @Override
        public Function2<T, Address, T> with() {
            return inner.with();
        }

        @Override
        public Function1<T, Address> get() {
            return inner.get();
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
