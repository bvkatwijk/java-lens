package org.bvkatwijk.lens;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public record Focus<S, T>(S source, Function<T, S> with, Function<S, T> zoom) implements IFocus<S, T> {
    @Override
    public <U> IFocus<S, U> focus(Function<U, T> nextWith, Function<T, U> nextZoom) {
        return new Focus<>(source, with.compose(nextWith), zoom.andThen(nextZoom));
    }

    @Override
    public S replace(T i) {
        return with.apply(i);
    }

    @Override
    public S modify(UnaryOperator<T> f) {
        return replace(this.zoom.andThen(f).apply(this.source));
    }
}
