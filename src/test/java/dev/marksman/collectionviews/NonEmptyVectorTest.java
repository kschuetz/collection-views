package dev.marksman.collectionviews;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NonEmptyVectorTest {

    @Nested
    @DisplayName("tryWrap")
    class TryWrapTests {
        @Test
        void arraySuccess() {
            NonEmptyVector<String> result = NonEmptyVector.tryWrap(new String[]{"foo"}).orElseThrow(AssertionError::new);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void listSuccess() {
            NonEmptyVector<String> result = NonEmptyVector.tryWrap(singletonList("foo")).orElseThrow(AssertionError::new);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void arrayFailure() {
            assertEquals(nothing(), NonEmptyVector.tryWrap(new String[]{}));
        }

        @Test
        void listFailure() {
            assertEquals(nothing(), NonEmptyVector.tryWrap(emptyList()));
        }

    }

    @Nested
    @DisplayName("wrapOrThrow")
    class WrapOrThrowTests {
        @Test
        void arraySuccess() {
            NonEmptyVector<String> result = NonEmptyVector.wrapOrThrow(new String[]{"foo"});
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void listSuccess() {
            NonEmptyVector<String> result = NonEmptyVector.wrapOrThrow(singletonList("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void arrayFailure() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.wrapOrThrow(new String[]{}));
        }

        @Test
        void listFailure() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.wrapOrThrow(emptyList()));
        }
    }

}
