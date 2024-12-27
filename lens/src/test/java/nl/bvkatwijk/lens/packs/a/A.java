package nl.bvkatwijk.lens.packs.a;

import lombok.With;
import nl.bvkatwijk.lens.Lenses;
import nl.bvkatwijk.lens.packs.b.B;

@With
@Lenses
public record A(B b) {
}
