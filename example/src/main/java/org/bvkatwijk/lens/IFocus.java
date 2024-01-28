package org.bvkatwijk.lens;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface IFocus<S, T> {
    <U> IFocus<S, U> focus(Function<U, T> nextWith, Function<T, U> nextZoom);

    S replace(T i);

    S modify(UnaryOperator<T> f);
}
