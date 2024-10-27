package nl.bvkatwijk.lens;

import nl.bvkatwijk.lens.kind.ILens;
import nl.bvkatwijk.lens.kind.Lens;

public final class Const {
    /**
     * Name of interface
     */
    public static final String ILENS = ILens.class.getSimpleName();

    /**
     * Name of Lens concept
     */
    public static final String LENS = "Lens";

    public static final String BASE_LENS = Lens.class.getSimpleName();

    /**
     * Indent character for generated code
     */
    public static final String INDENT = "\t";

    /**
     * Pack of generated lenses
     */
    // Todo generate in source package instead?
    public static final String PACK = "nl.bvkatwijk.lens.gen";

    /**
     * Qualified
     */
    public static final String LENS_ANNOTATION_QUALIFIED = "nl.bvkatwijk.lens.Lenses";

    /**
     * Name of generated type-specific lens instance
     */
    public static final String ROOT_LENS_NAME = "ROOT";

    /**
     * Name of generated generic type parameter
     */
    public static final String PARAM_SOURCE_TYPE = "SOURCE";
}
