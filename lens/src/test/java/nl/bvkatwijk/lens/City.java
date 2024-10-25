package nl.bvkatwijk.lens;

import lombok.With;

@With
@Lenses
public record City(String name) {
    public static City rand() {
        return new City("city " + ((int) (Math.random() * 100)));
    }
}
    
