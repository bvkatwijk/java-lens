package nl.bvkatwijk.lens.kind;

import io.vavr.Function1;

public interface IGet<S, T> {
    Function1<S, T> get();

    default T get(S s) {
        return get().apply(s);
    }
}
