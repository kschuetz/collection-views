package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.builtin.fn1.Id;
import com.jnape.palatable.lambda.functions.builtin.fn2.Eq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Cycle.cycle;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Repeat.repeat;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Cons.cons;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Replicate.replicate;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Tupler2.tupler;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class ImmutableVectorTest {

    @Nested
    @DisplayName("empty")
    class Empty {

        @Test
        void alwaysYieldsSameReference() {
            ImmutableVector<Integer> v1 = Vector.empty();
            ImmutableVector<String> v2 = Vector.empty();
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
            ImmutableVector<Object> subject = Vector.empty();
            assertEquals(nothing(), subject.get(0));
            assertEquals(nothing(), subject.get(1));
            assertEquals(nothing(), subject.get(-1));
        }

        @Test
        void unsafeGetThrows() {
            ImmutableVector<Object> subject = Vector.empty();
            assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(0));
            assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
            assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
        }

        @Test
        void iteratesCorrectly() {
            assertThat(Vector.empty(), emptyIterable());
        }

        @Test
        void reverseIsEmpty() {
            assertThat(Vector.empty().reverse(), emptyIterable());
        }

        @Test
        void zipWithIndexIsEmpty() {
            assertThat(Vector.empty().zipWithIndex(), emptyIterable());
        }

        @Test
        void findReturnsNothing() {
            assertEquals(nothing(), Vector.empty().find(constantly(true)));
        }

        @Test
        void equalToItself() {
            assertEquals(Vector.empty(), Vector.empty());
        }

    }

    @Nested
    @DisplayName("copyFrom")
    class CopyFrom {

        @Nested
        @DisplayName("copyFrom array")
        class CopyFromArray {

            @Test
            void throwsOnNullArgument() {
                Integer[] arr = null;
                assertThrows(NullPointerException.class, () -> Vector.copyFrom(arr));
            }

            @Test
            void makesCopy() {
                Integer[] arr = new Integer[]{1, 2, 3};
                ImmutableVector<Integer> subject = Vector.copyFrom(arr);
                assertThat(subject, contains(1, 2, 3));
                arr[0] = 4;
                assertThat(subject, contains(1, 2, 3));
            }

            @Test
            void getWillNeverReturnNull() {
                ImmutableVector<String> subject = Vector.copyFrom(new String[]{"foo", null, "baz"});
                assertEquals(just("foo"), subject.get(0));
                assertEquals(nothing(), subject.get(1));
                assertEquals(just("baz"), subject.get(2));
            }

            @Test
            void iteratorNextReturnsCorrectElements() {
                ImmutableVector<String> subject = Vector.copyFrom(new String[]{"foo", "bar", "baz"});
                Iterator<String> iterator = subject.iterator();
                assertEquals("foo", iterator.next());
                assertEquals("bar", iterator.next());
                assertEquals("baz", iterator.next());
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            void iteratorHasNextCanBeCalledMultipleTimes() {
                ImmutableVector<String> subject = Vector.copyFrom(new String[]{"foo", "bar", "baz"});
                Iterator<String> iterator = subject.iterator();
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertEquals("foo", iterator.next());
            }

            @Test
            void iteratorHasNextReturnsFalseIfNothingRemains() {
                ImmutableVector<String> subject = Vector.copyFrom(new String[]{"foo"});
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertFalse(iterator.hasNext());
            }

            @Test
            void iteratorNextThrowsIfNothingRemains() {
                ImmutableVector<String> subject = Vector.copyFrom(new String[]{"foo"});
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertThrows(NoSuchElementException.class, iterator::next);
            }

            @Test
            void iteratorThrowsIfRemoveIsCalled() {
                ImmutableVector<String> subject = Vector.copyFrom(new String[]{"foo"});
                Iterator<String> iterator = subject.iterator();
                assertThrows(UnsupportedOperationException.class, iterator::remove);
            }

            @Nested
            @DisplayName("copyFrom size 3 array")
            class CopyFromSize3Array {
                private ImmutableVector<String> subject;
                private String[] underlying;

                @BeforeEach
                void beforeEach() {
                    underlying = new String[]{"foo", "bar", "baz"};
                    subject = Vector.copyFrom(underlying);
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
                void getForValidIndices() {
                    assertEquals(just("foo"), subject.get(0));
                    assertEquals(just("bar"), subject.get(1));
                    assertEquals(just("baz"), subject.get(2));
                }

                @Test
                void getForInvalidIndices() {
                    assertEquals(nothing(), subject.get(3));
                    assertEquals(nothing(), subject.get(-1));
                }

                @Test
                void unsafeGetForValidIndices() {
                    assertEquals("foo", subject.unsafeGet(0));
                    assertEquals("bar", subject.unsafeGet(1));
                    assertEquals("baz", subject.unsafeGet(2));
                }

                @Test
                void unsafeGetThrowsForInvalidIndices() {
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo", "bar", "baz"));
                }

                @Test
                void reverseIteratesCorrectly() {
                    assertThat(subject.reverse(), contains("baz", "bar", "foo"));
                }

                @Test
                void zipWithIndexIteratesCorrectly() {
                    assertThat(subject.zipWithIndex(), contains(tuple("foo", 0), tuple("bar", 1), tuple("baz", 2)));
                }

                @Test
                void toNonEmptySucceeds() {
                    assertEquals(just(Vector.of("foo", "bar", "baz")),
                            subject.toNonEmpty());
                }

                @Test
                void toNonEmptyOrThrowSucceeds() {
                    assertEquals(Vector.of("foo", "bar", "baz"),
                            subject.toNonEmptyOrThrow());
                }

                @Test
                void toImmutableReturnsItself() {
                    assertSame(subject, subject.toImmutable());
                }

                @Test
                void notAffectedByMutation() {
                    underlying[0] = "qwerty";
                    assertThat(subject, contains("foo", "bar", "baz"));
                }

                @Test
                void findPositive() {
                    assertEquals(just("bar"), subject.find(Eq.eq("bar")));
                }

                @Test
                void findNegative() {
                    assertEquals(nothing(), subject.find(Eq.eq("not in list")));
                }

                @Test
                void findIndexPositive() {
                    assertEquals(just(1), subject.findIndex(Eq.eq("bar")));
                }

                @Test
                void findIndexNegative() {
                    assertEquals(nothing(), subject.findIndex(Eq.eq("not in list")));
                }

            }

            @Nested
            @DisplayName("copyFrom size 1 array")
            class CopyFromSize1Array {
                private ImmutableVector<String> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.copyFrom(new String[]{"foo"});
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

            }

            @Nested
            @DisplayName("copyFrom empty array")
            class CopyFromEmptyArray {
                private ImmutableVector<Integer> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.copyFrom(new Integer[]{});
                }

                @Test
                void isEmpty() {
                    assertTrue(subject.isEmpty());
                }

                @Test
                void sizeIsZero() {
                    assertEquals(0, subject.size());
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, emptyIterable());
                }

            }
        }

        @Nested
        @DisplayName("copyFrom List")
        class CopyFromList {

            @Test
            void throwsOnNullArgument() {
                List<Integer> list = null;
                assertThrows(NullPointerException.class, () -> Vector.copyFrom(list));
            }

            @Test
            void makesCopy() {
                List<Integer> list = asList(1, 2, 3);
                Vector<Integer> subject = Vector.copyFrom(list);
                assertThat(subject, contains(1, 2, 3));
                list.set(0, 4);
                assertThat(subject, contains(1, 2, 3));
            }

            @Test
            void getWillNeverReturnNull() {
                Vector<String> subject = Vector.copyFrom(asList("foo", null, "baz"));
                assertEquals(just("foo"), subject.get(0));
                assertEquals(nothing(), subject.get(1));
                assertEquals(just("baz"), subject.get(2));
            }

            @Test
            void iteratorNextReturnsCorrectElements() {
                Vector<String> subject = Vector.copyFrom(asList("foo", "bar", "baz"));
                Iterator<String> iterator = subject.iterator();
                assertEquals("foo", iterator.next());
                assertEquals("bar", iterator.next());
                assertEquals("baz", iterator.next());
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            void iteratorHasNextCanBeCalledMultipleTimes() {
                Vector<String> subject = Vector.copyFrom(asList("foo", "bar", "baz"));
                Iterator<String> iterator = subject.iterator();
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertEquals("foo", iterator.next());
            }

            @Test
            void iteratorHasNextReturnsFalseIfNothingRemains() {
                Vector<String> subject = Vector.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertFalse(iterator.hasNext());
            }

            @Test
            void iteratorNextThrowsIfNothingRemains() {
                Vector<String> subject = Vector.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertThrows(NoSuchElementException.class, iterator::next);
            }

            @Test
            void iteratorThrowsIfRemoveIsCalled() {
                Vector<String> subject = Vector.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                assertThrows(UnsupportedOperationException.class, iterator::remove);
            }

            @Nested
            @DisplayName("copyFrom size 3 List")
            class CopyFromSize3List {
                private Vector<String> subject;
                private List<String> underlying;

                @BeforeEach
                void setUp() {
                    underlying = asList("foo", "bar", "baz");
                    subject = Vector.copyFrom(underlying);
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
                void getForValidIndices() {
                    assertEquals(just("foo"), subject.get(0));
                    assertEquals(just("bar"), subject.get(1));
                    assertEquals(just("baz"), subject.get(2));
                }

                @Test
                void getForInvalidIndices() {
                    assertEquals(nothing(), subject.get(3));
                    assertEquals(nothing(), subject.get(-1));
                }

                @Test
                void unsafeGetForValidIndices() {
                    assertEquals("foo", subject.unsafeGet(0));
                    assertEquals("bar", subject.unsafeGet(1));
                    assertEquals("baz", subject.unsafeGet(2));
                }

                @Test
                void unsafeGetThrowsForInvalidIndices() {
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo", "bar", "baz"));
                }

                @Test
                void reverseIteratesCorrectly() {
                    assertThat(subject.reverse(), contains("baz", "bar", "foo"));
                }

                @Test
                void zipWithIndexIteratesCorrectly() {
                    assertThat(subject.zipWithIndex(), contains(tuple("foo", 0), tuple("bar", 1), tuple("baz", 2)));
                }

                @Test
                void toNonEmptySucceeds() {
                    assertEquals(just(Vector.of("foo", "bar", "baz")),
                            subject.toNonEmpty());
                }

                @Test
                void toNonEmptyOrThrowSucceeds() {
                    assertEquals(Vector.of("foo", "bar", "baz"),
                            subject.toNonEmptyOrThrow());
                }

                @Test
                void toImmutableReturnsItself() {
                    assertSame(subject, subject.toImmutable());
                }

                @Test
                void notAffectedByMutation() {
                    underlying.set(0, "qwerty");
                    assertThat(subject, contains("foo", "bar", "baz"));
                }

                @Test
                void findPositive() {
                    assertEquals(just("bar"), subject.find(Eq.eq("bar")));
                }

                @Test
                void findNegative() {
                    assertEquals(nothing(), subject.find(Eq.eq("not in list")));
                }

                @Test
                void findIndexPositive() {
                    assertEquals(just(1), subject.findIndex(Eq.eq("bar")));
                }

                @Test
                void findIndexNegative() {
                    assertEquals(nothing(), subject.findIndex(Eq.eq("not in list")));
                }

            }

            @Nested
            @DisplayName("copyFrom size 1 List")
            class CopyFromSize1List {

                private ImmutableVector<String> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.copyFrom(singletonList("foo"));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

            }

            @Nested
            @DisplayName("copyFrom empty List")
            class CopyFromEmptyList {
                private ImmutableVector<Integer> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.copyFrom(emptyList());
                }

                @Test
                void isEmpty() {
                    assertTrue(subject.isEmpty());
                }

                @Test
                void sizeIsZero() {
                    assertEquals(0, subject.size());
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, emptyIterable());
                }

                @Test
                void toNonEmptyFails() {
                    assertEquals(nothing(), subject.toNonEmpty());
                }

                @Test
                void toNonEmptyOrThrowThrows() {
                    assertThrows(IllegalArgumentException.class, () -> subject.toNonEmptyOrThrow());
                }

            }
        }

        @Nested
        @DisplayName("copyFrom Iterable")
        class CopyFromIterable {

            @Test
            void throwsOnNullArgument() {
                Iterable<String> source = null;
                assertThrows(NullPointerException.class, () -> Vector.copyFrom(source));
            }

            @Test
            void iteratesCorrectly() {
                Iterable<Integer> source = cons(1, cons(2, cons(3, emptyList())));
                Vector<Integer> subject = Vector.copyFrom(source);
                assertThat(subject, contains(1, 2, 3));
            }

            @Test
            void getWillNeverReturnNull() {
                Vector<String> subject = Vector.copyFrom(asList("foo", null, "baz"));
                assertEquals(just("foo"), subject.get(0));
                assertEquals(nothing(), subject.get(1));
                assertEquals(just("baz"), subject.get(2));
            }

            @Test
            void iteratorNextReturnsCorrectElements() {
                Vector<String> subject = Vector.copyFrom(asList("foo", "bar", "baz"));
                Iterator<String> iterator = subject.iterator();
                assertEquals("foo", iterator.next());
                assertEquals("bar", iterator.next());
                assertEquals("baz", iterator.next());
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            void iteratorHasNextCanBeCalledMultipleTimes() {
                Vector<String> subject = Vector.copyFrom(asList("foo", "bar", "baz"));
                Iterator<String> iterator = subject.iterator();
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertEquals("foo", iterator.next());
            }

            @Test
            void iteratorHasNextReturnsFalseIfNothingRemains() {
                Vector<String> subject = Vector.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertFalse(iterator.hasNext());
            }

            @Test
            void iteratorNextThrowsIfNothingRemains() {
                Vector<String> subject = Vector.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertThrows(NoSuchElementException.class, iterator::next);
            }

            @Test
            void iteratorThrowsIfRemoveIsCalled() {
                Vector<String> subject = Vector.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                assertThrows(UnsupportedOperationException.class, iterator::remove);
            }

            @Nested
            @DisplayName("copyFrom size 3 Iterable")
            class CopyFromSize3Iterable {
                private ImmutableVector<String> subject;
                private Iterable<String> underlying;

                @BeforeEach
                void setUp() {
                    underlying = cons("foo", cons("bar", cons("baz", emptyList())));
                    subject = Vector.copyFrom(underlying);
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
                void getForValidIndices() {
                    assertEquals(just("foo"), subject.get(0));
                    assertEquals(just("bar"), subject.get(1));
                    assertEquals(just("baz"), subject.get(2));
                }

                @Test
                void getForInvalidIndices() {
                    assertEquals(nothing(), subject.get(3));
                    assertEquals(nothing(), subject.get(-1));
                }

                @Test
                void unsafeGetForValidIndices() {
                    assertEquals("foo", subject.unsafeGet(0));
                    assertEquals("bar", subject.unsafeGet(1));
                    assertEquals("baz", subject.unsafeGet(2));
                }

                @Test
                void unsafeGetThrowsForInvalidIndices() {
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo", "bar", "baz"));
                }

                @Test
                void reverseIteratesCorrectly() {
                    assertThat(subject.reverse(), contains("baz", "bar", "foo"));
                }

                @Test
                void zipWithIndexIteratesCorrectly() {
                    assertThat(subject.zipWithIndex(), contains(tuple("foo", 0), tuple("bar", 1), tuple("baz", 2)));
                }

                @Test
                void toNonEmptySucceeds() {
                    assertEquals(just(Vector.of("foo", "bar", "baz")),
                            subject.toNonEmpty());
                }

                @Test
                void toNonEmptyOrThrowSucceeds() {
                    assertEquals(Vector.of("foo", "bar", "baz"),
                            subject.toNonEmptyOrThrow());
                }

                @Test
                void findPositive() {
                    assertEquals(just("bar"), subject.find(Eq.eq("bar")));
                }

                @Test
                void findNegative() {
                    assertEquals(nothing(), subject.find(Eq.eq("not in list")));
                }

                @Test
                void findIndexPositive() {
                    assertEquals(just(1), subject.findIndex(Eq.eq("bar")));
                }

                @Test
                void findIndexNegative() {
                    assertEquals(nothing(), subject.findIndex(Eq.eq("not in list")));
                }

            }

            @Nested
            @DisplayName("copyFrom size 1 Iterable")
            class CopyFromSize1Iterable {

                private ImmutableVector<String> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.copyFrom(cons("foo", emptyList()));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

            }

            @Nested
            @DisplayName("copyFrom empty Iterable")
            class CopyFromEmptyIterable {
                private Vector<Integer> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.copyFrom(Collections::emptyIterator);
                }

                @Test
                void isEmpty() {
                    assertTrue(subject.isEmpty());
                }

                @Test
                void sizeIsZero() {
                    assertEquals(0, subject.size());
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, emptyIterable());
                }

                @Test
                void toNonEmptyFails() {
                    assertEquals(nothing(), subject.toNonEmpty());
                }

                @Test
                void toNonEmptyOrThrowThrows() {
                    assertThrows(IllegalArgumentException.class, () -> subject.toNonEmptyOrThrow());
                }

            }

        }

        @Nested
        @DisplayName("copyFrom ImmutableVector")
        class CopyFromImmutableVector {

            @Test
            void returnsOriginalForPrimitives() {
                ImmutableVector<Integer> original = Vector.of(1, 2, 3);
                assertSame(original, Vector.copyFrom(original));
            }

            @Test
            void makesCopyForNonPrimitives() {
                ImmutableNonEmptyVector<Integer> source = Vector.of(1, 2, 3).fmap(n -> n * 2);
                ImmutableVector<Integer> copied = Vector.copyFrom(source);
                assertTrue(Util.isPrimitive(copied));
                assertNotSame(source, copied);
            }

        }

        @Nested
        @DisplayName("copyFrom with maxCount")
        class CopyFromWithMaxCount {

            @Nested
            @DisplayName("array")
            class CopyFromArrayWithMaxCount {
                private Integer[] source;

                @BeforeEach
                void setUp() {
                    source = new Integer[]{1, 2, 3};
                }

                @Test
                void takesAsMuchAsItCan() {
                    assertThat(Vector.copyFrom(1_000_000, source),
                            contains(1, 2, 3));
                }

                @Test
                void onlyTakesWhatWasAskedFor() {
                    assertThat(Vector.copyFrom(3, source),
                            contains(1, 2, 3));
                    assertThat(Vector.copyFrom(2, source),
                            contains(1, 2));
                    assertThat(Vector.copyFrom(1, source),
                            contains(1));
                    assertThat(Vector.copyFrom(0, source),
                            emptyIterable());
                }

            }

            @Nested
            @DisplayName("Iterable")
            class CopyFromIterableWithMaxCount {
                private Iterable<Integer> source;

                @BeforeEach
                void setUp() {
                    source = cons(1, cons(2, cons(3, emptyList())));
                }

                @Test
                void takesAsMuchAsItCan() {
                    assertThat(Vector.copyFrom(1_000_000, source),
                            contains(1, 2, 3));
                }

                @Test
                void onlyTakesWhatWasAskedFor() {
                    assertThat(Vector.copyFrom(3, source),
                            contains(1, 2, 3));
                    assertThat(Vector.copyFrom(2, source),
                            contains(1, 2));
                    assertThat(Vector.copyFrom(1, source),
                            contains(1));
                    assertThat(Vector.copyFrom(0, source),
                            emptyIterable());
                }

                @Test
                void willNotEvaluateIterableUnlessNecessary() {
                    Iterable<String> poison = () -> {
                        throw new AssertionError("Iterable was evaluated");
                    };
                    assertThat(Vector.copyFrom(0, poison), emptyIterable());
                    assertThat(Vector.copyFrom(1, cons("foo", poison)), contains("foo"));
                }

                @Test
                void safeToUseOnInfiniteIterables() {
                    assertThat(Vector.copyFrom(3, repeat("foo")),
                            contains("foo", "foo", "foo"));
                }

            }

            @Nested
            @DisplayName("ImmutableVector")
            class CopyFromImmutableVectorWithMaxCount {

                @Test
                void returnsOriginalIfMaxCountEqualsSize() {
                    ImmutableVector<Integer> original = Vector.of(1, 2, 3);
                    assertSame(original, Vector.copyFrom(3, original));
                }

                @Test
                void returnsOriginalIfMaxCountGreaterThanSize() {
                    ImmutableVector<Integer> original = Vector.of(1, 2, 3);
                    assertSame(original, Vector.copyFrom(4, original));
                }

                @Test
                void correctIfMaxCountLessThanSize() {
                    ImmutableVector<Integer> original = Vector.of(1, 2, 3);
                    assertThat(Vector.copyFrom(2, original), contains(1, 2));
                }

            }

        }

        @Nested
        @DisplayName("copySliceFrom")
        class CopySliceFrom {

            private Iterable<Integer> finite;
            private Iterable<Integer> infinite;

            @BeforeEach
            void setUp() {
                infinite = cycle(asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
                finite = take(10, infinite);
            }

            @Test
            void takesAsMuchAsItCan() {
                assertThat(Vector.copySliceFrom(5, 15, finite),
                        contains(5, 6, 7, 8, 9));
                assertThat(Vector.copySliceFrom(5, 15, infinite),
                        contains(5, 6, 7, 8, 9, 0, 1, 2, 3, 4));
            }

            @Test
            void onlyTakesWhatWasAskedFor() {
                assertThat(Vector.copySliceFrom(1, 4, infinite),
                        contains(1, 2, 3));
                assertThat(Vector.copySliceFrom(1, 3, infinite),
                        contains(1, 2));
                assertThat(Vector.copySliceFrom(1, 2, infinite),
                        contains(1));
                assertThat(Vector.copySliceFrom(1, 1, infinite),
                        emptyIterable());
            }

            @Test
            void willNotEvaluateIterableUnlessNecessary() {
                Iterable<String> poison = () -> {
                    throw new AssertionError("Iterable was evaluated");
                };
                assertThat(Vector.copySliceFrom(1000, 1000, poison), emptyIterable());
                assertThat(Vector.copySliceFrom(1, 2, cons("foo", cons("bar", poison))),
                        contains("bar"));
            }

            @Test
            void safeToUseOnInfiniteIterables() {
                assertThat(Vector.copySliceFrom(100, 103, infinite),
                        contains(0, 1, 2));
            }

            @Test
            void findPositive() {
                assertEquals(just(1), Vector.copySliceFrom(100, 103, infinite)
                        .find(n -> n >= 1));
            }

            @Test
            void findNegative() {
                assertEquals(nothing(), Vector.copySliceFrom(100, 103, infinite)
                        .find(n -> n >= 5));
            }

            @Test
            void findIndexPositive() {
                assertEquals(just(1), Vector.copySliceFrom(100, 103, infinite)
                        .findIndex(n -> n >= 1));
            }

            @Test
            void findIndexNegative() {
                assertEquals(nothing(), Vector.copySliceFrom(100, 103, infinite)
                        .findIndex(n -> n >= 5));
            }

        }
    }

    @Nested
    @DisplayName("fill")
    class Fill {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> Vector.fill(-1, "foo"));
        }

        @Test
        void countOfZeroReturnsEmptyVector() {
            assertSame(Vector.empty(), Vector.fill(0, "foo"));
        }

        @Test
        void getWillNeverReturnNull() {
            ImmutableVector<String> subject = Vector.fill(3, null);
            assertEquals(nothing(), subject.get(0));
            assertEquals(nothing(), subject.get(1));
            assertEquals(nothing(), subject.get(2));
        }

        @Test
        void iteratorNextReturnsCorrectElements() {
            ImmutableVector<String> subject = Vector.fill(3, "foo");
            Iterator<String> iterator = subject.iterator();
            assertEquals("foo", iterator.next());
            assertEquals("foo", iterator.next());
            assertEquals("foo", iterator.next());
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        void iteratorHasNextCanBeCalledMultipleTimes() {
            ImmutableVector<String> subject = Vector.fill(3, "foo");
            Iterator<String> iterator = subject.iterator();
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertEquals("foo", iterator.next());
        }

        @Test
        void iteratorHasNextReturnsFalseIfNothingRemains() {
            ImmutableVector<String> subject = Vector.fill(1, "foo");
            Iterator<String> iterator = subject.iterator();
            iterator.next();
            assertFalse(iterator.hasNext());
        }

        @Test
        void iteratorNextThrowsIfNothingRemains() {
            ImmutableVector<String> subject = Vector.fill(1, "foo");
            Iterator<String> iterator = subject.iterator();
            iterator.next();
            assertThrows(NoSuchElementException.class, iterator::next);
        }

        @Test
        void iteratorThrowsIfRemoveIsCalled() {
            ImmutableVector<String> subject = Vector.fill(1, "foo");
            Iterator<String> iterator = subject.iterator();
            assertThrows(UnsupportedOperationException.class, iterator::remove);
        }

        @Nested
        @DisplayName("fill size 3")
        class FillSize3 {
            private ImmutableVector<String> subject;

            @BeforeEach
            void setUp() {
                subject = Vector.fill(3, "foo");
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
            void toNonEmptyOrThrowSucceeds() {
                assertEquals(Vector.of("foo", "foo", "foo"),
                        subject.toNonEmptyOrThrow());
            }

            @Test
            void toImmutableReturnsItself() {
                assertSame(subject, subject.toImmutable());
            }

            @Test
            void allIndicesReturnSameReference() {
                Object obj = new Object();
                ImmutableVector<Object> subject = Vector.fill(3, obj);
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

        @Nested
        @DisplayName("take")
        class FillTake {

            private ImmutableVector<String> subject;

            @BeforeEach
            void setUp() {
                subject = Vector.fill(3, "foo");
            }

            @Test
            void takeZero() {
                assertSame(Vector.empty(), subject.take(0));
            }

            @Test
            void takeTooShort() {
                assertEquals(Vector.of("foo", "foo"), subject.take(2));
            }

            @Test
            void takeExact() {
                assertSame(subject, subject.take(3));
            }

            @Test
            void takeTooLong() {
                assertSame(subject, subject.take(1_000_000));
            }

            @Test
            void reverseReturnsSameReference() {
                assertSame(subject.take(1000), subject.take(1000).reverse());
            }

        }

        @Nested
        @DisplayName("drop")
        class FillDrop {

            private ImmutableVector<String> subject;

            @BeforeEach
            void setUp() {
                subject = Vector.fill(3, "foo");
            }

            @Test
            void dropZero() {
                assertSame(subject, subject.drop(0));
            }

            @Test
            void dropTooShort() {
                assertEquals(Vector.of("foo"), subject.drop(2));
            }

            @Test
            void dropExact() {
                assertSame(Vector.empty(), subject.drop(3));
            }

            @Test
            void dropTooLong() {
                assertSame(Vector.empty(), subject.drop(1_000_000));
            }

        }

        @Nested
        @DisplayName("slice")
        class FillSlice {

            private ImmutableVector<String> subject;

            @BeforeEach
            void setUp() {
                subject = Vector.fill(3, "foo");
            }

            @Test
            void sliceFull() {
                assertSame(subject, subject.slice(0, 3));
            }

            @Test
            void sliceShort() {
                assertEquals(Vector.of("foo"), subject.slice(1, 2));
            }

            @Test
            void sliceOutOfBounds() {
                assertSame(Vector.empty(), subject.slice(10, 1000));
            }

            @Test
            void sliceLong() {
                assertEquals(Vector.of("foo", "foo"), subject.slice(1, 10));
            }

        }

        @Nested
        @DisplayName("fmap")
        class Fmap {

            private ImmutableVector<Integer> subject;

            @BeforeEach
            void beforeEach() {
                subject = Vector.fill(3, 100);
            }

            @Test
            void throwsOnNullFunction() {
                assertThrows(NullPointerException.class, () -> subject.fmap(null));
            }

            @Test
            void fmap() {
                assertThat(subject.fmap(n -> n * 2), contains(200, 200, 200));
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
            void stackSafe() {
                ImmutableVector<Integer> mapped = foldLeft((acc, __) -> acc.fmap(n -> n + 1),
                        subject, replicate(10_000, UNIT));
                assertThat(mapped, contains(10_100, 10_100, 10_100));
            }

            @Test
            void reverseIteratesCorrectly() {
                assertThat(subject.fmap(Object::toString).reverse(),
                        contains("100", "100", "100"));
            }

        }

    }

    @Nested
    @DisplayName("lazyFill")
    class LazyFill {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> Vector.lazyFill(-1, n -> n * 10));
        }

        @Test
        void countOfZeroReturnsEmptyVector() {
            assertSame(Vector.empty(), Vector.lazyFill(0, n -> n * 10));
        }

        @Test
        void getWillNeverReturnNull() {
            ImmutableVector<String> subject = Vector.lazyFill(3, n -> null);
            assertEquals(nothing(), subject.get(0));
            assertEquals(nothing(), subject.get(1));
            assertEquals(nothing(), subject.get(2));
        }

        @Test
        void iteratorNextReturnsCorrectElements() {
            ImmutableVector<Integer> subject = Vector.lazyFill(3, n -> n * 10);
            Iterator<Integer> iterator = subject.iterator();
            assertEquals(0, iterator.next());
            assertEquals(10, iterator.next());
            assertEquals(20, iterator.next());
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        void iteratorHasNextCanBeCalledMultipleTimes() {
            ImmutableVector<Integer> subject = Vector.lazyFill(3, n -> n * 10);
            Iterator<Integer> iterator = subject.iterator();
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertEquals(0, iterator.next());
        }

        @Test
        void iteratorHasNextReturnsFalseIfNothingRemains() {
            ImmutableVector<Integer> subject = Vector.lazyFill(1, n -> n * 10);
            Iterator<Integer> iterator = subject.iterator();
            iterator.next();
            assertFalse(iterator.hasNext());
        }

        @Test
        void iteratorNextThrowsIfNothingRemains() {
            ImmutableVector<Integer> subject = Vector.lazyFill(1, n -> n * 10);
            Iterator<Integer> iterator = subject.iterator();
            iterator.next();
            assertThrows(NoSuchElementException.class, iterator::next);
        }

        @Test
        void iteratorThrowsIfRemoveIsCalled() {
            ImmutableVector<Integer> subject = Vector.lazyFill(1, n -> n * 10);
            Iterator<Integer> iterator = subject.iterator();
            assertThrows(UnsupportedOperationException.class, iterator::remove);
        }

        @Nested
        @DisplayName("lazyFill size 3")
        class LazyFillSize3 {
            private ImmutableVector<Integer> subject;

            @BeforeEach
            void setUp() {
                subject = Vector.lazyFill(3, n -> n * 10);
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
            void toNonEmptyOrThrowSucceeds() {
                assertEquals(Vector.of(0, 10, 20),
                        subject.toNonEmptyOrThrow());
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

        @Nested
        @DisplayName("take")
        class LazyFillTake {

            private ImmutableVector<Integer> subject;

            @BeforeEach
            void setUp() {
                subject = Vector.lazyFill(3, n -> n * 10);
            }

            @Test
            void takeZero() {
                assertSame(Vector.empty(), subject.take(0));
            }

            @Test
            void takeTooShort() {
                assertEquals(Vector.of(0, 10), subject.take(2));
            }

            @Test
            void takeExact() {
                assertSame(subject, subject.take(3));
            }

            @Test
            void takeTooLong() {
                assertSame(subject, subject.take(1_000_000));
            }

        }

        @Nested
        @DisplayName("drop")
        class LazyFillDrop {

            private ImmutableVector<Integer> subject;

            @BeforeEach
            void setUp() {
                subject = Vector.lazyFill(3, n -> n * 10);
            }

            @Test
            void dropZero() {
                assertSame(subject, subject.drop(0));
            }

            @Test
            void dropTooShort() {
                assertEquals(Vector.of(20), subject.drop(2));
            }

            @Test
            void dropExact() {
                assertSame(Vector.empty(), subject.drop(3));
            }

            @Test
            void dropTooLong() {
                assertSame(Vector.empty(), subject.drop(1_000_000));
            }

            @Test
            void stackSafe() {
                ImmutableVector<Integer> huge = Vector.lazyFill(1_000_000, Id.id());
                ImmutableVector<Integer> dropped = foldLeft((acc, __) -> acc.drop(1), huge, replicate(10_000, UNIT));
                assertEquals(10_000, dropped.unsafeGet(0));
            }

        }

        @Nested
        @DisplayName("slice")
        class LazyFillSlice {

            private ImmutableVector<Integer> subject;

            @BeforeEach
            void setUp() {
                subject = Vector.lazyFill(3, n -> n * 10);
            }

            @Test
            void sliceFull() {
                assertSame(subject, subject.slice(0, 3));
            }

            @Test
            void sliceShort() {
                assertEquals(Vector.of(10), subject.slice(1, 2));
            }

            @Test
            void sliceOutOfBounds() {
                assertSame(Vector.empty(), subject.slice(10, 1000));
            }

            @Test
            void sliceLong() {
                assertEquals(Vector.of(10, 20), subject.slice(1, 10));
            }

            @Test
            void reverseIteratesCorrectly() {
                assertThat(subject.slice(1, 3).reverse(), contains(20, 10));
            }

        }

    }

    @Nested
    @DisplayName("range")
    class Range {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> Vector.range(-1));
        }

        @Test
        void countOfZeroReturnsEmptyVector() {
            assertSame(Vector.empty(), Vector.range(0));
        }

        @Test
        void iteratesCorrectly() {
            assertThat(Vector.range(10), contains(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        }

        @Test
        void sizeIsCorrect() {
            assertEquals(Integer.MAX_VALUE, Vector.range(Integer.MAX_VALUE).size());
        }

        @Test
        void getInRange() {
            assertEquals(just(1_000_000_000), Vector.range(Integer.MAX_VALUE).get(1_000_000_000));
        }

        @Test
        void getOutOfRange() {
            assertEquals(nothing(), Vector.range(10).get(1_000_000_000));
            assertEquals(nothing(), Vector.range(10).get(-1));
        }

        @Test
        void reverseIteratesCorrectly() {
            assertThat(Vector.range(10).reverse(), contains(9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        }

        @Test
        void equality() {
            assertEquals(Vector.of(0, 1, 2, 3), Vector.range(4));
            assertEquals(Vector.range(4), Vector.of(0, 1, 2, 3));
        }

    }

    @Nested
    @DisplayName("fmap")
    class Fmap {

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

        @Test
        void reverseIteratesCorrectly() {
            assertThat(subject.fmap(Object::toString).reverse(),
                    contains("3", "2", "1"));
        }
    }

    @Nested
    @DisplayName("take")
    class Take {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> Vector.copyFrom(singletonList(1)).take(-1));
        }

        @Test
        void countZeroReturnsEmptyVector() {
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).take(0));
        }

        @Test
        void takeAllReturnsSameReference() {
            ImmutableVector<Integer> source = Vector.copyFrom(asList(1, 2, 3));
            ImmutableVector<Integer> sliced = source.take(3);
            assertSame(source, sliced);
        }

        @Test
        void takesAsMuchAsItCan() {
            assertThat(Vector.copyFrom(asList(1, 2, 3)).take(1_000_000),
                    contains(1, 2, 3));
        }

        @Test
        void onlyTakesWhatWasAskedFor() {
            assertThat(Vector.copyFrom(asList(1, 2, 3)).take(3),
                    contains(1, 2, 3));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).take(2),
                    contains(1, 2));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).take(1),
                    contains(1));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).take(0),
                    emptyIterable());
        }

        @Test
        void reverseIteratesCorrectly() {
            assertThat(Vector.copyFrom(asList(1, 2, 3, 4, 5, 6)).take(3).reverse(),
                    contains(3, 2, 1));
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

        @Test
        void stackSafe() {
            int BIG = 50_000;
            Integer[] underlying = new Integer[BIG];
            for (int i = 0; i < BIG; i++) {
                underlying[i] = i;
            }
            ImmutableVector<Integer> sliced = Vector.copyFrom(underlying);
            for (int i = BIG; i > 0; i--) {
                sliced = sliced.take(i);
            }
            assertEquals(just(0), sliced.get(0));
        }

    }

    @Nested
    @DisplayName("takeRight")
    class TakeRight {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> Vector.copyFrom(singletonList(1)).takeRight(-1));
        }

        @Test
        void takesAsMuchAsItCan() {
            assertThat(Vector.copyFrom(asList(1, 2, 3)).takeRight(1_000_000),
                    contains(1, 2, 3));
        }

        @Test
        void onlyTakesWhatWasAskedFor() {
            assertThat(Vector.copyFrom(asList(1, 2, 3)).takeRight(3),
                    contains(1, 2, 3));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).takeRight(2),
                    contains(2, 3));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).takeRight(1),
                    contains(3));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).takeRight(0),
                    emptyIterable());
        }

        @Test
        void reverseIteratesCorrectly() {
            assertThat(Vector.copyFrom(asList(1, 2, 3, 4, 5, 6)).takeRight(3).reverse(),
                    contains(6, 5, 4));
        }

        @Test
        void notAffectedByMutation() {
            List<String> originalUnderlying = asList("foo", "bar", "baz");
            ImmutableVector<String> original = Vector.copyFrom(originalUnderlying);
            Vector<String> sliced = original.takeRight(2);
            assertThat(sliced, contains("bar", "baz"));
            originalUnderlying.set(1, "qwerty");
            assertThat(sliced, contains("bar", "baz"));
        }

        @Test
        void returnsOriginalVectorReferenceIfPossible() {
            ImmutableVector<String> original = Vector.copyFrom(asList("foo", "bar", "baz"));
            ImmutableVector<String> slice1 = original.takeRight(100);
            ImmutableVector<String> slice2 = original.takeRight(3);
            assertSame(original, slice1);
            assertSame(original, slice2);
        }

    }

    @Nested
    @DisplayName("takeWhile")
    class TakeWhile {

        @Test
        void throwsOnNullPredicate() {
            assertThrows(NullPointerException.class, () -> Vector.copyFrom(singletonList(1)).takeWhile(null));
        }

        @Test
        void neverTakeReturnsEmptyVector() {
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).takeWhile(constantly(false)));
        }

        @Test
        void alwaysTakeReturnsSameReference() {
            ImmutableVector<Integer> source = Vector.copyFrom(asList(1, 2, 3));
            ImmutableVector<Integer> sliced = source.takeWhile(constantly(true));
            assertSame(source, sliced);
        }

        @Test
        void takesAsMuchAsItCan() {
            assertThat(Vector.copyFrom(asList(1, 2, 3)).takeWhile(constantly(true)),
                    contains(1, 2, 3));
        }

        @Test
        void onlyTakesWhatWasAskedFor() {
            assertThat(Vector.copyFrom(asList(1, 2, 3)).takeWhile(n -> n < 4),
                    contains(1, 2, 3));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).takeWhile(n -> n < 3),
                    contains(1, 2));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).takeWhile(n -> n < 2),
                    contains(1));
            assertThat(Vector.copyFrom(asList(1, 2, 3)).takeWhile(n -> n < 1),
                    emptyIterable());
        }

        @Test
        void reverseIteratesCorrectly() {
            assertThat(Vector.copyFrom(asList(1, 2, 3, 4, 5, 6)).takeWhile(n -> n < 4).reverse(),
                    contains(3, 2, 1));
        }

        @Test
        void notAffectedByMutation() {
            List<String> originalUnderlying = asList("baz", "bar", "foo");
            ImmutableVector<String> original = Vector.copyFrom(originalUnderlying);
            Vector<String> sliced = original.takeWhile(s -> s.startsWith("b"));
            assertThat(sliced, contains("baz", "bar"));
            originalUnderlying.set(0, "qwerty");
            assertThat(sliced, contains("baz", "bar"));
        }

        @Test
        void returnsOriginalVectorReferenceIfPossible() {
            ImmutableVector<String> original = Vector.copyFrom(asList("foo", "bar", "baz"));
            ImmutableVector<String> slice1 = original.takeWhile(s -> s.length() == 3);
            ImmutableVector<String> slice2 = original.takeWhile(constantly(true));
            assertSame(original, slice1);
            assertSame(original, slice2);
        }

    }

    @Nested
    @DisplayName("drop")
    class Drop {

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
        void reverseIteratesCorrectly() {
            assertThat(Vector.copyFrom(asList(1, 2, 3, 4, 5, 6)).drop(3).reverse(),
                    contains(6, 5, 4));
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

        @Test
        void stackSafe() {
            int BIG = 50_000;
            Integer[] underlying = new Integer[BIG];
            for (int i = 0; i < BIG; i++) {
                underlying[i] = i;
            }
            ImmutableVector<Integer> sliced = Vector.copyFrom(underlying);
            for (int i = 0; i < BIG - 1; i++) {
                sliced = sliced.drop(1);
            }
            assertEquals(just(BIG - 1), sliced.get(0));
        }

    }

    @Nested
    @DisplayName("dropRight")
    class DropRight {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> Vector.copyFrom(singletonList(1)).dropRight(-1));
        }

        @Test
        void countZeroReturnsSameReference() {
            ImmutableVector<Integer> source = Vector.copyFrom(asList(1, 2, 3));
            ImmutableVector<Integer> sliced = source.dropRight(0);
            assertSame(source, sliced);
        }

        @Test
        void countEqualToSizeReturnsEmptyVector() {
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).dropRight(3));
        }

        @Test
        void countExceedingSizeReturnsEmptyVector() {
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).dropRight(4));
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).dropRight(1_000_000));
        }

        @Test
        void oneElement() {
            ImmutableVector<Integer> source = Vector.copyFrom(asList(1, 2, 3));
            assertThat(source.dropRight(1), contains(1, 2));
        }

        @Test
        void twoElements() {
            ImmutableVector<Integer> source = Vector.copyFrom(asList(1, 2, 3));
            assertThat(source.dropRight(2), contains(1));
        }

        @Test
        void reverseIteratesCorrectly() {
            assertThat(Vector.copyFrom(asList(1, 2, 3, 4, 5, 6)).dropRight(3).reverse(),
                    contains(3, 2, 1));
        }

        @Test
        void notAffectedByMutation() {
            List<String> originalUnderlying = asList("foo", "bar", "baz");
            ImmutableVector<String> original = Vector.copyFrom(originalUnderlying);
            Vector<String> sliced = original.dropRight(1);
            assertThat(sliced, contains("foo", "bar"));
            originalUnderlying.set(1, "qwerty");
            assertThat(sliced, contains("foo", "bar"));
        }

    }

    @Nested
    @DisplayName("dropWhile")
    class DropWhile {

        @Test
        void throwsOnNullPredicate() {
            assertThrows(NullPointerException.class, () -> Vector.copyFrom(singletonList(1)).dropWhile(null));
        }

        @Test
        void neverDropReturnsSameReference() {
            ImmutableVector<Integer> source = Vector.copyFrom(asList(1, 2, 3));
            ImmutableVector<Integer> sliced = source.dropWhile(constantly(false));
            assertSame(source, sliced);
        }

        @Test
        void alwaysDropReturnsEmptyVector() {
            assertEquals(Vector.empty(), Vector.copyFrom(asList(1, 2, 3)).dropWhile(constantly(true)));
        }

        @Test
        void dropOneElement() {
            ImmutableVector<Integer> source = Vector.copyFrom(asList(1, 2, 3));
            assertThat(source.dropWhile(n -> n < 2), contains(2, 3));
        }

        @Test
        void reverseIteratesCorrectly() {
            assertThat(Vector.copyFrom(asList(1, 2, 3, 4, 5, 6)).dropWhile(n -> n < 4).reverse(),
                    contains(6, 5, 4));
        }

        @Test
        void notAffectedByMutation() {
            List<String> originalUnderlying = asList("foo", "bar", "baz");
            ImmutableVector<String> original = Vector.copyFrom(originalUnderlying);
            Vector<String> sliced = original.dropWhile(s -> s.startsWith("f"));
            assertThat(sliced, contains("bar", "baz"));
            originalUnderlying.set(1, "qwerty");
            assertThat(sliced, contains("bar", "baz"));
        }

    }

    @Nested
    @DisplayName("slice")
    class Slice {

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

        @Test
        void reverseIteratesCorrectly() {
            assertThat(Vector.copyFrom(asList(1, 2, 3, 4, 5, 6)).slice(1, 4).reverse(),
                    contains(4, 3, 2));
        }

    }

    @Nested
    @DisplayName("reverse")
    class Reverse {

        @Test
        void returnsEmptyVectorIfEmpty() {
            assertSame(Vector.empty(), Vector.empty().reverse());
        }

        @Test
        void returnsSelfIfOneElement() {
            Vector<Integer> subject = Vector.of(1);
            assertSame(subject, subject.reverse());
        }

        @Test
        void threeElements() {
            assertEquals(Vector.of(3, 2, 1), Vector.of(1, 2, 3).reverse());
        }

        @Test
        void doubleReverseReturnsOriginalReference() {
            Vector<Integer> subject = Vector.of(1, 2, 3);
            assertSame(subject, subject.reverse().reverse());
        }

        @Test
        void notAffectedByMutation() {
            List<String> underlying = asList("foo", "bar", "baz");
            ImmutableVector<String> original = Vector.copyFrom(underlying);
            underlying.set(1, "qwerty");
            assertThat(original, contains("foo", "bar", "baz"));
            assertThat(original.reverse(), contains("baz", "bar", "foo"));
        }

        @Test
        void equality() {
            assertEquals(Vector.of(3, 2, 1),
                    Vector.of(1, 2, 3).reverse());
        }

    }

    @Nested
    @DisplayName("zipWithIndex")
    class ZipWithIndex {

        private ImmutableVector<String> subject;

        @BeforeEach
        void beforeEach() {
            subject = Vector.copyFrom(new String[]{"foo", "bar", "baz"});
        }

        @Test
        void beforeFmap() {
            assertThat(subject.zipWithIndex().fmap(t -> t._1() + t._2()),
                    contains("foo0", "bar1", "baz2"));
        }

        @Test
        void afterFmap() {
            assertThat(subject.fmap(t -> t + "!").zipWithIndex(),
                    contains(tuple("foo!", 0), tuple("bar!", 1), tuple("baz!", 2)));
        }

        @Test
        void beforeReverse() {
            assertThat(subject.zipWithIndex().reverse(),
                    contains(tuple("baz", 2), tuple("bar", 1), tuple("foo", 0)));
        }

        @Test
        void afterReverse() {
            assertThat(subject.reverse().zipWithIndex(),
                    contains(tuple("baz", 0), tuple("bar", 1), tuple("foo", 2)));
        }

        @Test
        void beforeTake() {
            assertThat(subject.zipWithIndex().take(2),
                    contains(tuple("foo", 0), tuple("bar", 1)));
        }

        @Test
        void afterTake() {
            assertThat(subject.take(2).zipWithIndex(),
                    contains(tuple("foo", 0), tuple("bar", 1)));
        }

        @Test
        void beforeDrop() {
            assertThat(subject.zipWithIndex().drop(1),
                    contains(tuple("bar", 1), tuple("baz", 2)));
        }

        @Test
        void afterDrop() {
            assertThat(subject.drop(1).zipWithIndex(),
                    contains(tuple("bar", 0), tuple("baz", 1)));
        }

        @Test
        void notAffectedByMutation() {
            List<String> underlying = asList("foo", "bar", "baz");
            ImmutableVector<String> original = Vector.copyFrom(underlying);
            ImmutableVector<Tuple2<String, Integer>> subject = original.zipWithIndex();
            underlying.set(1, "qwerty");
            assertThat(original, contains("foo", "bar", "baz"));
            assertThat(subject, contains(tuple("foo", 0), tuple("bar", 1), tuple("baz", 2)));
        }

        @Test
        void equality() {
            assertEquals(Vector.copyFrom(asList(1, 2, 3)).zipWithIndex(),
                    Vector.copyFrom(new Integer[]{1, 2, 3}).zipWithIndex());
        }

    }

    @Nested
    @DisplayName("findByIndex")
    class FindByIndex {

        @Test
        void returnsFirstFound() {
            assertEquals(just(1), Vector.copyFrom(new String[]{"foo", "bar", "baz", "bar", "foo"})
                    .findIndex(Eq.eq("bar")));
        }

        @Test
        void correctWhenReversed() {
            assertEquals(just(2), Vector.copyFrom(new String[]{"foo", "bar", "baz"})
                    .reverse()
                    .findIndex(Eq.eq("foo")));
        }

    }

    @Nested
    @DisplayName("cross")
    class Cross {

        private String[] underlying1;
        private List<Integer> underlying2;
        private ImmutableVector<String> first;
        private ImmutableVector<Integer> second;

        @BeforeEach
        void setUp() {
            underlying1 = new String[]{"foo", "bar", "baz"};
            first = Vector.copyFrom(underlying1);
            underlying2 = asList(1, 2, 3);
            second = Vector.copyFrom(underlying2);
        }

        @Test
        void crossWithEmptyVectorIsEmpty() {
            assertEquals(Vector.empty(), first.cross(Vector.empty()));
        }

        @Test
        void emptyVectorCrossAnythingEmpty() {
            assertEquals(Vector.empty(), Vector.empty().cross(first));
        }

        @Test
        void iteratesCorrectly() {
            assertThat(first.cross(second), contains(
                    tuple("foo", 1), tuple("foo", 2), tuple("foo", 3),
                    tuple("bar", 1), tuple("bar", 2), tuple("bar", 3),
                    tuple("baz", 1), tuple("baz", 2), tuple("baz", 3)));
        }

        @Test
        void sizeIsProductOfComponentSizes() {
            assertEquals(27, first.cross(Vector.range(9)).size());
        }

        @Test
        void notAffectedByMutation() {
            ImmutableVector<Tuple2<String, Integer>> subject = first.cross(second);
            underlying1[0] = "qwerty";
            underlying2.set(1, 10);
            assertThat(subject, contains(
                    tuple("foo", 1), tuple("foo", 2), tuple("foo", 3),
                    tuple("bar", 1), tuple("bar", 2), tuple("bar", 3),
                    tuple("baz", 1), tuple("baz", 2), tuple("baz", 3)));
        }

    }

    @Nested
    @DisplayName("zipWith")
    class ZipWith {

        private String[] underlying1;
        private List<Integer> underlying2;
        private ImmutableVector<String> first;
        private ImmutableVector<Integer> second;

        @BeforeEach
        void setUp() {
            underlying1 = new String[]{"foo", "bar", "baz", "qux"};
            first = Vector.copyFrom(underlying1);
            underlying2 = asList(1, 2, 3);
            second = Vector.copyFrom(underlying2);
        }

        @Test
        void zipWithEmptyVectorIsEmpty() {
            assertEquals(Vector.empty(), first.zipWith(tupler(), Vector.empty()));
        }

        @Test
        void emptyVectorZipWithAnythingEmpty() {
            assertEquals(Vector.empty(), Vector.empty().zipWith(tupler(), first));
        }

        @Test
        void iteratesCorrectly() {
            assertThat(first.zipWith(tupler(), second),
                    contains(tuple("foo", 1), tuple("bar", 2), tuple("baz", 3)));
        }

        @Test
        void sizeIsShorterOfComponentSizes() {
            assertEquals(4, first.zipWith(tupler(), Vector.range(9)).size());
        }

        @Test
        void notAffectedByMutation() {
            ImmutableVector<Tuple2<String, Integer>> subject = first.zipWith(tupler(), second);
            underlying1[0] = "qwerty";
            underlying2.set(1, 10);
            assertThat(subject, contains(tuple("foo", 1), tuple("bar", 2), tuple("baz", 3)));
        }

    }

    @Nested
    @DisplayName("indices")
    class Indices {

        @Test
        void returnsEmptyVectorIfEmpty() {
            assertSame(Vector.empty(), Vector.empty().indices());
        }

        @Test
        void iteratesCorrectly() {
            assertThat(Vector.of("foo", "bar", "baz").indices(), contains(0, 1, 2));
        }

    }

    @Nested
    @DisplayName("inits")
    class Inits {

        @Test
        void emptyVector() {
            assertThat(Vector.copyFrom(emptyList()).inits(), contains(Vector.empty()));
        }

        @Test
        void vectorSize1() {
            assertThat(Vector.copyFrom(singletonList("foo")).inits(),
                    contains(Vector.of("foo"),
                            Vector.empty()));
        }

        @Test
        void vectorSize2() {
            assertThat(Vector.copyFrom(asList("foo", "bar")).inits(),
                    contains(Vector.of("foo", "bar"),
                            Vector.of("foo"),
                            Vector.empty()));
        }

        @Test
        void vectorSize3() {
            assertThat(Vector.copyFrom(asList("foo", "bar", "baz")).inits(),
                    contains(Vector.of("foo", "bar", "baz"),
                            Vector.of("foo", "bar"),
                            Vector.of("foo"),
                            Vector.empty()));
        }

    }

}
