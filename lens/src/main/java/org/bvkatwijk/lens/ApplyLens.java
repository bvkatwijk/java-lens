package org.bvkatwijk.lens;

import org.bvkatwijk.lens.kind.Lens;

import java.util.function.UnaryOperator;

public interface ApplyLens<S extends ApplyLens<S>> {
    /**
     * Apply the supplied operator to the property captured in the iso
     * @param iso
     * @param op
     * @return
     * @param <T>
     */
    default  <T> S with(Lens<S, T> iso, UnaryOperator<T> op) {
        @SuppressWarnings("unchecked") S s = (S) this;
        return iso.apply(s, op);
    }
}
