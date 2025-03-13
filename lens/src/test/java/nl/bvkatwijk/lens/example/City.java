package nl.bvkatwijk.lens.example;

import nl.bvkatwijk.lens.Lenses;

@lombok.With
@Lenses
public record City(String name) {
}
