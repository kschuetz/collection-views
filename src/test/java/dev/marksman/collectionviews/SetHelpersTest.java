package dev.marksman.collectionviews;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static dev.marksman.collectionviews.SetHelpers.setToString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SetHelpersTest {

    @Nested
    @DisplayName("setToString")
    class SetToString {

        @Test
        void oneElement() {
            assertEquals("Set(1)", setToString(Set.of(1)));
        }

        @Test
        void empty() {
            assertEquals("Set()", setToString(Set.empty()));
        }

        @Test
        void canHandleNullElements() {
            assertEquals("Set(null)", setToString(Set.of(null)));
        }

        @Test
        void canHandleNested() {
            assertEquals("Set(Set(1))",
                    setToString(Set.of(Set.of(1))));
        }

        @Test
        void throwsOnNullSet() {
            assertThrows(NullPointerException.class, () -> setToString(null));
        }

    }

}