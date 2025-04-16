package nl.bvkatwijk.lens.api;

import io.vavr.Value;
import io.vavr.collection.List;

import java.util.function.UnaryOperator;

public class Each {
    public static <T> UnaryOperator<Value<T>> value(UnaryOperator<T> f) {
        return it -> it.map(f);
    }

    public static <T> UnaryOperator<List<T>> list(UnaryOperator<T> f) {
        return it -> it.map(f);
    }
}
