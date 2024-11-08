package nl.bvkatwijk.lens;

import nl.bvkatwijk.lens.api.ILens;
import nl.bvkatwijk.lens.api.Lens;

public final class Const {
    /**
     * Qualified name of {@link ILens}
     */
    public static final String ILENS = ILens.class.getSimpleName();

    /**
     * Name of Lens concept
     */
    public static final String LENS = "Lens";

    /**
     * Qualified name of {@link Lens}
     */
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
     * Qualified name of {@link Lenses}
     */
    public static final String LENS_ANNOTATION_QUALIFIED = "nl.bvkatwijk.lens.Lenses";

    /**
     * Name of generated type-specific lens instance
     */
    public static final String ROOT_LENS_NAME = "µ";

    /**
     * Name of generated generic type parameter
     */
    public static final String PARAM_SOURCE_TYPE = "SOURCE";
}
