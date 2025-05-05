package nl.bvkatwijk.lens.api;

import io.vavr.Value;

import java.util.function.UnaryOperator;

public class Each {
    ///Create a function `V<T> -> V<T>` given a function `T -> T`
    /// @param f Function `T -> T`
    /// @return Function `V<T> -> V<T>`
    /// @param <V> Value Type
    /// @param <T> Wrapped Type
    @SuppressWarnings("unchecked")
    public static <T, V extends Value<T>> UnaryOperator<V> mapEach(UnaryOperator<T> f) {
        return it -> (V) it.map(f);
    }
}
