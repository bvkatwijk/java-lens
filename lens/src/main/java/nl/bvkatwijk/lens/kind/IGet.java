package nl.bvkatwijk.lens.kind;

import java.util.function.Function;

public interface IGet<S, T> {
    Function<S, T> get();

    default T get(S s) {
        return get().apply(s);
    }
}
