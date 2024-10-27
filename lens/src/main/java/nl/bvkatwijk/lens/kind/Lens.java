package nl.bvkatwijk.lens.kind;

import java.util.function.BiFunction;
import java.util.function.Function;

public record Lens<S, T>(Function<S, T> get, BiFunction<S, T, S> with) implements ILens<S, T> {
    public static <T> Lens<T, T> identity() {
        return new Lens<>(Function.identity(), (a, b) -> b);
    }
}
