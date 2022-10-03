package software.kes.collectionviews;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class VectorHelpersTest {

    @Nested
    @DisplayName("vectorToString")
    class VectorToString {

        @Test
        void threeElements() {
            Assertions.assertEquals("Vector(1, 2, 3)", VectorHelpers.vectorToString(Vector.of(1, 2, 3)));
        }

        @Test
        void oneElement() {
            Assertions.assertEquals("Vector(1)", VectorHelpers.vectorToString(Vector.of(1)));
        }

        @Test
        void empty() {
            Assertions.assertEquals("Vector()", VectorHelpers.vectorToString(Vector.empty()));
        }

        @Test
        void canHandleNullElements() {
            Assertions.assertEquals("Vector(1, null, 3)", VectorHelpers.vectorToString(Vector.of(1, null, 3)));
        }

        @Test
        void canHandleNested() {
            Assertions.assertEquals("Vector(Vector(1, 2), Vector(3, 4))",
                    VectorHelpers.vectorToString(Vector.of(Vector.of(1, 2), Vector.of(3, 4))));
        }

        @Test
        void throwsOnNullVector() {
            assertThrows(NullPointerException.class, () -> VectorHelpers.vectorToString(null));
        }

    }

}