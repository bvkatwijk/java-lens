package nl.bvkatwijk.lens.processor;

import com.google.testing.compile.JavaFileObjects;
import junit.framework.AssertionFailedError;
import nl.bvkatwijk.lens.test.TestUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.type.TypeKind;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElementOpsTest {
    @Nested
    class UnqualifiedType {
        @ParameterizedTest
        @EnumSource(value = TypeKind.class, names = {
            "BOOLEAN",
            "BYTE",
            "SHORT",
            "INT",
            "LONG",
            "CHAR",
            "FLOAT",
            "DOUBLE"
        })
        void works(TypeKind kind) {
            var kindName = kind.toString().toLowerCase();
            assertEquals(switch (kind) {
                    case BOOLEAN, BYTE, SHORT, LONG, FLOAT, DOUBLE -> Code.capitalize(kindName);
                    case INT -> "Integer";
                    case CHAR -> "Character";
                    default -> throw new AssertionFailedError("Unreachable code reached");
                },
                ElementOps.unqualifiedType(create(kindName + " param")));
        }
    }

    @Nested
    class OnString {
        public static final RecordComponentElement ELEMENT = create("String street");

        @Test
        void unqualifiedTypeName() {
            assertEquals("String", ElementOps.unqualifiedType(ELEMENT));
        }

        @Test
        void typeName() {
            assertEquals("java.lang.String", ElementOps.qualifiedType(ELEMENT));
        }

        @Test
        void packageElement() {
            assertEquals("java.lang", ElementOps.packageElement(ELEMENT));
        }

        @Test
        void fieldName() {
            assertEquals("street", ElementOps.fieldName(ELEMENT));
        }
    }

    @Nested
    class OnByte {
        public static final RecordComponentElement ELEMENT = create("byte someByte");

        @Test
        void unqualifiedTypeName() {
            assertEquals("Byte", ElementOps.unqualifiedType(ELEMENT));
        }

        @Test
        void typeName() {
            assertEquals("java.lang.Byte", ElementOps.qualifiedType(ELEMENT));
        }

        @Test
        void packageElement() {
            assertEquals("java.lang", ElementOps.packageElement(ELEMENT));
        }

        @Test
        void fieldName() {
            assertEquals("someByte", ElementOps.fieldName(ELEMENT));
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
