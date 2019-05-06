package dev.marksman.collectionviews;

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
import static com.jnape.palatable.lambda.functions.builtin.fn1.Repeat.repeat;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Cons.cons;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class ImmutableSetTest {

    @Nested
    @DisplayName("empty")
    class EmptySetTests {

        @Test
        void alwaysYieldsSameReference() {
            Set<Integer> v1 = Set.empty();
            Set<String> v2 = Set.empty();
            assertSame(v1, v2);
        }

        @Test
        void isEmpty() {
            assertTrue(Set.empty().isEmpty());
        }

        @Test
        void sizeIsZero() {
            assertEquals(0, Set.empty().size());
        }

        @Test
        void iteratesCorrectly() {
            assertThat(Set.empty(), emptyIterable());
        }

        @Test
        void equalToItself() {
            assertEquals(Set.empty(), Set.empty());
        }
    }

    @Nested
    @DisplayName("copyFrom")
    class CopyFromTests {

        @Nested
        @DisplayName("copyFrom array")
        class CopyFromArrayTests {

            @Test
            void throwsOnNullArgument() {
                Integer[] arr = null;
                assertThrows(NullPointerException.class, () -> Set.copyFrom(arr));
            }

            @Test
            void makesCopy() {
                Integer[] arr = new Integer[]{1, 2, 3};
                ImmutableSet<Integer> subject = Set.copyFrom(arr);
                assertThat(subject, containsInAnyOrder(1, 2, 3));
                arr[0] = 4;
                assertThat(subject, containsInAnyOrder(1, 2, 3));
            }

            @Test
            void iteratorNextReturnsCorrectElements() {
                ImmutableSet<String> subject = Set.copyFrom(new String[]{"foo"});
                Iterator<String> iterator = subject.iterator();
                assertEquals("foo", iterator.next());
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            void iteratorHasNextCanBeCalledMultipleTimes() {
                ImmutableSet<String> subject = Set.copyFrom(new String[]{"foo"});
                Iterator<String> iterator = subject.iterator();
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertEquals("foo", iterator.next());
            }

            @Test
            void iteratorHasNextReturnsFalseIfNothingRemains() {
                ImmutableSet<String> subject = Set.copyFrom(new String[]{"foo"});
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertFalse(iterator.hasNext());
            }

            @Test
            void iteratorNextThrowsIfNothingRemains() {
                ImmutableSet<String> subject = Set.copyFrom(new String[]{"foo"});
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertThrows(NoSuchElementException.class, iterator::next);
            }

            @Test
            void iteratorThrowsIfRemoveIsCalled() {
                ImmutableSet<String> subject = Set.copyFrom(new String[]{"foo"});
                Iterator<String> iterator = subject.iterator();
                assertThrows(UnsupportedOperationException.class, iterator::remove);
            }

            @Nested
            @DisplayName("copyFrom size 3 array")
            class CopyFromArray3Tests {
                private ImmutableSet<String> subject;
                private String[] underlying;

                @BeforeEach
                void beforeEach() {
                    underlying = new String[]{"foo", "bar", "baz"};
                    subject = Set.copyFrom(underlying);
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
                    assertFalse(subject.contains("qwerty"));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, containsInAnyOrder("foo", "bar", "baz"));
                }

                @Test
                void toNonEmptySucceeds() {
                    assertEquals(just(Set.of("foo", "bar", "baz")),
                            subject.toNonEmpty());
                }

                @Test
                void toNonEmptyOrThrowSucceeds() {
                    assertEquals(Set.of("foo", "bar", "baz"),
                            subject.toNonEmptyOrThrow());
                }

                @Test
                void toImmutableReturnsItself() {
                    assertSame(subject, subject.toImmutable());
                }

                @Test
                void notAffectedByMutation() {
                    underlying[0] = "qwerty";
                    assertThat(subject, containsInAnyOrder("foo", "bar", "baz"));
                }

            }

            @Nested
            @DisplayName("copyFrom size 1 array")
            class CopyFromSingletonArrayTests {
                private ImmutableSet<String> subject;

                @BeforeEach
                void setUp() {
                    subject = Set.copyFrom(new String[]{"foo"});
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

            }

            @Nested
            @DisplayName("copyFrom empty array")
            class WrapEmptyArrayTests {
                private ImmutableSet<Integer> subject;

                @BeforeEach
                void setUp() {
                    subject = Set.copyFrom(new Integer[]{});
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
        class CopyFromListTests {

            @Test
            void throwsOnNullArgument() {
                List<Integer> list = null;
                assertThrows(NullPointerException.class, () -> Set.copyFrom(list));
            }

            @Test
            void makesCopy() {
                List<Integer> list = asList(1, 2, 3);
                Set<Integer> subject = Set.copyFrom(list);
                assertThat(subject, contains(1, 2, 3));
                list.set(0, 4);
                assertThat(subject, contains(1, 2, 3));
            }

            @Test
            void iteratorNextReturnsCorrectElements() {
                Set<String> subject = Set.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                assertEquals("foo", iterator.next());
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            void iteratorHasNextCanBeCalledMultipleTimes() {
                Set<String> subject = Set.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertEquals("foo", iterator.next());
            }

            @Test
            void iteratorHasNextReturnsFalseIfNothingRemains() {
                Set<String> subject = Set.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertFalse(iterator.hasNext());
            }

            @Test
            void iteratorNextThrowsIfNothingRemains() {
                Set<String> subject = Set.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertThrows(NoSuchElementException.class, iterator::next);
            }

            @Test
            void iteratorThrowsIfRemoveIsCalled() {
                Set<String> subject = Set.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                assertThrows(UnsupportedOperationException.class, iterator::remove);
            }

            @Nested
            @DisplayName("copyFrom size 3 List")
            class CopyFromList3Tests {
                private Set<String> subject;
                private List<String> underlying;

                @BeforeEach
                void setUp() {
                    underlying = asList("foo", "bar", "baz");
                    subject = Set.copyFrom(underlying);
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
                    assertFalse(subject.contains("qwerty"));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, containsInAnyOrder("foo", "bar", "baz"));
                }

                @Test
                void toNonEmptySucceeds() {
                    assertEquals(just(Set.of("foo", "bar", "baz")),
                            subject.toNonEmpty());
                }

                @Test
                void toNonEmptyOrThrowSucceeds() {
                    assertEquals(Set.of("foo", "bar", "baz"),
                            subject.toNonEmptyOrThrow());
                }

                @Test
                void toImmutableReturnsItself() {
                    assertSame(subject, subject.toImmutable());
                }

                @Test
                void notAffectedByMutation() {
                    underlying.set(0, "qwerty");
                    assertThat(subject, containsInAnyOrder("foo", "bar", "baz"));
                }

            }

            @Nested
            @DisplayName("copyFrom size 1 List")
            class CopyFromSize1ListTests {

                private ImmutableSet<String> subject;

                @BeforeEach
                void setUp() {
                    subject = Set.copyFrom(singletonList("foo"));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

            }

            @Nested
            @DisplayName("wrap empty List")
            class CopyFromEmptyListTests {
                private ImmutableSet<Integer> subject;

                @BeforeEach
                void setUp() {
                    subject = Set.copyFrom(emptyList());
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
        class CopyFromIterableTests {

            @Test
            void throwsOnNullArgument() {
                Iterable<String> source = null;
                assertThrows(NullPointerException.class, () -> Set.copyFrom(source));
            }

            @Test
            void iteratesCorrectly() {
                Iterable<Integer> source = cons(1, cons(2, cons(3, emptyList())));
                Set<Integer> subject = Set.copyFrom(source);
                assertThat(subject, containsInAnyOrder(1, 2, 3));
            }

            @Test
            void iteratorNextReturnsCorrectElements() {
                Set<String> subject = Set.copyFrom(asList("foo"));
                Iterator<String> iterator = subject.iterator();
                assertEquals("foo", iterator.next());
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            void iteratorHasNextCanBeCalledMultipleTimes() {
                Set<String> subject = Set.copyFrom(asList("foo"));
                Iterator<String> iterator = subject.iterator();
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertTrue(iterator.hasNext());
                assertEquals("foo", iterator.next());
            }

            @Test
            void iteratorHasNextReturnsFalseIfNothingRemains() {
                Set<String> subject = Set.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertFalse(iterator.hasNext());
            }

            @Test
            void iteratorNextThrowsIfNothingRemains() {
                Set<String> subject = Set.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                iterator.next();
                assertThrows(NoSuchElementException.class, iterator::next);
            }

            @Test
            void iteratorThrowsIfRemoveIsCalled() {
                Set<String> subject = Set.copyFrom(singletonList("foo"));
                Iterator<String> iterator = subject.iterator();
                assertThrows(UnsupportedOperationException.class, iterator::remove);
            }

            @Nested
            @DisplayName("copyFrom size 3 Iterable")
            class CopyFromSize3IterableTests {
                private ImmutableSet<String> subject;
                private Iterable<String> underlying;

                @BeforeEach
                void setUp() {
                    underlying = cons("foo", cons("bar", cons("baz", emptyList())));
                    subject = Set.copyFrom(underlying);
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
                    assertFalse(subject.contains("qwerty"));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, containsInAnyOrder("foo", "bar", "baz"));
                }

                @Test
                void toNonEmptySucceeds() {
                    assertEquals(just(Set.of("foo", "bar", "baz")),
                            subject.toNonEmpty());
                }

                @Test
                void toNonEmptyOrThrowSucceeds() {
                    assertEquals(Set.of("foo", "bar", "baz"),
                            subject.toNonEmptyOrThrow());
                }

            }

            @Nested
            @DisplayName("copyFrom size 1 Iterable")
            class CopyFromSize1IterableTests {

                private ImmutableSet<String> subject;

                @BeforeEach
                void setUp() {
                    subject = Set.copyFrom(cons("foo", emptyList()));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

            }

            @Nested
            @DisplayName("copyFrom empty Iterable")
            class CopyFromEmptyIterableTests {
                private Set<Integer> subject;

                @BeforeEach
                void setUp() {
                    subject = Set.copyFrom(Collections::emptyIterator);
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
        @DisplayName("with maxCount")
        class CopyFromMaxCountTests {

            private Iterable<Integer> source;

            @BeforeEach
            void setUp() {
                source = cons(1, cons(2, cons(3, emptyList())));
            }

            @Test
            void takesAsMuchAsItCan() {
                assertThat(Set.copyFrom(1_000_000, source),
                        containsInAnyOrder(1, 2, 3));
            }

            @Test
            void onlyTakesWhatWasAskedFor() {
                assertThat(Set.copyFrom(3, source),
                        containsInAnyOrder(1, 2, 3));
                assertThat(Set.copyFrom(2, source),
                        containsInAnyOrder(1, 2));
                assertThat(Set.copyFrom(1, source),
                        contains(1));
                assertThat(Set.copyFrom(0, source),
                        emptyIterable());
            }

            @Test
            void willNotEvaluateIterableUnlessNecessary() {
                Iterable<String> poison = () -> {
                    throw new AssertionError("Iterable was evaluated");
                };
                assertThat(Set.copyFrom(0, poison), emptyIterable());
                assertThat(Set.copyFrom(1, cons("foo", poison)), contains("foo"));
            }

            @Test
            void safeToUseOnInfiniteIterables() {
                assertThat(Set.copyFrom(3, repeat("foo")),
                        contains("foo"));
            }

        }

    }

}
