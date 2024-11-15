package nl.bvkatwijk.lens;

import nl.bvkatwijk.lens.api.ILens;

import java.util.function.UnaryOperator;

/**
 * Add convenience methods onto a record class.
 * See:
 * {@link LensOps#modify(ILens, UnaryOperator)}
 * {@link LensOps#with(ILens, Object)}
 * @param <S> Self-type
 */
public interface LensOps<S extends LensOps<S>> {
    /** Apply the supplied operator to the property through the lens */
    default <T> S modify(ILens<S, T> lens, UnaryOperator<T> op) {
        /* can this cast be prevented? */
        @SuppressWarnings("unchecked") S s = (S) this;
        return lens.modify(s, op);
    }

    /**
     *
     * @param lens Lens of S into T
     * @param val new value T
     * @return new S with updated value
     * @param <T> lens target type
     */
    default <T> S with(ILens<S, T> lens, T val) {
        return modify(lens, i -> val);
    }
}
