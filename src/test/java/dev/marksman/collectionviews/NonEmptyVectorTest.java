package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Replicate.replicate;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static java.util.Arrays.asList;
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
