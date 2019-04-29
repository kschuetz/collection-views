package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;
import org.junit.jupiter.api.BeforeEach;
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
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class ImmutableVectorTest {

    @Nested
    @DisplayName("empty")
    class EmptyVectorTests {
        @Test
        void alwaysYieldsSameReference() {
            Vector<Integer> v1 = Vector.empty();
            Vector<String> v2 = Vector.empty();
            assertSame(v1, v2);
        }

        @Test
        void isEmpty() {
            assertTrue(Vector.empty().isEmpty());
        }

        @Test
        void sizeIsZero() {
            assertEquals(0, Vector.empty().size());
        }

        @Test
        void getReturnsNothing() {
            Vector<Object> subject = Vector.empty();
            assertEquals(nothing(), subject.get(0));
            assertEquals(nothing(), subject.get(1));
            assertEquals(nothing(), subject.get(-1));
        }

        @Test
        void unsafeGetThrows() {
            Vector<Object> subject = Vector.empty();
            assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(0));
            assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
            assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
        }

        @Test
        void iteratesCorrectly() {
            assertThat(Vector.empty(), emptyIterable());
        }

        @Test
        void tailIsEmpty() {
            assertThat(Vector.empty().tail(), emptyIterable());
        }
    }

    @Nested
    @DisplayName("fmap")
    class FmapTests {

        private ImmutableVector<Integer> subject;
        private Integer[] underlying;

        @BeforeEach
        void beforeEach() {
            underlying = new Integer[]{1, 2, 3};
            subject = Vector.copyFrom(underlying);
        }


        @Test
        void throwsOnNullFunction() {
            assertThrows(NullPointerException.class, () -> subject.fmap(null));
        }

        @Test
        void fmap() {
            assertThat(subject.fmap(Object::toString), contains("1", "2", "3"));
        }

        @Test
        void functorIdentity() {
            assertEquals(subject, subject.fmap(id()));
        }

        @Test
        void functorComposition() {
            Fn1<Integer, Integer> f = n -> n * 2;
            Fn1<Integer, String> g = Object::toString;
            assertEquals(subject.fmap(f).fmap(g), subject.fmap(f.andThen(g)));
        }

        @Test
        void notAffectedByMutation() {
            underlying[0] = 10;
            assertThat(subject.fmap(n -> n * 2), contains(2, 4, 6));
        }

        @Test
        void stackSafe() {
            ImmutableVector<Integer> mapped = foldLeft((acc, __) -> acc.fmap(n -> n + 1),
                    subject, replicate(10_000, UNIT));
            assertThat(mapped, contains(10_001, 10_002, 10_003));
        }
    }

    @Nested
    @DisplayName("take")
    class TakeTests {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> Vector.of(1).take(-1));
        }

        @Test
        void takesAsMuchAsItCan() {
            assertThat(Vector.of(1, 2, 3).take(1_000_000),
                    contains(1, 2, 3));
        }

        @Test
        void onlyTakesWhatWasAskedFor() {
            assertThat(Vector.of(1, 2, 3).take(3),
                    contains(1, 2, 3));
            assertThat(Vector.of(1, 2, 3).take(2),
                    contains(1, 2));
            assertThat(Vector.of(1, 2, 3).take(1),
                    contains(1));
            assertThat(Vector.of(1, 2, 3).take(0),
                    emptyIterable());
        }

        @Test
        void notAffectedByMutation() {
            List<String> originalUnderlying = asList("foo", "bar", "baz");
            ImmutableVector<String> original = Vector.copyFrom(originalUnderlying);
            Vector<String> sliced = original.take(2);
            assertThat(sliced, contains("foo", "bar"));
            originalUnderlying.set(0, "qwerty");
            assertThat(sliced, contains("foo", "bar"));
        }

        @Test
        void returnsOriginalVectorReferenceIfPossible() {
            ImmutableVector<String> original = Vector.copyFrom(asList("foo", "bar", "baz"));
            ImmutableVector<String> slice1 = original.take(100);
            ImmutableVector<String> slice2 = original.take(3);
            assertSame(original, slice1);
            assertSame(original, slice2);
        }

    }

    @Nested
    @DisplayName("drop")
    class DropTests {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> Vector.copyFrom(singletonList(1)).drop(-1));
        }

        @Test
        void countZeroReturnsSameReference() {
            ImmutableVector<Integer> source = Vector.copyFrom(asList(1, 2, 3));
            ImmutableVector<Integer> sliced = source.drop(0);
            assertSame(source, sliced);
        }

        @Test
        void countEqualToSizeReturnsEmptyVector() {
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).drop(3));
        }

        @Test
        void countExceedingSizeReturnsEmptyVector() {
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).drop(4));
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).drop(1_000_000));
        }

        @Test
        void oneElement() {
            ImmutableVector<Integer> source = Vector.copyFrom(asList(1, 2, 3));
            assertThat(source.drop(1), contains(2, 3));
        }

        @Test
        void twoElements() {
            ImmutableVector<Integer> source = Vector.copyFrom(asList(1, 2, 3));
            assertThat(source.drop(2), contains(3));
        }

        @Test
        void notAffectedByMutation() {
            List<String> originalUnderlying = asList("foo", "bar", "baz");
            ImmutableVector<String> original = Vector.copyFrom(originalUnderlying);
            Vector<String> sliced = original.drop(1);
            assertThat(sliced, contains("bar", "baz"));
            originalUnderlying.set(1, "qwerty");
            assertThat(sliced, contains("bar", "baz"));
        }

    }

    @Nested
    @DisplayName("slice")
    class SliceTests {

        @Test
        void throwsOnNegativeStartIndex() {
            assertThrows(IllegalArgumentException.class, () -> Vector.copyFrom(asList(1, 2, 3)).slice(-1, 1));
        }

        @Test
        void throwsOnNegativeEndIndex() {
            assertThrows(IllegalArgumentException.class, () -> Vector.copyFrom(asList(1, 2, 3)).slice(0, -1));
        }

        @Test
        void returnsEmptyVectorIfWidthIsZero() {
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).slice(0, 0));
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).slice(1_000_000, 1_000_000));
        }

        @Test
        void returnsEmptyVectorIfWidthLessThanZero() {
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).slice(10, 9));
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).slice(1_000_000, 0));
        }

        @Test
        void takesAsMuchAsItCan() {
            assertThat(Vector.copyFrom(asList(1, 2, 3)).slice(1, 1_000_000),
                    contains(2, 3));
        }

        @Test
        void onlyTakesWhatWasAskedFor() {
            assertThat(Vector.copyFrom(asList(1, 2, 3)).slice(0, 3),
                    contains(1, 2, 3));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).slice(1, 3),
                    contains(2, 3));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).slice(1, 2),
                    contains(2));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).slice(0, 0),
                    emptyIterable());
        }

        @Test
        void startIndexEqualToSizeReturnsEmptyVector() {
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).slice(3, 6));
        }

        @Test
        void startIndexExceedingSizeReturnsEmptyVector() {
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).slice(4, 3));
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).slice(1_000_000, 3));
        }

        @Test
        void notAffectedByMutation() {
            List<String> underlying = asList("foo", "bar", "baz");
            ImmutableVector<String> original = Vector.copyFrom(underlying);
            ImmutableVector<String> slice2 = original.slice(1, 3);
            ImmutableVector<String> slice3 = original.slice(2, 100);
            underlying.set(0, "qwerty");
            underlying.set(2, "quux");
            assertThat(original, contains("foo", "bar", "baz"));
            assertThat(slice2, contains("bar", "baz"));
            assertThat(slice3, contains("baz"));
        }

    }

}
