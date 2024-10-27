package nl.bvkatwijk.lens.kind;

import io.vavr.Function1;

import java.util.function.BiFunction;
import java.util.function.Function;

public record Lens<S, T>(BiFunction<S, T, S> with, Function<S, T> get) implements ILens<S, T> {
    public static <T> Lens<T, T> identity() {
        return new Lens<>((a, b) -> b, Function1.identity());
    }
}
