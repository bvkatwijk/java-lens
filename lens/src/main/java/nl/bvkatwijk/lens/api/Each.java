package nl.bvkatwijk.lens.api;

import io.vavr.Value;

import java.util.function.UnaryOperator;

public class Each {
    /// Create a function `V<T> -> V<T>` given a function `T -> T`
    ///
    /// @param f   Function `T -> T`
    /// @param <V> Value Type
    /// @param <T> Wrapped Type
    /// @return Function `V<T> -> V<T>`
    @SuppressWarnings("unchecked")
    public static <T, V extends Value<T>> UnaryOperator<V> mapEach(UnaryOperator<T> f) {
        return it -> (V) it.map(f);
    }
}
