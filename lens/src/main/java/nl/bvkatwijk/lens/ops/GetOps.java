package nl.bvkatwijk.lens.ops;

import java.util.function.Function;

/**
 * Operations for retrieving a value
 * @param <S> Source Type
 * @param <T> Target Type
 */
public interface GetOps<S, T> {
    Function<S, T> get();

    default T get(S s) {
        return get().apply(s);
    }
}
