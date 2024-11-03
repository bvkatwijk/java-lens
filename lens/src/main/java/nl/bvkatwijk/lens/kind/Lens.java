package nl.bvkatwijk.lens.kind;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Basic implementation of {@link ILens}
 * @param get getter function
 * @param with wither function
 * @param <S> Source type
 * @param <T> Target type
 */
public record Lens<S, T>(Function<S, T> get, BiFunction<S, T, S> with) implements ILens<S, T> {
    public static <T> Lens<T, T> identity() {
        return new Lens<>(Function.identity(), (a, b) -> b);
    }
}
