package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.builtin.fn2.Eq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Replicate.replicate;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class ImmutableNonEmptyVectorTest {

    @Nested
    @DisplayName("fmap")
    class Fmap {

        private ImmutableNonEmptyVector<Integer> subject;
        private Integer[] underlying;

        @BeforeEach
        void beforeEach() {
            underlying = new Integer[]{1, 2, 3};
            subject = Vector.wrap(underlying).toImmutable().toNonEmptyOrThrow();
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
            ImmutableNonEmptyVector<Integer> mapped = foldLeft((acc, __) -> acc.fmap(n -> n + 1),
                    subject, replicate(10_000, UNIT));
            assertThat(mapped, contains(10_001, 10_002, 10_003));
        }

    }

    @Nested
    @DisplayName("fill")
    class Fill {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.fill(-1, "foo"));
        }

        @Test
        void throwsOnCountOfZero() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.fill(0, "foo"));
        }

        @Test
        void getWillNeverReturnNull() {
            ImmutableNonEmptyVector<String> subject = NonEmptyVector.fill(3, null);
            assertEquals(nothing(), subject.get(0));
            assertEquals(nothing(), subject.get(1));
            assertEquals(nothing(), subject.get(2));
        }

        @Test
        void iteratorNextReturnsCorrectElements() {
            ImmutableNonEmptyVector<String> subject = NonEmptyVector.fill(3, "foo");
            Iterator<String> iterator = subject.iterator();
            assertEquals("foo", iterator.next());
            assertEquals("foo", iterator.next());
            assertEquals("foo", iterator.next());
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        void iteratorHasNextCanBeCalledMultipleTimes() {
            ImmutableNonEmptyVector<String> subject = NonEmptyVector.fill(3, "foo");
            Iterator<String> iterator = subject.iterator();
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertEquals("foo", iterator.next());
        }

        @Test
        void iteratorHasNextReturnsFalseIfNothingRemains() {
            ImmutableNonEmptyVector<String> subject = NonEmptyVector.fill(1, "foo");
            Iterator<String> iterator = subject.iterator();
            iterator.next();
            assertFalse(iterator.hasNext());
        }

        @Test
        void iteratorNextThrowsIfNothingRemains() {
            ImmutableVector<String> subject = NonEmptyVector.fill(1, "foo");
            Iterator<String> iterator = subject.iterator();
            iterator.next();
            assertThrows(NoSuchElementException.class, iterator::next);
        }

        @Test
        void iteratorThrowsIfRemoveIsCalled() {
            ImmutableVector<String> subject = NonEmptyVector.fill(1, "foo");
            Iterator<String> iterator = subject.iterator();
            assertThrows(UnsupportedOperationException.class, iterator::remove);
        }

        @Nested
        @DisplayName("fill size 3")
        class FillSize3 {
            private ImmutableNonEmptyVector<String> subject;

            @BeforeEach
            void setUp() {
                subject = NonEmptyVector.fill(3, "foo");
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
            void headYieldsFirstElement() {
                assertEquals("foo", subject.head());
            }

            @Test
            void getForValidIndices() {
                assertEquals(just("foo"), subject.get(0));
                assertEquals(just("foo"), subject.get(1));
                assertEquals(just("foo"), subject.get(2));
            }

            @Test
            void getForInvalidIndices() {
                assertEquals(nothing(), subject.get(3));
                assertEquals(nothing(), subject.get(-1));
            }

            @Test
            void unsafeGetForValidIndices() {
                assertEquals("foo", subject.unsafeGet(0));
                assertEquals("foo", subject.unsafeGet(1));
                assertEquals("foo", subject.unsafeGet(2));
            }

            @Test
            void unsafeGetThrowsForInvalidIndices() {
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, contains("foo", "foo", "foo"));
            }

            @Test
            void tailIteratesCorrectly() {
                assertThat(subject.tail(), contains("foo", "foo"));
            }

            @Test
            void reverseIteratesCorrectly() {
                assertThat(subject.reverse(), contains("foo", "foo", "foo"));
            }

            @Test
            void zipWithIndexIteratesCorrectly() {
                assertThat(subject.zipWithIndex(), contains(tuple("foo", 0), tuple("foo", 1), tuple("foo", 2)));
            }

            @Test
            void toNonEmptySucceeds() {
                assertEquals(just(Vector.of("foo", "foo", "foo")),
                        subject.toNonEmpty());
            }

            @Test
            void toNonEmptyOrThrowReturnsItself() {
                assertSame(subject, subject.toNonEmptyOrThrow());
            }

            @Test
            void toImmutableReturnsItself() {
                assertSame(subject, subject.toImmutable());
            }

            @Test
            void allIndicesReturnSameReference() {
                Object obj = new Object();
                ImmutableNonEmptyVector<Object> subject = NonEmptyVector.fill(3, obj);
                assertSame(subject.unsafeGet(0), subject.unsafeGet(1));
                assertSame(subject.unsafeGet(1), subject.unsafeGet(2));
            }

            @Test
            void equalToItself() {
                assertEquals(subject, subject);
            }

            @Test
            void equalToOtherVectorWrappingEquivalentUnderlying() {
                Vector<String> other = Vector.wrap(asList("foo", "foo", "foo"));
                assertEquals(subject, other);
                assertEquals(other, subject);
            }

            @Test
            void equalToSameVectorConstructedImmutably() {
                assertEquals(subject, Vector.of("foo", "foo", "foo"));
                assertEquals(Vector.of("foo", "foo", "foo"), subject);
            }

            @Test
            void notEqualToNull() {
                assertNotEquals(subject, null);
                assertNotEquals(null, subject);
            }

            @Test
            void notEqualToEmpty() {
                assertNotEquals(subject, Vector.empty());
                assertNotEquals(Vector.empty(), subject);
            }

            @Test
            void notEqualToSubsequence() {
                Vector<String> subsequence = Vector.of("foo", "foo");
                assertNotEquals(subject, subsequence);
                assertNotEquals(subsequence, subject);
            }

            @Test
            void notEqualToSupersequence() {
                Vector<String> supersequence = Vector.of("foo", "foo", "foo", "foo");
                assertNotEquals(subject, supersequence);
                assertNotEquals(supersequence, subject);
            }

            @Test
            void reverseReturnsSameReference() {
                assertSame(subject, subject.reverse());
            }

            @Test
            void findPositive() {
                assertEquals(just("foo"), subject.find(Eq.eq("foo")));
            }

            @Test
            void findNegative() {
                assertEquals(nothing(), subject.find(Eq.eq("not in list")));
            }

            @Test
            void findIndexPositive() {
                assertEquals(just(0), subject.findIndex(Eq.eq("foo")));
            }

            @Test
            void findIndexNegative() {
                assertEquals(nothing(), subject.findIndex(Eq.eq("not in list")));
            }

        }

    }

    @Nested
    @DisplayName("lazyFill")
    class LazyFill {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.lazyFill(-1, n -> n * 10));
        }

        @Test
        void throwsOnCountOfZero() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.lazyFill(0, n -> n * 10));
        }

        @Test
        void getWillNeverReturnNull() {
            ImmutableVector<String> subject = NonEmptyVector.lazyFill(3, n -> null);
            assertEquals(nothing(), subject.get(0));
            assertEquals(nothing(), subject.get(1));
            assertEquals(nothing(), subject.get(2));
        }

        @Test
        void iteratorNextReturnsCorrectElements() {
            ImmutableVector<Integer> subject = NonEmptyVector.lazyFill(3, n -> n * 10);
            Iterator<Integer> iterator = subject.iterator();
            assertEquals(0, iterator.next());
            assertEquals(10, iterator.next());
            assertEquals(20, iterator.next());
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        void iteratorHasNextCanBeCalledMultipleTimes() {
            ImmutableVector<Integer> subject = NonEmptyVector.lazyFill(3, n -> n * 10);
            Iterator<Integer> iterator = subject.iterator();
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertEquals(0, iterator.next());
        }

        @Test
        void iteratorHasNextReturnsFalseIfNothingRemains() {
            ImmutableVector<Integer> subject = NonEmptyVector.lazyFill(1, n -> n * 10);
            Iterator<Integer> iterator = subject.iterator();
            iterator.next();
            assertFalse(iterator.hasNext());
        }

        @Test
        void iteratorNextThrowsIfNothingRemains() {
            ImmutableVector<Integer> subject = NonEmptyVector.lazyFill(1, n -> n * 10);
            Iterator<Integer> iterator = subject.iterator();
            iterator.next();
            assertThrows(NoSuchElementException.class, iterator::next);
        }

        @Test
        void iteratorThrowsIfRemoveIsCalled() {
            ImmutableVector<Integer> subject = NonEmptyVector.lazyFill(1, n -> n * 10);
            Iterator<Integer> iterator = subject.iterator();
            assertThrows(UnsupportedOperationException.class, iterator::remove);
        }

        @Nested
        @DisplayName("lazyFill size 3")
        class LazyFillSize3 {
            private ImmutableNonEmptyVector<Integer> subject;

            @BeforeEach
            void setUp() {
                subject = NonEmptyVector.lazyFill(3, n -> n * 10);
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
            void headYieldsFirstElement() {
                assertEquals(0, subject.head());
            }

            @Test
            void getForValidIndices() {
                assertEquals(just(0), subject.get(0));
                assertEquals(just(10), subject.get(1));
                assertEquals(just(20), subject.get(2));
            }

            @Test
            void getForInvalidIndices() {
                assertEquals(nothing(), subject.get(3));
                assertEquals(nothing(), subject.get(-1));
            }

            @Test
            void unsafeGetForValidIndices() {
                assertEquals(0, subject.unsafeGet(0));
                assertEquals(10, subject.unsafeGet(1));
                assertEquals(20, subject.unsafeGet(2));
            }

            @Test
            void unsafeGetThrowsForInvalidIndices() {
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, contains(0, 10, 20));
            }

            @Test
            void tailIteratesCorrectly() {
                assertThat(subject.tail(), contains(10, 20));
            }

            @Test
            void reverseIteratesCorrectly() {
                assertThat(subject.reverse(), contains(20, 10, 0));
            }

            @Test
            void zipWithIndexIteratesCorrectly() {
                assertThat(subject.zipWithIndex(), contains(tuple(0, 0), tuple(10, 1), tuple(20, 2)));
            }

            @Test
            void toNonEmptySucceeds() {
                assertEquals(just(Vector.of(0, 10, 20)),
                        subject.toNonEmpty());
            }

            @Test
            void toNonEmptyOrThrowReturnsItself() {
                assertSame(subject, subject.toNonEmptyOrThrow());
            }

            @Test
            void toImmutableReturnsItself() {
                assertSame(subject, subject.toImmutable());
            }

            @Test
            void equalToItself() {
                assertEquals(subject, subject);
            }

            @Test
            void equalToOtherVectorWrappingEquivalentUnderlying() {
                Vector<Integer> other = Vector.wrap(asList(0, 10, 20));
                assertEquals(subject, other);
                assertEquals(other, subject);
            }

            @Test
            void equalToSameVectorConstructedImmutably() {
                assertEquals(subject, Vector.of(0, 10, 20));
                assertEquals(Vector.of(0, 10, 20), subject);
            }

            @Test
            void notEqualToNull() {
                assertNotEquals(subject, null);
                assertNotEquals(null, subject);
            }

            @Test
            void notEqualToEmpty() {
                assertNotEquals(subject, Vector.empty());
                assertNotEquals(Vector.empty(), subject);
            }

            @Test
            void notEqualToSubsequence() {
                Vector<Integer> subsequence = Vector.of(0, 10);
                assertNotEquals(subject, subsequence);
                assertNotEquals(subsequence, subject);
            }

            @Test
            void notEqualToSupersequence() {
                Vector<Integer> supersequence = Vector.of(0, 10, 20, 30);
                assertNotEquals(subject, supersequence);
                assertNotEquals(supersequence, subject);
            }

            @Test
            void findPositive() {
                assertEquals(just(20), subject.find(n -> n >= 20));
            }

            @Test
            void findNegative() {
                assertEquals(nothing(), subject.find(n -> n > 100));
            }

            @Test
            void findIndexPositive() {
                assertEquals(just(2), subject.findIndex(n -> n >= 20));
            }

            @Test
            void findIndexNegative() {
                assertEquals(nothing(), subject.findIndex(n -> n > 100));
            }

        }

    }

}
