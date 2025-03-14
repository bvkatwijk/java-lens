package nl.bvkatwijk.lens.processor;

import com.google.testing.compile.JavaFileObjects;
import nl.bvkatwijk.lens.test.TestUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.RecordComponentElement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElementOpsTest {

    @Nested
    class OnString {
        public static final RecordComponentElement STRING_STREET = create("String street");

        @Test
        void unqualifiedTypeName() {
            assertEquals("String", ElementOps.unqualifiedType(STRING_STREET));
        }

        @Test
        void typeName() {
            assertEquals("java.lang.String", ElementOps.qualifiedType(STRING_STREET));
        }

        @Test
        void packageElement() {
            assertEquals("java.lang", ElementOps.packageElement(STRING_STREET));
        }

        @Test
        void fieldName() {
            assertEquals("street", ElementOps.fieldName(STRING_STREET));
        }
    }

    @Nested
    class OnByte {
        public static final RecordComponentElement STRING_STREET = create("byte someByte");

        @Test
        void unqualifiedTypeName() {
            assertEquals("Byte", ElementOps.unqualifiedType(STRING_STREET));
        }

        @Test
        void typeName() {
            assertEquals("java.lang.Byte", ElementOps.qualifiedType(STRING_STREET));
        }

        @Test
        void packageElement() {
            assertEquals("java.lang", ElementOps.packageElement(STRING_STREET));
        }

        @Test
        void fieldName() {
            assertEquals("someByte", ElementOps.fieldName(STRING_STREET));
        }
    }

    public static RecordComponentElement create(String fieldComponent) {
        var lensProcessor = new LensProcessor();
        TestUtil.compile(lensProcessor, JavaFileObjects.forSourceString(
            "pack.Address", "    package pack;\n" +
                            "\n" +
                            "    @nl.bvkatwijk.lens.Lenses\n" +
                            "    @lombok.With\n" +
                            "    public record Address(\n" +
                            "        " + fieldComponent + "\n" +
                            "    ) { }\n"));
        return lensProcessor
            .elements()
            .head();
    }
}
