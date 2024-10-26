package nl.bvkatwijk.lens.kind;

import io.vavr.Function1;
import io.vavr.Function2;

public record Lens<S, T>(Function2<S, T, S> with, Function1<S, T> get) implements ILens<S, T> {
    public static <T> Lens<T, T> identity() {
        return new Lens<>((a, b) -> b, Function1.identity());
    }
}
