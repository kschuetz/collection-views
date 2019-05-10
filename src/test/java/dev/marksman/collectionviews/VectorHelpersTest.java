package dev.marksman.collectionviews;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static dev.marksman.collectionviews.VectorHelpers.vectorToString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VectorHelpersTest {

    @Nested
    @DisplayName("vectorToString")
    class VectorToString {

        @Test
        void threeElements() {
            assertEquals("Vector(1, 2, 3)", vectorToString(Vector.of(1, 2, 3)));
        }

        @Test
        void oneElement() {
            assertEquals("Vector(1)", vectorToString(Vector.of(1)));
        }

        @Test
        void empty() {
            assertEquals("Vector()", vectorToString(Vector.empty()));
        }

        @Test
        void canHandleNullElements() {
            assertEquals("Vector(1, null, 3)", vectorToString(Vector.of(1, null, 3)));
        }

        @Test
        void canHandleNested() {
            assertEquals("Vector(Vector(1, 2), Vector(3, 4))",
                    vectorToString(Vector.of(Vector.of(1, 2), Vector.of(3, 4))));
        }

        @Test
        void throwsOnNullVector() {
            assertThrows(NullPointerException.class, () -> vectorToString(null));
        }

    }

}