package nl.bvkatwijk.lens;

import nl.bvkatwijk.lens.kind.ILens;
import nl.bvkatwijk.lens.kind.Lens;

import java.util.function.UnaryOperator;

public interface LensOps<S extends LensOps<S>> {
    /** Apply the supplied operator to the property through the lens */
    default <T> S modify(ILens<S, T> lens, UnaryOperator<T> op) {
        @SuppressWarnings("unchecked") S s = (S) this;
        return lens.apply(s, op);
    }

    /** Set the supplied value to the property through the lens */
    default <T> S with(ILens<S, T> lens, T val) {
        return modify(lens, i -> val);
    }
}
