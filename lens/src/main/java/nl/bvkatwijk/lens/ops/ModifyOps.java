package nl.bvkatwijk.lens.ops;

import nl.bvkatwijk.lens.api.ILens;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * Operations for applying a value transformation
 * @param <S> Source Type
 * @param <T> Source Type
 */
public interface ModifyOps<S, T> extends GetOps<S, T>, WithOps<S, T> {
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
