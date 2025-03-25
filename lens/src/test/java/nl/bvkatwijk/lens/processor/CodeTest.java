package nl.bvkatwijk.lens.processor;

import io.vavr.collection.List;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeTest {
    @Nested
    @DisplayName("Reference")
    class MethodReferenceTest {
        @Test
        void generate() {
            assertEquals("A::b", Code.reference("A", "b"));
        }
    }

    @Nested
    @DisplayName("Import")
    class ImportStatementTest {
        @Test
        void generate() {
            assertEquals("import a.B;", Code.importStatement("a.B"));
        }
    }

    @Nested
    @DisplayName("Unqualify")
    class UnqualifyTest {
        @Test
        void generate() {
            assertEquals("C", Code.unqualify("a.b.C"));
        }
    }

    @Nested
    @DisplayName("Remove Generics")
    class RemoveGenericsTest {
        @Test
        void generate() {
            assertEquals("A", Code.removeGenerics("A<B, C<D>>"));
        }
    }

    @Nested
    class WithTest {
        @ParameterizedTest
        @EnumSource(ParamKind.class)
        void approve(ParamKind paramKind) {
            Approvals.verify(Code.with("TypeName", 0, List.of(
                    new Field("FieldTypeName", "fieldName", paramKind)
                ))
                .toList()
                .mkString("\n"), Approvals.NAMES.withParameters(paramKind.name()));
        }
    }
}
