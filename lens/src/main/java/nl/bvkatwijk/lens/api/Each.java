package nl.bvkatwijk.lens.api;

import io.vavr.Value;

import java.util.function.UnaryOperator;

public class Each {
    public static <V extends Value<T>, T> UnaryOperator<V> map(UnaryOperator<T> f) {
        return it -> (V) it.map(f);
    }
}
