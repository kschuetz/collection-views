package dev.marksman.collectionviews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

class SetTest {

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
    }

    @Nested
    @DisplayName("wrap")
    class WrapTests {

        @Test
        void iteratorNextReturnsCorrectElements() {
            Set<String> subject = Set.wrap(singleton("foo"));
            Iterator<String> iterator = subject.iterator();
            assertEquals("foo", iterator.next());
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        void iteratorHasNextCanBeCalledMultipleTimes() {
            Set<String> subject = Set.wrap(singleton("foo"));
            Iterator<String> iterator = subject.iterator();
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertEquals("foo", iterator.next());
        }

        @Test
        void iteratorHasNextReturnsFalseIfNothingRemains() {
            Set<String> subject = Set.wrap(singleton("foo"));
            Iterator<String> iterator = subject.iterator();
            iterator.next();
            assertFalse(iterator.hasNext());
        }

        @Test
        void iteratorNextThrowsIfNothingRemains() {
            Set<String> subject = Set.wrap(singleton("foo"));
            Iterator<String> iterator = subject.iterator();
            iterator.next();
            assertThrows(NoSuchElementException.class, iterator::next);
        }

        @Test
        void iteratorThrowsIfRemoveIsCalled() {
            Set<String> subject = Set.wrap(singleton("foo"));
            Iterator<String> iterator = subject.iterator();
            assertThrows(UnsupportedOperationException.class, iterator::remove);
        }

        @Nested
        @DisplayName("wrap size 3 java.util.Set")
        class WrapSize3Tests {
            private java.util.Set<String> underlying;
            private Set<String> subject;

            @BeforeEach
            void beforeEach() {
                underlying = new java.util.HashSet<String>();
                underlying.addAll(asList("foo", "bar", "baz"));
                subject = Set.wrap(underlying);
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

            @Test
            void willNotMakeCopyOfUnderlying() {
                underlying.add("don't do this!");
                assertTrue(subject.contains("don't do this!"));
                underlying.remove("foo");
                assertFalse(subject.contains("foo"));
            }

        }

        @Nested
        @DisplayName("wrap size 3 java.util.Set")
        class WrapSize1Tests {
            private java.util.Set<String> underlying;
            private Set<String> subject;

            @BeforeEach
            void beforeEach() {
                underlying = singleton("foo");
                subject = Set.wrap(underlying);
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
                assertFalse(subject.contains("baz"));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, containsInAnyOrder("foo"));
            }

        }

        @Nested
        @DisplayName("wrap size 3 java.util.Set")
        class WrapEmptySetTests {
            private Set<String> subject;

            @BeforeEach
            void beforeEach() {
                subject = Set.wrap(emptySet());
            }

            @Test
            void yieldsSameReferenceAsEmptySet() {
                assertSame(subject, Set.empty());
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
            void containsNegative() {
                assertFalse(subject.contains("foo"));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, emptyIterable());
            }

        }

    }
}
