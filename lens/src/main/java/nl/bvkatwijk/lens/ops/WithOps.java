package nl.bvkatwijk.lens.ops;

import nl.bvkatwijk.lens.api.ILens;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * Operations for setting a new value
 * @param <S> Source Type
 * @param <T> Target Type
 */
public interface WithOps<S, T> {
    BiFunction<S, T, S> with();

    /**
     * Currying version of {@link ILens#with()}
     * @param value new value
     * @return {@link UnaryOperator} on S
     */
    default UnaryOperator<S> with(T value) {
        return s -> with(s, value);
    }

    /**
     * Directly application of {@link ILens#with()}
     * @param subject S subject
     * @param value new value
     * @return new S with adjusted T value
     */
    default S with(S subject, T value) {
        return with().apply(subject, value);
    }
}
