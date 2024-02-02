package org.bvkatwijk.lens.kind;

import io.vavr.Function1;
import io.vavr.Function2;

import java.util.function.UnaryOperator;

public record Lens<S, T>(Function2<S, T, S> with, Function1<S, T> get) {
    public static <S, T> S apply(S s, Lens<S, T> lens, UnaryOperator<T> op) {
        return lens.with().apply(s, op.apply(lens.get().apply(s)));
    }

    public S apply(S s, UnaryOperator<T> op) {
        return with().apply(s, op.apply(get().apply(s)));
    }

    public <U> Lens<S, U> andThen(Lens<T, U> you) {
        return new Lens<>((s, u) -> with.apply(s, you.with().apply(get.apply(s)).apply(u)), get.andThen(you.get));
    }
}
