package dev.marksman.collectionviews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

class SetTest {

    @Nested
    @DisplayName("wrap")
    class Wrap {

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
        class WrapSize3JavaUtilSet {
            private java.util.Set<String> underlying;
            private Set<String> subject;

            @BeforeEach
            void beforeEach() {
                underlying = new java.util.HashSet<>();
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

            @Test
            void equalToSelf() {
                assertEquals(subject, subject);
            }

            @Test
            void equalToOtherSetWrappingEquivalentUnderlying() {
                HashSet<String> otherUnderlying = toCollection(HashSet::new, asList("baz", "bar", "foo"));
                Set<String> other = Set.wrap(otherUnderlying);
                assertEquals(subject, other);
                assertEquals(other, subject);
            }

            @Test
            void equalToSameSetConstructedImmutably() {
                assertEquals(subject, Set.of("foo", "bar", "baz"));
                assertEquals(Set.of("baz", "bar", "foo"), subject);
            }

            @Test
            void notEqualToNull() {
                assertNotEquals(subject, null);
                assertNotEquals(null, subject);
            }

            @Test
            void notEqualToStrictSubset() {
                Set<String> subset = Set.of("foo", "bar");
                assertNotEquals(subject, subset);
                assertNotEquals(subset, subject);
            }

            @Test
            void notEqualToStrictSuperset() {
                Set<String> superset = Set.of("foo", "bar", "baz", "quux");
                assertNotEquals(subject, superset);
                assertNotEquals(superset, subject);
            }

        }

        @Nested
        @DisplayName("wrap size 1 java.util.Set")
        class WrapSize1JavaUtilSet {
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

            @Test
            void equalToSelf() {
                assertEquals(subject, subject);
            }

            @Test
            void equalToOtherSetWrappingEquivalentUnderlying() {
                java.util.Set<String> otherUnderlying = singleton("foo");
                Set<String> other = Set.wrap(otherUnderlying);
                assertEquals(subject, other);
                assertEquals(other, subject);
            }

            @Test
            void equalToSameSetConstructedImmutably() {
                assertEquals(subject, Set.of("foo"));
                assertEquals(Set.of("foo"), subject);
            }

            @Test
            void notEqualToNull() {
                assertNotEquals(subject, null);
                assertNotEquals(null, subject);
            }

            @Test
            void notEqualToEmpty() {
                assertNotEquals(subject, Set.empty());
                assertNotEquals(Set.empty(), subject);
            }

            @Test
            void notEqualToStrictSuperset() {
                Set<String> superset = Set.of("foo", "bar", "baz");
                assertNotEquals(subject, superset);
                assertNotEquals(superset, subject);
            }

        }

        @Nested
        @DisplayName("wrap empty java.util.Set")
        class WrapEmptySet {
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

            @Test
            void equalToSelf() {
                assertEquals(subject, subject);
            }

            @Test
            void equalToEmptySet() {
                assertEquals(subject, subject);
            }

            @Test
            void notEqualToNull() {
                assertNotEquals(subject, null);
                assertNotEquals(null, subject);
            }

            @Test
            void notEqualToStrictSuperset() {
                Set<String> superset = Set.of("foo");
                assertNotEquals(subject, superset);
                assertNotEquals(superset, subject);
            }

        }

    }

    @Nested
    @DisplayName("equality")
    class Equality {

        @Test
        void setsWithSameElementsEqual() {
            ImmutableNonEmptySet<Integer> set1 = Set.of(1, 2, 3);
            Set<Integer> set2 = Set.wrap(new java.util.HashSet<Integer>() {{
                addAll(asList(3, 2, 1));
            }});

            assertEquals(set1, set2);
            assertEquals(set2, set1);
        }

        @Test
        void differentTypesNotEqual() {
            Set<String> set1 = Set.of("foo", "bar", "baz");
            Set<Integer> set2 = Set.of(1, 2, 3);
            assertNotEquals(set1, set2);
        }

        @Test
        void nestedEquality() {
            Set<Integer> set1 = Set.of(1, 2, 3);
            Set<Integer> set2 = Set.of(4, 5, 6);
            ImmutableNonEmptySet<Set<Integer>> set3 = Set.of(set1, set2);
            ImmutableNonEmptySet<Set<Integer>> set4 = Set.of(set2, set1);
            assertEquals(set3, set4);
            assertEquals(set4, set3);
        }

    }

}
