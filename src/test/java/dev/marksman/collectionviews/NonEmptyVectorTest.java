package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Cycle.cycle;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Replicate.replicate;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class NonEmptyVectorTest {

    @Nested
    @DisplayName("tryWrap")
    class TryWrapTests {

        @Nested
        @DisplayName("array")
        class TryWrapArrayTests {

            @Test
            void throwsOnNullArgument() {
                Integer[] arr = null;
                assertThrows(NullPointerException.class, () -> NonEmptyVector.tryWrap(arr));
            }

            @Test
            void success() {
                NonEmptyVector<String> result = NonEmptyVector.tryWrap(new String[]{"foo"}).orElseThrow(AssertionError::new);
                assertThat(result, contains("foo"));
                assertEquals("foo", result.head());
                assertEquals(1, result.size());
            }

            @Test
            void failure() {
                assertEquals(nothing(), NonEmptyVector.tryWrap(new String[]{}));
            }

        }

        @Nested
        @DisplayName("List")
        class TryWrapListTests {

            @Test
            void throwsOnNullArgument() {
                List<String> list = null;
                assertThrows(NullPointerException.class, () -> NonEmptyVector.tryWrap(list));
            }

            @Test
            void success() {
                NonEmptyVector<String> result = NonEmptyVector.tryWrap(singletonList("foo")).orElseThrow(AssertionError::new);
                assertThat(result, contains("foo"));
                assertEquals("foo", result.head());
                assertEquals(1, result.size());
            }

            @Test
            void failure() {
                assertEquals(nothing(), NonEmptyVector.tryWrap(emptyList()));
            }

        }

    }

    @Nested
    @DisplayName("wrapOrThrow")
    class WrapOrThrowTests {

        @Nested
        @DisplayName("array")
        class WrapOrThrowArrayTests {

            @Test
            void throwsOnNullArgument() {
                Integer[] arr = null;
                assertThrows(NullPointerException.class, () -> NonEmptyVector.wrapOrThrow(arr));
            }

            @Test
            void success() {
                NonEmptyVector<String> result = NonEmptyVector.wrapOrThrow(new String[]{"foo"});
                assertThat(result, contains("foo"));
                assertEquals("foo", result.head());
                assertEquals(1, result.size());
            }

            @Test
            void failure() {
                assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.wrapOrThrow(new String[]{}));
            }

        }

        @Nested
        @DisplayName("List")
        class WrapOrThrowListTests {

            @Test
            void throwsOnNullArgument() {
                List<String> list = null;
                assertThrows(NullPointerException.class, () -> NonEmptyVector.wrapOrThrow(list));
            }

            @Test
            void success() {
                NonEmptyVector<String> result = NonEmptyVector.wrapOrThrow(singletonList("foo"));
                assertEquals("foo", result.head());
                assertEquals(1, result.size());
            }

            @Test
            void failure() {
                assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.wrapOrThrow(emptyList()));
            }

        }

    }

    @Nested
    @DisplayName("tryCopyFrom")
    class TryCopyFromTests {

        @Nested
        @DisplayName("array")
        class TryCopyFromArrayTests {

            @Test
            void throwsOnNullArgument() {
                Integer[] arr = null;
                assertThrows(NullPointerException.class, () -> NonEmptyVector.tryCopyFrom(arr));
            }

            @Test
            void success() {
                NonEmptyVector<String> result = NonEmptyVector.tryCopyFrom(new String[]{"foo"}).orElseThrow(AssertionError::new);
                assertThat(result, contains("foo"));
                assertEquals("foo", result.head());
                assertEquals(1, result.size());
            }

            @Test
            void successWithMaxCount() {
                NonEmptyVector<String> result = NonEmptyVector.tryCopyFrom(2, new String[]{"foo", "bar", "baz"}).orElseThrow(AssertionError::new);
                assertThat(result, contains("foo", "bar"));
            }

            @Test
            void failure() {
                assertEquals(nothing(), NonEmptyVector.tryCopyFrom(new String[]{}));
            }

        }

        @Nested
        @DisplayName("Iterable")
        class TryCopyFromIterableTests {

            @Test
            void throwsOnNullArgument() {
                Iterable<String> iterable = null;
                assertThrows(NullPointerException.class, () -> NonEmptyVector.tryCopyFrom(iterable));
            }

            @Test
            void success() {
                NonEmptyVector<String> result = NonEmptyVector.tryCopyFrom(singletonList("foo")).orElseThrow(AssertionError::new);
                assertThat(result, contains("foo"));
                assertEquals("foo", result.head());
                assertEquals(1, result.size());
            }

            @Test
            void successWithMaxCount() {
                Iterable<Integer> infinite = cycle(asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
                assertEquals(just(Vector.of(0, 1, 2, 3, 4)),
                        NonEmptyVector.tryCopyFrom(5, infinite));
            }

            @Test
            void failure() {
                assertEquals(nothing(), NonEmptyVector.tryCopyFrom(emptyList()));
            }

            @Test
            void returnsOriginalIfAlreadyImmutableNonEmptyVector() {
                Vector<Integer> original = Vector.of(1, 2, 3, 4);
                NonEmptyVector<Integer> result = NonEmptyVector.tryCopyFrom(original).orElseThrow(AssertionError::new);
                assertSame(original, result);
            }

        }

    }

    @Nested
    @DisplayName("copyFromOrThrow")
    class CopyFromOrThrowTests {

        @Nested
        @DisplayName("array")
        class CopyFromOrThrowArrayTests {

            @Test
            void throwsOnNullArgument() {
                Integer[] arr = null;
                assertThrows(NullPointerException.class, () -> NonEmptyVector.copyFromOrThrow(arr));
            }

            @Test
            void success() {
                NonEmptyVector<String> result = NonEmptyVector.copyFromOrThrow(new String[]{"foo"});
                assertThat(result, contains("foo"));
                assertEquals("foo", result.head());
                assertEquals(1, result.size());
            }

            @Test
            void successWithMaxCount() {
                NonEmptyVector<String> result = NonEmptyVector.copyFromOrThrow(2, new String[]{"foo", "bar", "baz"});
                assertThat(result, contains("foo", "bar"));
            }

            @Test
            void failure() {
                assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.copyFromOrThrow(new String[]{}));
            }

        }

        @Nested
        @DisplayName("Iterable")
        class CopyFromOrThrowIterableTests {

            @Test
            void throwsOnNullArgument() {
                Iterable<String> iterable = null;
                assertThrows(NullPointerException.class, () -> NonEmptyVector.copyFromOrThrow(iterable));
            }

            @Test
            void success() {
                NonEmptyVector<String> result = NonEmptyVector.copyFromOrThrow(singletonList("foo"));
                assertThat(result, contains("foo"));
                assertEquals("foo", result.head());
                assertEquals(1, result.size());
            }

            @Test
            void successWithMaxCount() {
                Iterable<Integer> infinite = cycle(asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
                assertEquals(Vector.of(0, 1, 2, 3, 4),
                        NonEmptyVector.copyFromOrThrow(5, infinite));
            }

            @Test
            void failure() {
                assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.copyFromOrThrow(emptyList()));
            }

            @Test
            void returnsOriginalIfAlreadyImmutableNonEmptyVector() {
                Vector<Integer> original = Vector.of(1, 2, 3, 4);
                NonEmptyVector<Integer> result = NonEmptyVector.copyFromOrThrow(original);
                assertSame(original, result);
            }

        }

    }

    @Nested
    @DisplayName("fmap")
    class FmapTests {

        @Test
        void wrappedArray() {
            NonEmptyVector<Integer> subject = NonEmptyVector.wrapOrThrow(new Integer[]{1, 2, 3});
            assertThat(subject.fmap(Object::toString), contains("1", "2", "3"));
        }

        @Test
        void wrappedList() {
            NonEmptyVector<Integer> subject = NonEmptyVector.wrapOrThrow(asList(1, 2, 3));
            assertThat(subject.fmap(Object::toString), contains("1", "2", "3"));
        }

        @Test
        void functorIdentity() {
            NonEmptyVector<Integer> subject = Vector.of(1, 2, 3);
            assertEquals(subject, subject.fmap(id()));
        }

        @Test
        void functorComposition() {
            NonEmptyVector<Integer> source = Vector.of(1, 2, 3);
            Fn1<Integer, Integer> f = n -> n * 2;
            Fn1<Integer, String> g = Object::toString;
            assertEquals(source.fmap(f).fmap(g), source.fmap(f.andThen(g)));
        }

        @Test
        void willNotMakeCopiesOfUnderlyingArrays() {
            Integer[] underlying = {1, 2, 3};
            NonEmptyVector<Integer> subject = NonEmptyVector.wrapOrThrow(underlying);
            NonEmptyVector<Integer> mapped1 = subject.fmap(n -> n * 2);
            NonEmptyVector<String> mapped2 = mapped1.fmap(Object::toString);
            assertThat(mapped1, contains(2, 4, 6));
            assertThat(mapped2, contains("2", "4", "6"));
            underlying[0] = 10;
            assertThat(mapped1, contains(20, 4, 6));
            assertThat(mapped2, contains("20", "4", "6"));
        }

        @Test
        void willNotMakeCopiesOfUnderlyingLists() {
            List<Integer> underlying = asList(1, 2, 3);
            NonEmptyVector<Integer> subject = NonEmptyVector.wrapOrThrow(underlying);
            NonEmptyVector<Integer> mapped1 = subject.fmap(n -> n * 2);
            NonEmptyVector<String> mapped2 = mapped1.fmap(Object::toString);
            assertThat(mapped1, contains(2, 4, 6));
            assertThat(mapped2, contains("2", "4", "6"));
            underlying.set(0, 10);
            assertThat(mapped1, contains(20, 4, 6));
            assertThat(mapped2, contains("20", "4", "6"));
        }

        @Test
        void stackSafe() {
            NonEmptyVector<Integer> source = Vector.of(1, 2, 3);
            NonEmptyVector<Integer> mapped = foldLeft((acc, __) -> acc.fmap(n -> n + 1),
                    source, replicate(10_000, UNIT));
            assertThat(mapped, contains(10_001, 10_002, 10_003));
        }

    }

}
