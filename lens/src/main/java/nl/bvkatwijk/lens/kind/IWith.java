package nl.bvkatwijk.lens.kind;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public interface IWith<S, T> {
    BiFunction<S, T, S> with();

    /**
     * Currying version of {@link ILens#with()}
     * @param t new value
     * @return {@link UnaryOperator} on S
     */
    default UnaryOperator<S> with(T t) {
        return s -> with().apply(s, t);
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