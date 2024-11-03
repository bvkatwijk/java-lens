package nl.bvkatwijk.lens.kind;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public interface Modify<S, T> extends IGet<S, T>, IWith<S, T> {
    default BiFunction<S, UnaryOperator<T>, S> modify() {
        return (s, f) -> modify(f).apply(s);
    }

    /**
     * Currying variant of {@link ILens#modify(Object, UnaryOperator)}
     */
    default UnaryOperator<S> modify(UnaryOperator<T> op) {
        return s -> modify(s, op);
    }

    default S modify(S s, UnaryOperator<T> op) {
        return with().apply(s, op.apply(get(s)));
    }
}
