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

    default <R> Lens<R, T> compose(ILens<R, S> lens) {
        return lens.andThen(this);
    }

    default <U> Lens<S, U> andThen(ILens<T, U> lens) {
        return new Lens<>(
            (s, u) -> with().apply(s, lens.with().apply(get().apply(s)).apply(u)),
            get().andThen(lens.get()));
    }
}
