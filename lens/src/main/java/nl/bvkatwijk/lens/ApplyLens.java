package nl.bvkatwijk.lens;

import nl.bvkatwijk.lens.kind.Lens;

import java.util.function.UnaryOperator;

public interface ApplyLens<S extends ApplyLens<S>> {
    /**
     * Apply the supplied operator to the property captured in the lens
     *
     * @param lens
     * @param op
     * @param <T>
     * @return
     */
    default <T> S with(Lens<S, T> lens, UnaryOperator<T> op) {
        @SuppressWarnings("unchecked") S s = (S) this;
        return lens.apply(s, op);
    }
}
