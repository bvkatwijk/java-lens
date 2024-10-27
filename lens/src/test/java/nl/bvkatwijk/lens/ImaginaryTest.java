package nl.bvkatwijk.lens;

import com.karuslabs.elementary.Results;
import com.karuslabs.elementary.junit.JavacExtension;
import com.karuslabs.elementary.junit.annotations.Inline;
import com.karuslabs.elementary.junit.annotations.Options;
import com.karuslabs.elementary.junit.annotations.Processors;
import nl.bvkatwijk.lens.processor.LensProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(JavacExtension.class)
@Options("-Werror")
@Processors({LensProcessor.class})
@Inline(name = "Person", source =
    """
        package nl.bvkatwijk.lens;
          
        import nl.bvkatwijk.lens.Lenses;
          
        @Lenses
        public record Person(String name) {
            public Person withName(String newName) {
                return new Person(newName);
            }
        }
        """
)
class ImaginaryTest {
    @Test
    void process_without_error(Results results) {
        var errors = results.find().errors();
        assertEquals(0, errors.count(), "Errors was not empty: " + errors.diagnostics());
    }
}
