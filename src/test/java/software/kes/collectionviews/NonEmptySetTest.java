package software.kes.collectionviews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NonEmptySetTest {

    @Nested
    @DisplayName("'of' constructor")
    class Constructor {

        @Nested
        @DisplayName("of size 3")
        class OfSize3 {
            private NonEmptySet<String> subject;

            @BeforeEach
            void beforeEach() {
                subject = Set.of("foo", "bar", "baz");
            }

            @Test
            void notEmpty() {
                assertFalse(subject.isEmpty());
            }

            @Test
            void sizeIs3() {
                assertEquals(3, subject.size());
            }

            @Test
            void containsPositive() {
                assertTrue(subject.contains("foo"));
                assertTrue(subject.contains("bar"));
                assertTrue(subject.contains("baz"));
            }

            @Test
            void containsNegative() {
                assertFalse(subject.contains("qux"));
                assertFalse(subject.contains("quux"));
                assertFalse(subject.contains("qwerty"));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, containsInAnyOrder("foo", "bar", "baz"));
            }
        }

        @Nested
        @DisplayName("of size 1")
        class OfSize1 {
            private NonEmptySet<String> subject;

            @BeforeEach
            void beforeEach() {
                subject = Set.of("foo");
            }

            @Test
            void notEmpty() {
                assertFalse(subject.isEmpty());
            }

            @Test
            void sizeIs3() {
                assertEquals(1, subject.size());
            }

            @Test
            void containsPositive() {
                assertTrue(subject.contains("foo"));
            }

            @Test
            void containsNegative() {
                assertFalse(subject.contains("bar"));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, containsInAnyOrder("foo"));
            }
        }

    }

    @Nested
    @DisplayName("maybeWrap")
    class MaybeWrap {

        @Test
        void javaUtilSetSuccess() {
            NonEmptySet<String> result = NonEmptySet.maybeWrap(singleton("foo")).orElseThrow(AssertionError::new);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
            assertTrue(result.contains("foo"));
        }

        @Test
        void javaUtilSetFailure() {
            assertEquals(nothing(), NonEmptySet.maybeWrap(emptySet()));
        }

    }

    @Nested
    @DisplayName("wrapOrThrow")
    class WrapOrThrow {

        @Test
        void javaUtilSetSuccess() {
            NonEmptySet<String> result = NonEmptySet.wrapOrThrow(singleton("foo"));
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
            assertTrue(result.contains("foo"));
        }

        @Test
        void javaUtilSetFailure() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptySet.wrapOrThrow(emptySet()));
        }

    }

}
