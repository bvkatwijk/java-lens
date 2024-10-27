package nl.bvkatwijk.lens.kind;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public interface ILens<S, T> extends IGet<S, T>, IWith<S, T> {
    default S apply(S s, UnaryOperator<T> op) {
        return with().apply(s, op.apply(get(s)));
    }

    /**
     * Currying variant of {@link ILens#apply(Object, UnaryOperator)}
     */
    default UnaryOperator<S> apply(UnaryOperator<T> op) {
        return s -> apply(s, op);
    }

    default BiFunction<S, UnaryOperator<T>, S> modify() {
        return (s, f) -> with(s, f.apply(get(s)));
    }

    /** Currying variant of {@link ILens#modify()} */
    default UnaryOperator<S> modify(UnaryOperator<T> f) {
        return s -> with(s, f.apply(get(s)));
    }

    default <U> ILens<S, U> andThen(ILens<T, U> lens) {
        return new Lens<>(
            get().andThen(lens::get),
            (s, u) -> with(s, lens.with(get(s), u)));
    }

    default <R> ILens<R, T> compose(ILens<R, S> lens) {
        return lens.andThen(this);
    }
}
