package nl.bvkatwijk.lens.kind;

import io.vavr.Function1;
import io.vavr.Function2;

import java.util.function.UnaryOperator;

public interface ILens<S, T> {
    Function2<S, T, S> with();
    Function1<S, T> get();

    static <S, T> S apply(S s, Lens<S, T> lens, UnaryOperator<T> op) {
        return lens.with().apply(s, op.apply(lens.get().apply(s)));
    }

    default S apply(S s, UnaryOperator<T> op) {
        return with().apply(s, op.apply(get().apply(s)));
    }

    /**
     * Currying variant of {@link ILens#apply(Object, UnaryOperator)}
     */
    default UnaryOperator<S> apply(UnaryOperator<T> op) {
        return s -> apply(s, op);
    }

    default <R> Lens<R, T> compose(ILens<R, S> lens) {
        return lens.andThen(this);
    }

    default <U> Lens<S, U> andThen(ILens<T, U> lens) {
        return new Lens<>(
            (s, u) -> with().apply(s, lens.with().apply(get().apply(s)).apply(u)),
            get().andThen(lens.get()));
    }

    /** Currying variant of {@link ILens#with()} */
    default UnaryOperator<S> with(T t) {
        return s -> with().apply(s, t);
    }

    default Function2<S, UnaryOperator<T>, S> modify() {
        return (s, f) -> with().apply(s, f.apply(get().apply(s)));
    }

    /** Currying variant of {@link ILens#modify()} */
    default UnaryOperator<S> modify(UnaryOperator<T> f) {
        return s -> with().apply(s, f.apply(get().apply(s)));
    }
}
