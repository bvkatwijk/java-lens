package nl.bvkatwijk.lens.example;

import nl.bvkatwijk.lens.Lenses;

/**
 * Test lens generation for record having component with identical name
 */
@SuppressWarnings("unused")
@Lenses
public record Same(String same) {
}
