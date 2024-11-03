package nl.bvkatwijk.lens.kind;

import java.util.function.Function;

public interface ILens<S, T> extends Modify<S, T>, IGet<S, T>, IWith<S, T> {
    default <U> ILens<S, U> andThen(ILens<T, U> lens) {
        return new Lens<>(
            get().andThen(lens::get),
            (s, u) -> with(s, lens.with(get(s), u)));
    }

    /**
     * Compose target lens with this one, returning a new lens that first applies the argument lens
     * before applying this one.
     * <br />
     * See {@link java.util.function.Function#compose(Function)}
     *
     * @param lens target {@link ILens}
     * @return {@link ILens} instance applying target lens before applying this one.
     * @param <R> Source type
     */
    default <R> ILens<R, T> compose(ILens<R, S> lens) {
        return lens.andThen(this);
    }
}
