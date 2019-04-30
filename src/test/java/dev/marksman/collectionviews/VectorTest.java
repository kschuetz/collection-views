package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Replicate.replicate;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class VectorTest {

    @Nested
    @DisplayName("empty")
    class EmptyVectorTests {

        @Nested
        @DisplayName("array")
        class EmptyArray {

            @Test
            void alwaysYieldsSameReference() {
                Vector<Integer> v1 = Vector.wrap(new Integer[]{});
                Vector<String> v2 = Vector.wrap(new String[]{});
                assertSame(v1, v2);
            }

            @Test
            void isEmpty() {
                assertTrue(Vector.wrap(new Integer[]{}).isEmpty());
            }

            @Test
            void sizeIsZero() {
                assertEquals(0, Vector.wrap(new Integer[]{}).size());
            }

            @Test
            void getReturnsNothing() {
                Vector<Object> subject = Vector.wrap(new Integer[]{});
                assertEquals(nothing(), subject.get(0));
                assertEquals(nothing(), subject.get(1));
                assertEquals(nothing(), subject.get(-1));
            }

            @Test
            void unsafeGetThrows() {
                Vector<Object> subject = Vector.wrap(new Integer[]{});
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(0));
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(Vector.wrap(new Integer[]{}), emptyIterable());
            }

            @Test
            void tailIsEmpty() {
                assertThat(Vector.wrap(new Integer[]{}).tail(), emptyIterable());
            }

        }

        @Nested
        @DisplayName("List")
        class EmptyList {

            @Test
            void alwaysYieldsSameReference() {
                Vector<Integer> v1 = Vector.wrap(emptyList());
                Vector<String> v2 = Vector.wrap(emptyList());
                assertSame(v1, v2);
            }

            @Test
            void isEmpty() {
                assertTrue(Vector.wrap(emptyList()).isEmpty());
            }

            @Test
            void sizeIsZero() {
                assertEquals(0, Vector.wrap(emptyList()).size());
            }

            @Test
            void getReturnsNothing() {
                Vector<Object> subject = Vector.wrap(emptyList());
                assertEquals(nothing(), subject.get(0));
                assertEquals(nothing(), subject.get(1));
                assertEquals(nothing(), subject.get(-1));
            }

            @Test
            void unsafeGetThrows() {
                Vector<Object> subject = Vector.wrap(emptyList());
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(0));
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(Vector.wrap(emptyList()), emptyIterable());
            }

            @Test
            void tailIsEmpty() {
                assertThat(Vector.wrap(emptyList()).tail(), emptyIterable());
            }

        }
        
    }

    @Nested
    @DisplayName("wrap")
    class WrapTests {

        @Nested
        @DisplayName("wrap array")
        class WrapArrayTests {

            @Test
            void throwsOnNullArgument() {
                Integer[] arr = null;
                assertThrows(NullPointerException.class, () -> Vector.wrap(arr));
            }

            @Test
            void willNotMakeCopy() {
                Integer[] arr = new Integer[]{1, 2, 3};
                Vector<Integer> subject = Vector.wrap(arr);
                assertThat(subject, contains(1, 2, 3));
                arr[0] = 4;
                assertThat(subject, contains(4, 2, 3));
            }

            @Test
            void tailWillNotMakeCopy() {
                Integer[] arr = new Integer[]{1, 2, 3};
                Vector<Integer> subject = Vector.wrap(arr);
                assertThat(subject.tail(), contains(2, 3));
                arr[2] = 4;
                assertThat(subject.tail(), contains(2, 4));
            }

            @Test
            void getWillNeverReturnNull() {
                Vector<String> subject = Vector.wrap(new String[]{"foo", null, "baz"});
                assertEquals(just("foo"), subject.get(0));
                assertEquals(nothing(), subject.get(1));
                assertEquals(just("baz"), subject.get(2));
            }

            @Test
            void iteratorNextReturnsCorrectElements() {
                Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
                Iterator<String> iterator = subject.iterator();
                assertEquals("foo", iterator.next());
                assertEquals("bar", iterator.next());
                assertEquals("baz", iterator.next());
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            void iteratorHasNextCanBeCalledMultipleTimes() {
                Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
                Iterator<String> iterator = subject.iterator();
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertEquals("foo", iterator.next());
            }

            @Test
            void iteratorHasNextReturnsFalseIfNothingRemains() {
                Vector<String> subject = Vector.wrap(new String[]{"foo"});
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertFalse(iterator.hasNext());
            }

            @Test
            void iteratorNextThrowsIfNothingRemains() {
                Vector<String> subject = Vector.wrap(new String[]{"foo"});
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertThrows(NoSuchElementException.class, iterator::next);
            }

            @Test
            void iteratorThrowsIfRemoveIsCalled() {
                Vector<String> subject = Vector.wrap(new String[]{"foo"});
                Iterator<String> iterator = subject.iterator();
                assertThrows(UnsupportedOperationException.class, iterator::remove);
            }

            @Nested
            @DisplayName("wrap size 3 array")
            class WrapArray3Tests {
                private Vector<String> subject;
                private String[] underlying;

                @BeforeEach
                void beforeEach() {
                    underlying = new String[]{"foo", "bar", "baz"};
                    subject = Vector.wrap(underlying);
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
                void tailIteratesCorrectly() {
                    assertThat(subject.tail(), contains("bar", "baz"));
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
                void toImmutableIsUnaffectedByMutation() {
                    ImmutableVector<String> immutable = subject.toImmutable();
                    underlying[0] = "qwerty";
                    assertThat(subject, contains("qwerty", "bar", "baz"));
                    assertThat(immutable, contains("foo", "bar", "baz"));
                }

            }

            @Nested
            @DisplayName("wrap size 1 array")
            class WrapSingletonArrayTests {
                private Vector<String> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.wrap(new String[]{"foo"});
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

                @Test
                void tailIsEmpty() {
                    assertTrue(subject.tail().isEmpty());
                    assertThat(subject.tail(), emptyIterable());
                }

            }

            @Nested
            @DisplayName("wrap empty array")
            class WrapEmptyArrayTests {
                private Vector<Integer> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.wrap(new Integer[]{});
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
                void tailIsEmpty() {
                    assertTrue(subject.tail().isEmpty());
                    assertThat(subject.tail(), emptyIterable());
                }
            }
        }

        @Nested
        @DisplayName("wrap List")
        class WrapListTests {

            @Test
            void throwsOnNullArgument() {
                Integer[] arr = null;
                assertThrows(NullPointerException.class, () -> Vector.wrap(arr));
            }

            @Test
            void willNotMakeCopy() {
                List<Integer> list = asList(1, 2, 3);
                Vector<Integer> subject = Vector.wrap(list);
                assertThat(subject, contains(1, 2, 3));
                list.set(0, 4);
                assertThat(subject, contains(4, 2, 3));
            }

            @Test
            void tailWillNotMakeCopy() {
                List<Integer> list = asList(1, 2, 3);
                Vector<Integer> subject = Vector.wrap(list);
                assertThat(subject.tail(), contains(2, 3));
                list.set(2, 4);
                assertThat(subject.tail(), contains(2, 4));
            }

            @Test
            void getWillNeverReturnNull() {
                Vector<String> subject = Vector.wrap(asList("foo", null, "baz"));
                assertEquals(just("foo"), subject.get(0));
                assertEquals(nothing(), subject.get(1));
                assertEquals(just("baz"), subject.get(2));
            }

            @Test
            void iteratorNextReturnsCorrectElements() {
                Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
                Iterator<String> iterator = subject.iterator();
                assertEquals("foo", iterator.next());
                assertEquals("bar", iterator.next());
                assertEquals("baz", iterator.next());
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            void iteratorHasNextCanBeCalledMultipleTimes() {
                Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
                Iterator<String> iterator = subject.iterator();
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertEquals("foo", iterator.next());
            }

            @Test
            void iteratorHasNextReturnsFalseIfNothingRemains() {
                Vector<String> subject = Vector.wrap(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertFalse(iterator.hasNext());
            }

            @Test
            void iteratorNextThrowsIfNothingRemains() {
                Vector<String> subject = Vector.wrap(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertThrows(NoSuchElementException.class, iterator::next);
            }

            @Test
            void iteratorThrowsIfRemoveIsCalled() {
                Vector<String> subject = Vector.wrap(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                assertThrows(UnsupportedOperationException.class, iterator::remove);
            }

            @Nested
            @DisplayName("wrap size 3 List")
            class WrapList3Tests {
                private Vector<String> subject;
                private List<String> underlying;

                @BeforeEach
                void setUp() {
                    underlying = asList("foo", "bar", "baz");
                    subject = Vector.wrap(underlying);
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
                void tailIteratesCorrectly() {
                    assertThat(subject.tail(), contains("bar", "baz"));
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
                void toImmutableIsUnaffectedByMutation() {
                    ImmutableVector<String> immutable = subject.toImmutable();
                    underlying.set(0, "qwerty");
                    assertThat(subject, contains("qwerty", "bar", "baz"));
                    assertThat(immutable, contains("foo", "bar", "baz"));
                }

            }

            @Nested
            @DisplayName("wrap size 1 List")
            class WrapSize1ListTests {

                private Vector<String> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.wrap(singletonList("foo"));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

                @Test
                void tailIsEmpty() {
                    assertTrue(subject.tail().isEmpty());
                    assertThat(subject.tail(), emptyIterable());
                }
            }

            @Nested
            @DisplayName("wrap empty List")
            class WrapEmptyListTests {
                private Vector<Integer> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.wrap(emptyList());
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
                void tailIsEmpty() {
                    assertTrue(subject.tail().isEmpty());
                    assertThat(subject.tail(), emptyIterable());
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
    }

    @Nested
    @DisplayName("fmap")
    class FmapTests {
        @Nested
        @DisplayName("array")
        class ArrayFmapTests {

            @Test
            void throwsOnNullFunction() {
                assertThrows(NullPointerException.class, () -> Vector.wrap(singletonList(1)).fmap(null));
            }

            @Test
            void fmap() {
                Vector<Integer> subject = Vector.wrap(new Integer[]{1, 2, 3});
                assertThat(subject.fmap(Object::toString), contains("1", "2", "3"));
            }

            @Test
            void functorIdentity() {
                Vector<Integer> subject = Vector.wrap(new Integer[]{1, 2, 3});
                assertEquals(subject, subject.fmap(id()));
            }

            @Test
            void functorComposition() {
                Vector<Integer> source = Vector.wrap(new Integer[]{1, 2, 3});
                Fn1<Integer, Integer> f = n -> n * 2;
                Fn1<Integer, String> g = Object::toString;
                assertEquals(source.fmap(f).fmap(g), source.fmap(f.andThen(g)));
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                Integer[] underlying = {1, 2, 3};
                Vector<Integer> subject = Vector.wrap(underlying);
                Vector<Integer> mapped1 = subject.fmap(n -> n * 2);
                Vector<String> mapped2 = mapped1.fmap(Object::toString);
                assertThat(mapped1, contains(2, 4, 6));
                assertThat(mapped2, contains("2", "4", "6"));
                underlying[0] = 10;
                assertThat(mapped1, contains(20, 4, 6));
                assertThat(mapped2, contains("20", "4", "6"));
            }

            @Test
            void stackSafe() {
                Vector<Integer> source = Vector.wrap(new Integer[]{1, 2, 3});
                Vector<Integer> mapped = foldLeft((acc, __) -> acc.fmap(n -> n + 1),
                        source, replicate(10_000, UNIT));
                assertThat(mapped, contains(10_001, 10_002, 10_003));
            }

        }

        @Nested
        @DisplayName("List")
        class ListFmapTests {

            @Test
            void throwsOnNullFunction() {
                assertThrows(NullPointerException.class, () -> Vector.wrap(singletonList(1)).fmap(null));
            }

            @Test
            void fmap() {
                Vector<Integer> subject = Vector.wrap(asList(1, 2, 3));
                assertThat(subject.fmap(Object::toString), contains("1", "2", "3"));
            }

            @Test
            void functorIdentity() {
                Vector<Integer> subject = Vector.wrap(asList(1, 2, 3));
                assertEquals(subject, subject.fmap(id()));
            }

            @Test
            void functorComposition() {
                Vector<Integer> source = Vector.wrap(asList(1, 2, 3));
                Fn1<Integer, Integer> f = n -> n * 2;
                Fn1<Integer, String> g = Object::toString;
                assertEquals(source.fmap(f).fmap(g), source.fmap(f.andThen(g)));
            }

//            @Test
//            void willNotMakeCopiesOfUnderlyingArrays() {
//                Integer[] underlying = {1, 2, 3};
//                Vector<Integer> subject = Vector.wrap(underlying);
//                Vector<Integer> mapped1 = subject.fmap(n -> n * 2);
//                Vector<String> mapped2 = mapped1.fmap(Object::toString);
//                assertThat(mapped1, contains(2, 4, 6));
//                assertThat(mapped2, contains("2", "4", "6"));
//                underlying[0] = 10;
//                assertThat(mapped1, contains(20, 4, 6));
//                assertThat(mapped2, contains("20", "4", "6"));
//            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                List<Integer> underlying = asList(1, 2, 3);
                Vector<Integer> subject = Vector.wrap(underlying);
                Vector<Integer> mapped1 = subject.fmap(n -> n * 2);
                Vector<String> mapped2 = mapped1.fmap(Object::toString);
                assertThat(mapped1, contains(2, 4, 6));
                assertThat(mapped2, contains("2", "4", "6"));
                underlying.set(0, 10);
                assertThat(mapped1, contains(20, 4, 6));
                assertThat(mapped2, contains("20", "4", "6"));
            }

            @Test
            void stackSafe() {
                Vector<Integer> source = Vector.wrap(asList(1, 2, 3));
                Vector<Integer> mapped = foldLeft((acc, __) -> acc.fmap(n -> n + 1),
                        source, replicate(10_000, UNIT));
                assertThat(mapped, contains(10_001, 10_002, 10_003));
            }
        }

    }

    @Nested
    @DisplayName("take")
    class TakeTests {

        @Nested
        @DisplayName("array")
        class ArrayTests {

            @Test
            void throwsOnNegativeCount() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(new Integer[]{1}).take(-1));
            }

            @Test
            void takesAsMuchAsItCan() {
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).take(1_000_000),
                        contains(1, 2, 3));
            }

            @Test
            void onlyTakesWhatWasAskedFor() {
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).take(3),
                        contains(1, 2, 3));
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).take(2),
                        contains(1, 2));
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).take(1),
                        contains(1));
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).take(0),
                        emptyIterable());
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                String[] originalUnderlying = new String[]{"foo", "bar", "baz"};
                Vector<String> original = Vector.wrap(originalUnderlying);
                Vector<String> sliced = original.take(2);
                assertThat(sliced, contains("foo", "bar"));
                originalUnderlying[0] = "qwerty";
                assertThat(sliced, contains("qwerty", "bar"));
            }

            @Test
            void returnsOriginalVectorReferenceIfPossible() {
                Vector<String> original = Vector.wrap(new String[]{"foo", "bar", "baz"});
                Vector<String> slice1 = original.take(100);
                Vector<String> slice2 = original.take(3);
                assertSame(original, slice1);
                assertSame(original, slice2);
            }

        }

        @Nested
        @DisplayName("List")
        class ListTests {
            @Test
            void throwsOnNegativeCount() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(singletonList(1)).take(-1));
            }

            @Test
            void takesAsMuchAsItCan() {
                assertThat(Vector.wrap(asList(1, 2, 3)).take(1_000_000),
                        contains(1, 2, 3));
            }

            @Test
            void onlyTakesWhatWasAskedFor() {
                assertThat(Vector.wrap(asList(1, 2, 3)).take(3),
                        contains(1, 2, 3));
                assertThat(Vector.wrap(asList(1, 2, 3)).take(2),
                        contains(1, 2));
                assertThat(Vector.wrap(asList(1, 2, 3)).take(1),
                        contains(1));
                assertThat(Vector.wrap(asList(1, 2, 3)).take(0),
                        emptyIterable());
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                List<String> originalUnderlying = asList("foo", "bar", "baz");
                Vector<String> original = Vector.wrap(originalUnderlying);
                Vector<String> sliced = original.take(2);
                assertThat(sliced, contains("foo", "bar"));
                originalUnderlying.set(0, "qwerty");
                assertThat(sliced, contains("qwerty", "bar"));
            }

            @Test
            void returnsOriginalVectorReferenceIfPossible() {
                Vector<String> original = Vector.wrap(asList("foo", "bar", "baz"));
                Vector<String> slice1 = original.take(100);
                Vector<String> slice2 = original.take(3);
                assertSame(original, slice1);
                assertSame(original, slice2);
            }

        }

    }

    @Nested
    @DisplayName("drop")
    class DropTests {

        @Nested
        @DisplayName("array")
        class ArrayDropTests {

            @Test
            void throwsOnNegativeCount() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(new Integer[]{1}).drop(-1));
            }

            @Test
            void countZeroReturnsSameReference() {
                Vector<Integer> source = Vector.wrap(new Integer[]{1, 2, 3});
                Vector<Integer> sliced = source.drop(0);
                assertSame(source, sliced);
            }

            @Test
            void countEqualToSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).drop(3));
            }

            @Test
            void countExceedingSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).drop(4));
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).drop(1_000_000));
            }

            @Test
            void oneElement() {
                Vector<Integer> source = Vector.wrap(new Integer[]{1, 2, 3});
                assertThat(source.drop(1), contains(2, 3));
            }

            @Test
            void twoElements() {
                Vector<Integer> source = Vector.wrap(new Integer[]{1, 2, 3});
                assertThat(source.drop(2), contains(3));
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                String[] underlying = {"foo", "bar", "baz"};
                Vector<String> source = Vector.wrap(underlying);
                Vector<String> drop1 = source.drop(1);
                assertThat(drop1, contains("bar", "baz"));
                underlying[1] = "qwerty";
                assertThat(drop1, contains("qwerty", "baz"));
            }

        }

        @Nested
        @DisplayName("List")
        class ListDropTests {

            @Test
            void throwsOnNegativeCount() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(singletonList(1)).drop(-1));
            }

            @Test
            void countZeroReturnsSameReference() {
                Vector<Integer> source = Vector.wrap(asList(1, 2, 3));
                Vector<Integer> sliced = source.drop(0);
                assertSame(source, sliced);
            }

            @Test
            void countEqualToSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).drop(3));
            }

            @Test
            void countExceedingSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).drop(4));
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).drop(1_000_000));
            }

            @Test
            void oneElement() {
                Vector<Integer> source = Vector.wrap(asList(1, 2, 3));
                assertThat(source.drop(1), contains(2, 3));
            }

            @Test
            void twoElements() {
                Vector<Integer> source = Vector.wrap(asList(1, 2, 3));
                assertThat(source.drop(2), contains(3));
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                List<String> underlying = asList("foo", "bar", "baz");
                Vector<String> source = Vector.wrap(underlying);
                Vector<String> drop1 = source.drop(1);
                assertThat(drop1, contains("bar", "baz"));
                underlying.set(1, "qwerty");
                assertThat(drop1, contains("qwerty", "baz"));
            }
        }

    }

    @Nested
    @DisplayName("slice")
    class SliceTests {

        @Nested
        @DisplayName("array")
        class ArraySliceTests {

            @Test
            void throwsOnNegativeStartIndex() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(new Integer[]{1, 2, 3}).slice(-1, 1));
            }

            @Test
            void throwsOnNegativeEndIndex() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(new Integer[]{1, 2, 3}).slice(0, -1));
            }

            @Test
            void returnsEmptyVectorIfWidthIsZero() {
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).slice(0, 0));
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).slice(1_000_000, 1_000_000));
            }

            @Test
            void returnsEmptyVectorIfWidthLessThanZero() {
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).slice(10, 9));
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).slice(1_000_000, 0));
            }

            @Test
            void takesAsMuchAsItCan() {
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).slice(1, 1_000_000),
                        contains(2, 3));
            }

            @Test
            void onlyTakesWhatWasAskedFor() {
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).slice(0, 3),
                        contains(1, 2, 3));
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).slice(1, 3),
                        contains(2, 3));
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).slice(1, 2),
                        contains(2));
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).slice(0, 0),
                        emptyIterable());
            }

            @Test
            void startIndexEqualToSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).slice(3, 6));
            }

            @Test
            void startIndexExceedingSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).slice(4, 3));
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).slice(1_000_000, 3));
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                String[] underlying = new String[]{"foo", "bar", "baz"};
                Vector<String> original = Vector.wrap(underlying);
                Vector<String> slice2 = original.slice(1, 3);
                Vector<String> slice3 = original.slice(2, 100);
                underlying[0] = "qwerty";
                underlying[2] = "quux";
                assertThat(original, contains("qwerty", "bar", "quux"));
                assertThat(slice2, contains("bar", "quux"));
                assertThat(slice3, contains("quux"));
            }

        }

        @Nested
        @DisplayName("List")
        class ListSliceTests {

            @Test
            void throwsOnNegativeStartIndex() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(asList(1, 2, 3)).slice(-1, 1));
            }

            @Test
            void throwsOnNegativeEndIndex() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(asList(1, 2, 3)).slice(0, -1));
            }

            @Test
            void returnsEmptyVectorIfWidthIsZero() {
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).slice(0, 0));
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).slice(1_000_000, 1_000_000));
            }

            @Test
            void returnsEmptyVectorIfWidthLessThanZero() {
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).slice(10, 9));
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).slice(1_000_000, 0));
            }

            @Test
            void takesAsMuchAsItCan() {
                assertThat(Vector.wrap(asList(1, 2, 3)).slice(1, 1_000_000),
                        contains(2, 3));
            }

            @Test
            void onlyTakesWhatWasAskedFor() {
                assertThat(Vector.wrap(asList(1, 2, 3)).slice(0, 3),
                        contains(1, 2, 3));
                assertThat(Vector.wrap(asList(1, 2, 3)).slice(1, 3),
                        contains(2, 3));
                assertThat(Vector.wrap(asList(1, 2, 3)).slice(1, 2),
                        contains(2));
                assertThat(Vector.wrap(asList(1, 2, 3)).slice(0, 0),
                        emptyIterable());
            }

            @Test
            void startIndexEqualToSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).slice(3, 6));
            }

            @Test
            void startIndexExceedingSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).slice(4, 3));
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).slice(1_000_000, 3));
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                List<String> underlying = asList("foo", "bar", "baz");
                Vector<String> original = Vector.wrap(underlying);
                Vector<String> slice2 = original.slice(1, 3);
                Vector<String> slice3 = original.slice(2, 100);
                underlying.set(0, "qwerty");
                underlying.set(2, "quux");
                assertThat(original, contains("qwerty", "bar", "quux"));
                assertThat(slice2, contains("bar", "quux"));
                assertThat(slice3, contains("quux"));
            }

        }

    }
}

//    @Nested
//    @DisplayName("sliceFromIterable")
//    class SliceTests {
//        @Test
//        void throwsOnNegativeStartIndex() {
//            assertThrows(IllegalArgumentException.class, () -> Vector.sliceFromIterable(-1, 1, Vector.empty()));
//        }
//
//        @Test
//        void throwsOnNegativeEndIndex() {
//            assertThrows(IllegalArgumentException.class, () -> Vector.sliceFromIterable(0, -1, Vector.empty()));
//        }
//
//        @Test
//        void throwsOnNullSource() {
//            assertThrows(NullPointerException.class, () -> Vector.sliceFromIterable(0, 0, null));
//        }
//
//        @Test
//        void returnsEmptyVectorIfWidthIsZero() {
//            Iterable<String> infinite = Repeat.repeat("foo");
//            assertEquals(Vector.empty(), Vector.sliceFromIterable(0, 0, infinite));
//            assertEquals(Vector.empty(), Vector.sliceFromIterable(1_000_000, 1_000_000, infinite));
//        }
//
//        @Test
//        void returnsEmptyVectorIfWidthLessThanZero() {
//            Iterable<String> infinite = Repeat.repeat("foo");
//            assertEquals(Vector.empty(), Vector.sliceFromIterable(10, 9, infinite));
//            assertEquals(Vector.empty(), Vector.sliceFromIterable(1_000_000, 0, infinite));
//        }
//
//        @Test
//        void canHandleInfiniteSource() {
//            Vector<String> subject = Vector.sliceFromIterable(100, 102, Repeat.repeat("foo"));
//            assertThat(subject, contains("foo", "foo"));
//        }
//
//        @Nested
//        @DisplayName("sliceFromIterable List")
//        class SliceListTests {
//            @Test
//            void takesAsMuchAsItCan() {
//                assertThat(Vector.sliceFromIterable(1, 1_000_000, asList(1, 2, 3)),
//                        contains(2, 3));
//            }
//
//            @Test
//            void onlyTakesWhatWasAskedFor() {
//                assertThat(Vector.sliceFromIterable(0, 3, asList(1, 2, 3)),
//                        contains(1, 2, 3));
//                assertThat(Vector.sliceFromIterable(1, 3, asList(1, 2, 3)),
//                        contains(2, 3));
//                assertThat(Vector.sliceFromIterable(1, 2, asList(1, 2, 3)),
//                        contains(2));
//                assertThat(Vector.sliceFromIterable(0, 0, asList(1, 2, 3)),
//                        emptyIterable());
//            }
//
//            @Test
//            void startIndexEqualToSizeReturnsEmptyVector() {
//                assertEquals(Vector.empty(), Vector.sliceFromIterable(3, 6, asList(1, 2, 3)));
//            }
//
//            @Test
//            void startIndexExceedingSizeReturnsEmptyVector() {
//                assertEquals(Vector.empty(), Vector.sliceFromIterable(4, 3, asList(1, 2, 3)));
//                assertEquals(Vector.empty(), Vector.sliceFromIterable(1_000_000, 3, asList(1, 2, 3)));
//            }
//
//            @Test
//            void willNotMakeCopiesOfUnderlying() {
//                List<String> originalList = asList("foo", "bar", "baz");
//                Vector<String> slice1 = Vector.sliceFromIterable(0, 100, originalList);
//                Vector<String> slice2 = Vector.sliceFromIterable(1, 3, originalList);
//                Vector<String> slice3 = Vector.sliceFromIterable(2, 100, originalList);
//                originalList.set(0, "qwerty");
//                originalList.set(2, "quux");
//                assertThat(slice1, contains("qwerty", "bar", "quux"));
//                assertThat(slice2, contains("bar", "quux"));
//                assertThat(slice3, contains("quux"));
//            }
//
//        }
//
//        @Nested
//        @DisplayName("sliceFromIterable Vector")
//        class SliceVectorTests {
//            @Test
//            void takesAsMuchAsItCan() {
//                assertThat(Vector.sliceFromIterable(1, 1_000_000, Vector.of(1, 2, 3)),
//                        contains(2, 3));
//            }
//
//            @Test
//            void onlyTakesWhatWasAskedFor() {
//                assertThat(Vector.sliceFromIterable(0, 3, Vector.of(1, 2, 3)),
//                        contains(1, 2, 3));
//                assertThat(Vector.sliceFromIterable(1, 3, Vector.of(1, 2, 3)),
//                        contains(2, 3));
//                assertThat(Vector.sliceFromIterable(1, 2, Vector.of(1, 2, 3)),
//                        contains(2));
//                assertThat(Vector.sliceFromIterable(0, 0, Vector.of(1, 2, 3)),
//                        emptyIterable());
//            }
//
//            @Test
//            void willNotEvaluateIterableUnlessNecessary() {
//                Iterable<String> iterable = () -> {
//                    throw new AssertionError("Iterable was evaluated");
//                };
//                Vector.sliceFromIterable(0, 0, iterable);
//                Vector.sliceFromIterable(1, 0, iterable);
//                Vector.sliceFromIterable(1, 1, iterable);
//            }
//
//            @Test
//            void startIndexEqualToSizeReturnsEmptyVector() {
//                assertEquals(Vector.empty(), Vector.sliceFromIterable(3, 6, Vector.of(1, 2, 3)));
//            }
//
//            @Test
//            void startIndexExceedingSizeReturnsEmptyVector() {
//                assertEquals(Vector.empty(), Vector.sliceFromIterable(4, 3, Vector.of(1, 2, 3)));
//                assertEquals(Vector.empty(), Vector.sliceFromIterable(1_000_000, 3, Vector.of(1, 2, 3)));
//            }
//
//            @Test
//            void willNotMakeCopiesOfUnderlyingForVectors() {
//                List<String> originalUnderlying = asList("foo", "bar", "baz");
//                Vector<String> original = Vector.wrap(originalUnderlying);
//                Vector<String> sliced = Vector.sliceFromIterable(1, 10000, original);
//                assertThat(sliced, contains("bar", "baz"));
//                originalUnderlying.set(2, "qwerty");
//                assertThat(sliced, contains("bar", "qwerty"));
//            }
//        }
//    }

