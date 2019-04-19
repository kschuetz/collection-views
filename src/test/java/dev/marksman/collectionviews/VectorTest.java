package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.builtin.fn1.Repeat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static dev.marksman.collectionviews.EmptyVector.emptyVector;
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
        @Test
        void alwaysYieldsSameReference() {
            Vector<Integer> v1 = emptyVector();
            Vector<String> v2 = emptyVector();
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

                @BeforeEach
                void beforeEach() {
                    subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
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

                @BeforeEach
                void setUp() {
                    subject = Vector.wrap(asList("foo", "bar", "baz"));
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
            }
        }
    }

    @Nested
    @DisplayName("take")
    class TakeTests {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> Vector.take(-1, singletonList(1)));
        }

        @Test
        void throwsOnNullSource() {
            assertThrows(NullPointerException.class, () -> Vector.take(0, null));
        }

        @Test
        void takesAsMuchAsItCan() {
            assertThat(Vector.take(1_000_000, asList(1, 2, 3)),
                    contains(1, 2, 3));
        }

        @Test
        void onlyTakesWhatWasAskedFor() {
            assertThat(Vector.take(3, asList(1, 2, 3)),
                    contains(1, 2, 3));
            assertThat(Vector.take(2, asList(1, 2, 3)),
                    contains(1, 2));
            assertThat(Vector.take(1, asList(1, 2, 3)),
                    contains(1));
            assertThat(Vector.take(0, asList(1, 2, 3)),
                    emptyIterable());
        }

        @Test
        void willNotEvaluateIterableUnlessNecessary() {
            Iterable<String> iterable = () -> {
                throw new AssertionError("Iterable was evaluated");
            };
            Vector.take(0, iterable);
        }

        @Test
        void willNotMakeCopiesOfUnderlyingForVectors() {
            List<String> originalUnderlying = asList("foo", "bar", "baz");
            Vector<String> original = Vector.wrap(originalUnderlying);
            Vector<String> sliced = Vector.take(2, original);
            assertThat(sliced, contains("foo", "bar"));
            originalUnderlying.set(0, "qwerty");
            assertThat(sliced, contains("qwerty", "bar"));
        }

        @Test
        void willNotMakeCopiesOfUnderlyingForLists() {
            List<String> originalList = asList("foo", "bar", "baz");
            Vector<String> slice1 = Vector.take(100, originalList);
            Vector<String> slice2 = Vector.take(3, originalList);
            Vector<String> slice3 = Vector.take(1, originalList);
            originalList.set(0, "qwerty");
            originalList.set(2, "quux");
            assertThat(slice1, contains("qwerty", "bar", "quux"));
            assertThat(slice2, contains("qwerty", "bar", "quux"));
            assertThat(slice3, contains("qwerty"));
        }

        @Test
        void returnsOriginalVectorReferenceIfPossible() {
            Vector<String> original = Vector.wrap(asList("foo", "bar", "baz"));
            Vector<String> slice1 = Vector.take(100, original);
            Vector<String> slice2 = Vector.take(3, slice1);
            assertSame(original, slice1);
            assertSame(original, slice2);
        }
    }

    @Nested
    @DisplayName("drop")
    class DropTests {

        @Test
        void throwsOnNegativeCount() {
            assertThrows(IllegalArgumentException.class, () -> Vector.drop(-1, Vector.of(1)));
        }

        @Test
        void throwsOnNullSource() {
            assertThrows(NullPointerException.class, () -> Vector.drop(0, null));
        }

        @Test
        void countZeroReturnsSameReference() {
            Vector<Integer> source = Vector.of(1, 2, 3);
            Vector<Integer> sliced = Vector.drop(0, source);
            assertSame(source, sliced);
        }

        @Test
        void countEqualToSizeReturnsEmptyVector() {
            assertEquals(emptyVector(), Vector.drop(3, Vector.of(1, 2, 3)));
        }

        @Test
        void countExceedingSizeReturnsEmptyVector() {
            assertEquals(emptyVector(), Vector.drop(4, Vector.of(1, 2, 3)));
            assertEquals(emptyVector(), Vector.drop(1_000_000, Vector.of(1, 2, 3)));
        }

        @Test
        void oneElement() {
            Vector<Integer> source = Vector.of(1, 2, 3);
            assertThat(Vector.drop(1, source), contains(2, 3));
        }

        @Test
        void twoElements() {
            Vector<Integer> source = Vector.of(1, 2, 3);
            assertThat(Vector.drop(2, source), contains(3));
        }

        @Test
        void willNotMakeCopiesOfUnderlyingArrays() {
            String[] underlying = {"foo", "bar", "baz"};
            Vector<String> source = Vector.wrap(underlying);
            Vector<String> drop1 = Vector.drop(1, source);
            assertThat(drop1, contains("bar", "baz"));
            underlying[1] = "qwerty";
            assertThat(drop1, contains("qwerty", "baz"));
        }

        @Test
        void willNotMakeCopiesOfUnderlyingLists() {
            List<String> underlying = asList("foo", "bar", "baz");
            Vector<String> source = Vector.wrap(underlying);
            Vector<String> drop1 = Vector.drop(1, source);
            assertThat(drop1, contains("bar", "baz"));
            underlying.set(1, "qwerty");
            assertThat(drop1, contains("qwerty", "baz"));
        }
    }

    @Nested
    @DisplayName("slice")
    class SliceTests {
        @Test
        void throwsOnNegativeStartIndex() {
            assertThrows(IllegalArgumentException.class, () -> Vector.slice(-1, 1, Vector.empty()));
        }

        @Test
        void throwsOnNegativeEndIndex() {
            assertThrows(IllegalArgumentException.class, () -> Vector.slice(0, -1, Vector.empty()));
        }

        @Test
        void throwsOnNullSource() {
            assertThrows(NullPointerException.class, () -> Vector.slice(0, 0, null));
        }

        @Test
        void returnsEmptyVectorIfWidthIsZero() {
            Iterable<String> infinite = Repeat.repeat("foo");
            assertEquals(Vector.empty(), Vector.slice(0, 0, infinite));
            assertEquals(Vector.empty(), Vector.slice(1_000_000, 1_000_000, infinite));
        }

        @Test
        void returnsEmptyVectorIfWidthLessThanZero() {
            Iterable<String> infinite = Repeat.repeat("foo");
            assertEquals(Vector.empty(), Vector.slice(10, 9, infinite));
            assertEquals(Vector.empty(), Vector.slice(1_000_000, 0, infinite));
        }

        @Test
        void canHandleInfiniteSource() {
            Vector<String> subject = Vector.slice(100, 102, Repeat.repeat("foo"));
            assertThat(subject, contains("foo", "foo"));
        }

        @Nested
        @DisplayName("slice List")
        class SliceListTests {
            @Test
            void takesAsMuchAsItCan() {
                assertThat(Vector.slice(1, 1_000_000, asList(1, 2, 3)),
                        contains(2, 3));
            }

            @Test
            void onlyTakesWhatWasAskedFor() {
                assertThat(Vector.slice(0, 3, asList(1, 2, 3)),
                        contains(1, 2, 3));
                assertThat(Vector.slice(1, 3, asList(1, 2, 3)),
                        contains(2, 3));
                assertThat(Vector.slice(1, 2, asList(1, 2, 3)),
                        contains(2));
                assertThat(Vector.slice(0, 0, asList(1, 2, 3)),
                        emptyIterable());
            }

            @Test
            void startIndexEqualToSizeReturnsEmptyVector() {
                assertEquals(emptyVector(), Vector.slice(3, 6, asList(1, 2, 3)));
            }

            @Test
            void startIndexExceedingSizeReturnsEmptyVector() {
                assertEquals(emptyVector(), Vector.slice(4, 3, asList(1, 2, 3)));
                assertEquals(emptyVector(), Vector.slice(1_000_000, 3, asList(1, 2, 3)));
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                List<String> originalList = asList("foo", "bar", "baz");
                Vector<String> slice1 = Vector.slice(0, 100, originalList);
                Vector<String> slice2 = Vector.slice(1, 3, originalList);
                Vector<String> slice3 = Vector.slice(2, 100, originalList);
                originalList.set(0, "qwerty");
                originalList.set(2, "quux");
                assertThat(slice1, contains("qwerty", "bar", "quux"));
                assertThat(slice2, contains("bar", "quux"));
                assertThat(slice3, contains("quux"));
            }

        }

        @Nested
        @DisplayName("slice Vector")
        class SliceVectorTests {
            @Test
            void takesAsMuchAsItCan() {
                assertThat(Vector.slice(1, 1_000_000, Vector.of(1, 2, 3)),
                        contains(2, 3));
            }

            @Test
            void onlyTakesWhatWasAskedFor() {
                assertThat(Vector.slice(0, 3, Vector.of(1, 2, 3)),
                        contains(1, 2, 3));
                assertThat(Vector.slice(1, 3, Vector.of(1, 2, 3)),
                        contains(2, 3));
                assertThat(Vector.slice(1, 2, Vector.of(1, 2, 3)),
                        contains(2));
                assertThat(Vector.slice(0, 0, Vector.of(1, 2, 3)),
                        emptyIterable());
            }

            @Test
            void willNotEvaluateIterableUnlessNecessary() {
                Iterable<String> iterable = () -> {
                    throw new AssertionError("Iterable was evaluated");
                };
                Vector.slice(0, 0, iterable);
                Vector.slice(1, 0, iterable);
                Vector.slice(1, 1, iterable);
            }

            @Test
            void startIndexEqualToSizeReturnsEmptyVector() {
                assertEquals(emptyVector(), Vector.slice(3, 6, Vector.of(1, 2, 3)));
            }

            @Test
            void startIndexExceedingSizeReturnsEmptyVector() {
                assertEquals(emptyVector(), Vector.slice(4, 3, Vector.of(1, 2, 3)));
                assertEquals(emptyVector(), Vector.slice(1_000_000, 3, Vector.of(1, 2, 3)));
            }

            @Test
            void willNotMakeCopiesOfUnderlyingForVectors() {
                List<String> originalUnderlying = asList("foo", "bar", "baz");
                Vector<String> original = Vector.wrap(originalUnderlying);
                Vector<String> sliced = Vector.slice(1, 10000, original);
                assertThat(sliced, contains("bar", "baz"));
                originalUnderlying.set(2, "qwerty");
                assertThat(sliced, contains("bar", "qwerty"));
            }
        }
    }

}