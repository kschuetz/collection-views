package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.builtin.fn2.Eq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
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
    class EmptyVector {

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
            void reverseIsEmpty() {
                assertThat(Vector.wrap(new Integer[]{}).reverse(), emptyIterable());
            }

            @Test
            void zipWithIndexIsEmpty() {
                assertThat(Vector.wrap(new Integer[]{}).zipWithIndex(), emptyIterable());
            }

            @Test
            void findReturnsNothing() {
                assertEquals(nothing(), Vector.wrap(new Integer[]{}).find(constantly(true)));
            }

            @Test
            void findIndexReturnsNothing() {
                assertEquals(nothing(), Vector.wrap(new Integer[]{}).findIndex(constantly(true)));
            }

            @Test
            void equalToItself() {
                assertEquals(Vector.wrap(new Integer[]{}), Vector.wrap(new Integer[]{}));
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
            void reverseIsEmpty() {
                assertThat(Vector.wrap(emptyList()).reverse(), emptyIterable());
            }

            @Test
            void zipWithIndexIsEmpty() {
                assertThat(Vector.wrap(emptyList()).zipWithIndex(), emptyIterable());
            }

            @Test
            void findReturnsNothing() {
                assertEquals(nothing(), Vector.wrap(emptyList()).find(constantly(true)));
            }

            @Test
            void findIndexReturnsNothing() {
                assertEquals(nothing(), Vector.wrap(emptyList()).findIndex(constantly(true)));
            }

            @Test
            void equalToItself() {
                assertEquals(Vector.wrap(emptyList()), Vector.wrap(emptyList()));
            }

        }

    }

    @Nested
    @DisplayName("wrap")
    class Wrap {

        @Nested
        @DisplayName("wrap array")
        class WrapArray {

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
            class WrapSize3Array3 {
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
                void toImmutableIsUnaffectedByMutation() {
                    ImmutableVector<String> immutable = subject.toImmutable();
                    underlying[0] = "qwerty";
                    assertThat(subject, contains("qwerty", "bar", "baz"));
                    assertThat(immutable, contains("foo", "bar", "baz"));
                }

                @Test
                void equalToItself() {
                    assertEquals(subject, subject);
                }

                @Test
                void equalToOtherVectorWrappingEquivalentUnderlying() {
                    Vector<String> other = Vector.wrap(asList("foo", "bar", "baz"));
                    assertEquals(subject, other);
                    assertEquals(other, subject);
                }

                @Test
                void equalToSameVectorConstructedImmutably() {
                    assertEquals(subject, Vector.of("foo", "bar", "baz"));
                    assertEquals(Vector.of("foo", "bar", "baz"), subject);
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
                    Vector<String> subsequence = Vector.of("foo", "bar");
                    assertNotEquals(subject, subsequence);
                    assertNotEquals(subsequence, subject);
                }

                @Test
                void notEqualToSupersequence() {
                    Vector<String> supersequence = Vector.of("foo", "bar", "baz", "quux");
                    assertNotEquals(subject, supersequence);
                    assertNotEquals(supersequence, subject);
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
            @DisplayName("wrap size 1 array")
            class WrapSize1Array {
                private Vector<String> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.wrap(new String[]{"foo"});
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

            }

            @Nested
            @DisplayName("wrap empty array")
            class WrapEmptyArray {
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
                void equalToEmpty() {
                    assertEquals(subject, Vector.empty());
                }

            }
        }

        @Nested
        @DisplayName("wrap List")
        class WrapList {

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
            class WrapSize3List {
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
                void toImmutableIsUnaffectedByMutation() {
                    ImmutableVector<String> immutable = subject.toImmutable();
                    underlying.set(0, "qwerty");
                    assertThat(subject, contains("qwerty", "bar", "baz"));
                    assertThat(immutable, contains("foo", "bar", "baz"));
                }

                @Test
                void equalToItself() {
                    assertEquals(subject, subject);
                }

                @Test
                void equalToOtherVectorWrappingEquivalentUnderlying() {
                    Vector<String> other = Vector.wrap(asList("foo", "bar", "baz"));
                    assertEquals(subject, other);
                    assertEquals(other, subject);
                }

                @Test
                void equalToSameVectorConstructedImmutably() {
                    assertEquals(subject, Vector.of("foo", "bar", "baz"));
                    assertEquals(Vector.of("foo", "bar", "baz"), subject);
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
                    Vector<String> subsequence = Vector.of("foo", "bar");
                    assertNotEquals(subject, subsequence);
                    assertNotEquals(subsequence, subject);
                }

                @Test
                void notEqualToSupersequence() {
                    Vector<String> supersequence = Vector.of("foo", "bar", "baz", "quux");
                    assertNotEquals(subject, supersequence);
                    assertNotEquals(supersequence, subject);
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
            @DisplayName("wrap size 1 List")
            class WrapSize1List {

                private Vector<String> subject;

                @BeforeEach
                void setUp() {
                    subject = Vector.wrap(singletonList("foo"));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

            }

            @Nested
            @DisplayName("wrap empty List")
            class WrapEmptyList {
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
                void toNonEmptyFails() {
                    assertEquals(nothing(), subject.toNonEmpty());
                }

                @Test
                void toNonEmptyOrThrowThrows() {
                    assertThrows(IllegalArgumentException.class, () -> subject.toNonEmptyOrThrow());
                }

                @Test
                void equalToEmpty() {
                    assertEquals(subject, Vector.empty());
                }

            }

        }

    }

    @Nested
    @DisplayName("fmap")
    class Fmap {
        @Nested
        @DisplayName("array")
        class Array {

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

            @Test
            void equality() {
                Vector<Integer> source = Vector.wrap(new Integer[]{1, 2, 3});
                Vector<String> mapped = source.fmap(n -> n * 2).fmap(Object::toString);
                assertEquals(Vector.wrap(new String[]{"2", "4", "6"}), mapped);
            }

        }

        @Nested
        @DisplayName("List")
        class List {

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

            @Test
            void willNotMakeCopiesOfUnderlying() {
                java.util.List<Integer> underlying = asList(1, 2, 3);
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

            @Test
            void equality() {
                Vector<Integer> source = Vector.wrap(asList(1, 2, 3));
                Vector<String> mapped = source.fmap(n -> n * 2).fmap(Object::toString);
                assertEquals(Vector.wrap(asList("2", "4", "6")), mapped);
            }

        }

    }

    @Nested
    @DisplayName("take")
    class Take {

        @Nested
        @DisplayName("array")
        class Array {

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

            @Test
            void equality() {
                assertEquals(Vector.wrap(new Integer[]{1, 2, 3}),
                        Vector.wrap(new Integer[]{1, 2, 3, 4, 5, 6}).take(3));
            }

            @Test
            void findPositive() {
                assertEquals(just("bar"), Vector.wrap(new String[]{"foo", "bar", "baz"}).take(2)
                        .find(Eq.eq("bar")));
            }

            @Test
            void findNegative() {
                assertEquals(nothing(), Vector.wrap(new String[]{"foo", "bar", "baz"}).take(2)
                        .find(Eq.eq("baz")));
            }

            @Test
            void findIndexPositive() {
                assertEquals(just(1), Vector.wrap(new String[]{"foo", "bar", "baz"}).take(2)
                        .findIndex(Eq.eq("bar")));
            }

            @Test
            void findIndexNegative() {
                assertEquals(nothing(), Vector.wrap(new String[]{"foo", "bar", "baz"}).take(2)
                        .findIndex(Eq.eq("baz")));
            }

            @Test
            void stackSafe() {
                int BIG = 50_000;
                Integer[] underlying = new Integer[BIG];
                for (int i = 0; i < BIG; i++) {
                    underlying[i] = i;
                }
                Vector<Integer> sliced = Vector.wrap(underlying);
                for (int i = BIG; i > 0; i--) {
                    sliced = sliced.take(i);
                }
                assertEquals(just(0), sliced.get(0));
            }

        }

        @Nested
        @DisplayName("List")
        class List {
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
                java.util.List<String> originalUnderlying = asList("foo", "bar", "baz");
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

            @Test
            void equality() {
                assertEquals(Vector.wrap(asList(1, 2, 3)),
                        Vector.wrap(asList(1, 2, 3, 4, 5, 6)).take(3));
            }

            @Test
            void findPositive() {
                assertEquals(just("bar"), Vector.wrap(asList("foo", "bar", "baz")).take(2)
                        .find(Eq.eq("bar")));
            }

            @Test
            void findNegative() {
                assertEquals(nothing(), Vector.wrap(asList("foo", "bar", "baz")).take(2)
                        .find(Eq.eq("baz")));
            }

            @Test
            void findIndexPositive() {
                assertEquals(just(1), Vector.wrap(asList("foo", "bar", "baz")).take(2)
                        .findIndex(Eq.eq("bar")));
            }

            @Test
            void findIndexNegative() {
                assertEquals(nothing(), Vector.wrap(asList("foo", "bar", "baz")).take(2)
                        .findIndex(Eq.eq("baz")));
            }

            @Test
            void stackSafe() {
                int BIG = 50_000;
                ArrayList<Integer> underlying = new ArrayList<>(BIG);
                for (int i = 0; i < BIG; i++) {
                    underlying.add(i);
                }
                Vector<Integer> sliced = Vector.wrap(underlying);
                for (int i = BIG; i > 0; i--) {
                    sliced = sliced.take(i);
                }
                assertEquals(just(0), sliced.get(0));
            }

        }

    }

    @Nested
    @DisplayName("takeRight")
    class TakeRight {

        @Nested
        @DisplayName("array")
        class Array {

            @Test
            void throwsOnNegativeCount() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(new Integer[]{1}).takeRight(-1));
            }

            @Test
            void takesAsMuchAsItCan() {
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).takeRight(1_000_000),
                        contains(1, 2, 3));
            }

            @Test
            void onlyTakesWhatWasAskedFor() {
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).takeRight(3),
                        contains(1, 2, 3));
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).takeRight(2),
                        contains(2, 3));
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).takeRight(1),
                        contains(3));
                assertThat(Vector.wrap(new Integer[]{1, 2, 3}).takeRight(0),
                        emptyIterable());
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                String[] originalUnderlying = new String[]{"foo", "bar", "baz"};
                Vector<String> original = Vector.wrap(originalUnderlying);
                Vector<String> sliced = original.takeRight(2);
                assertThat(sliced, contains("bar", "baz"));
                originalUnderlying[1] = "qwerty";
                assertThat(sliced, contains("qwerty", "baz"));
            }

            @Test
            void returnsOriginalVectorReferenceIfPossible() {
                Vector<String> original = Vector.wrap(new String[]{"foo", "bar", "baz"});
                Vector<String> slice1 = original.takeRight(100);
                Vector<String> slice2 = original.takeRight(3);
                assertSame(original, slice1);
                assertSame(original, slice2);
            }

            @Test
            void equality() {
                assertEquals(Vector.wrap(new Integer[]{4, 5, 6}),
                        Vector.wrap(new Integer[]{1, 2, 3, 4, 5, 6}).takeRight(3));
            }

        }

        @Nested
        @DisplayName("List")
        class List {
            @Test
            void throwsOnNegativeCount() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(singletonList(1)).takeRight(-1));
            }

            @Test
            void takesAsMuchAsItCan() {
                assertThat(Vector.wrap(asList(1, 2, 3)).takeRight(1_000_000),
                        contains(1, 2, 3));
            }

            @Test
            void onlyTakesWhatWasAskedFor() {
                assertThat(Vector.wrap(asList(1, 2, 3)).takeRight(3),
                        contains(1, 2, 3));
                assertThat(Vector.wrap(asList(1, 2, 3)).takeRight(2),
                        contains(2, 3));
                assertThat(Vector.wrap(asList(1, 2, 3)).takeRight(1),
                        contains(3));
                assertThat(Vector.wrap(asList(1, 2, 3)).takeRight(0),
                        emptyIterable());
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                java.util.List<String> originalUnderlying = asList("foo", "bar", "baz");
                Vector<String> original = Vector.wrap(originalUnderlying);
                Vector<String> sliced = original.takeRight(2);
                assertThat(sliced, contains("bar", "baz"));
                originalUnderlying.set(1, "qwerty");
                assertThat(sliced, contains("qwerty", "baz"));
            }

            @Test
            void returnsOriginalVectorReferenceIfPossible() {
                Vector<String> original = Vector.wrap(asList("foo", "bar", "baz"));
                Vector<String> slice1 = original.takeRight(100);
                Vector<String> slice2 = original.takeRight(3);
                assertSame(original, slice1);
                assertSame(original, slice2);
            }

            @Test
            void equality() {
                assertEquals(Vector.wrap(asList(1, 2, 3)),
                        Vector.wrap(asList(1, 2, 3, 4, 5, 6)).take(3));
            }

        }

    }

    @Nested
    @DisplayName("drop")
    class Drop {

        @Nested
        @DisplayName("array")
        class Array {

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

            @Test
            void equality() {
                assertEquals(Vector.wrap(new Integer[]{4, 5, 6}),
                        Vector.wrap(new Integer[]{1, 2, 3, 4, 5, 6}).drop(3));
            }

            @Test
            void findPositive() {
                assertEquals(just("bar"), Vector.wrap(new String[]{"foo", "bar", "baz"}).drop(1)
                        .find(Eq.eq("bar")));
            }

            @Test
            void findNegative() {
                assertEquals(nothing(), Vector.wrap(new String[]{"foo", "bar", "baz"}).drop(1)
                        .find(Eq.eq("foo")));
            }

            @Test
            void findIndexPositive() {
                assertEquals(just(0), Vector.wrap(new String[]{"foo", "bar", "baz"}).drop(1)
                        .findIndex(Eq.eq("bar")));
            }

            @Test
            void findIndexNegative() {
                assertEquals(nothing(), Vector.wrap(new String[]{"foo", "bar", "baz"}).drop(1)
                        .findIndex(Eq.eq("foo")));
            }

            @Test
            void stackSafe() {
                int BIG = 50_000;
                Integer[] underlying = new Integer[BIG];
                for (int i = 0; i < BIG; i++) {
                    underlying[i] = i;
                }
                Vector<Integer> sliced = Vector.wrap(underlying);
                for (int i = 0; i < BIG - 1; i++) {
                    sliced = sliced.drop(1);
                }
                assertEquals(just(BIG - 1), sliced.get(0));
            }

        }

        @Nested
        @DisplayName("List")
        class List {

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
                java.util.List<String> underlying = asList("foo", "bar", "baz");
                Vector<String> source = Vector.wrap(underlying);
                Vector<String> drop1 = source.drop(1);
                assertThat(drop1, contains("bar", "baz"));
                underlying.set(1, "qwerty");
                assertThat(drop1, contains("qwerty", "baz"));
            }

            @Test
            void equality() {
                assertEquals(Vector.wrap(asList(4, 5, 6)),
                        Vector.wrap(asList(1, 2, 3, 4, 5, 6)).drop(3));
            }

            @Test
            void findPositive() {
                assertEquals(just("bar"), Vector.wrap(asList("foo", "bar", "baz")).drop(1)
                        .find(Eq.eq("bar")));
            }

            @Test
            void findNegative() {
                assertEquals(nothing(), Vector.wrap(asList("foo", "bar", "baz")).drop(1)
                        .find(Eq.eq("foo")));
            }

            @Test
            void findIndexPositive() {
                assertEquals(just(0), Vector.wrap(asList("foo", "bar", "baz")).drop(1)
                        .findIndex(Eq.eq("bar")));
            }

            @Test
            void findIndexNegative() {
                assertEquals(nothing(), Vector.wrap(asList("foo", "bar", "baz")).drop(1)
                        .findIndex(Eq.eq("foo")));
            }

            @Test
            void stackSafe() {
                int BIG = 50_000;
                ArrayList<Integer> underlying = new ArrayList<>(BIG);
                for (int i = 0; i < BIG; i++) {
                    underlying.add(i);
                }
                Vector<Integer> sliced = Vector.wrap(underlying);
                for (int i = 0; i < BIG - 1; i++) {
                    sliced = sliced.drop(1);
                }
                assertEquals(just(BIG - 1), sliced.get(0));
            }

        }

    }

    @Nested
    @DisplayName("dropRight")
    class DropRight {

        @Nested
        @DisplayName("array")
        class Array {

            @Test
            void throwsOnNegativeCount() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(new Integer[]{1}).dropRight(-1));
            }

            @Test
            void countZeroReturnsSameReference() {
                Vector<Integer> source = Vector.wrap(new Integer[]{1, 2, 3});
                Vector<Integer> sliced = source.dropRight(0);
                assertSame(source, sliced);
            }

            @Test
            void countEqualToSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).dropRight(3));
            }

            @Test
            void countExceedingSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).dropRight(4));
                assertEquals(Vector.empty(), Vector.wrap(new Integer[]{1, 2, 3}).dropRight(1_000_000));
            }

            @Test
            void oneElement() {
                Vector<Integer> source = Vector.wrap(new Integer[]{1, 2, 3});
                assertThat(source.dropRight(1), contains(1, 2));
            }

            @Test
            void twoElements() {
                Vector<Integer> source = Vector.wrap(new Integer[]{1, 2, 3});
                assertThat(source.dropRight(2), contains(1));
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                String[] underlying = {"foo", "bar", "baz"};
                Vector<String> source = Vector.wrap(underlying);
                Vector<String> drop1 = source.dropRight(1);
                assertThat(drop1, contains("foo", "bar"));
                underlying[1] = "qwerty";
                assertThat(drop1, contains("foo", "qwerty"));
            }

            @Test
            void equality() {
                assertEquals(Vector.wrap(new Integer[]{1, 2, 3}),
                        Vector.wrap(new Integer[]{1, 2, 3, 4, 5, 6}).dropRight(3));
            }

        }

        @Nested
        @DisplayName("List")
        class List {

            @Test
            void throwsOnNegativeCount() {
                assertThrows(IllegalArgumentException.class, () -> Vector.wrap(singletonList(1)).dropRight(-1));
            }

            @Test
            void countZeroReturnsSameReference() {
                Vector<Integer> source = Vector.wrap(asList(1, 2, 3));
                Vector<Integer> sliced = source.dropRight(0);
                assertSame(source, sliced);
            }

            @Test
            void countEqualToSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).dropRight(3));
            }

            @Test
            void countExceedingSizeReturnsEmptyVector() {
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).dropRight(4));
                assertEquals(Vector.empty(), Vector.wrap(asList(1, 2, 3)).dropRight(1_000_000));
            }

            @Test
            void oneElement() {
                Vector<Integer> source = Vector.wrap(asList(1, 2, 3));
                assertThat(source.dropRight(1), contains(1, 2));
            }

            @Test
            void twoElements() {
                Vector<Integer> source = Vector.wrap(asList(1, 2, 3));
                assertThat(source.dropRight(2), contains(1));
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                java.util.List<String> underlying = asList("foo", "bar", "baz");
                Vector<String> source = Vector.wrap(underlying);
                Vector<String> drop1 = source.dropRight(1);
                assertThat(drop1, contains("foo", "bar"));
                underlying.set(1, "qwerty");
                assertThat(drop1, contains("foo", "qwerty"));
            }

            @Test
            void equality() {
                assertEquals(Vector.wrap(asList(1, 2, 3)),
                        Vector.wrap(asList(1, 2, 3, 4, 5, 6)).dropRight(3));
            }

        }

    }


    @Nested
    @DisplayName("slice")
    class Slice {

        @Nested
        @DisplayName("array")
        class Array {

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

            @Test
            void equality() {
                assertEquals(Vector.wrap(new Integer[]{2, 3, 4}),
                        Vector.wrap(new Integer[]{1, 2, 3, 4, 5, 6}).slice(1, 4));
            }

            @Test
            void findPositive() {
                assertEquals(just("bar"), Vector.wrap(new String[]{"foo", "bar", "baz"}).slice(1, 2)
                        .find(Eq.eq("bar")));
            }

            @Test
            void findNegative() {
                assertEquals(nothing(), Vector.wrap(new String[]{"foo", "bar", "baz"}).slice(1, 2)
                        .find(Eq.eq("foo")));
            }

            @Test
            void findIndexPositive() {
                assertEquals(just(0), Vector.wrap(new String[]{"foo", "bar", "baz"}).slice(1, 2)
                        .findIndex(Eq.eq("bar")));
            }

            @Test
            void findIndexNegative() {
                assertEquals(nothing(), Vector.wrap(new String[]{"foo", "bar", "baz"}).slice(1, 2)
                        .findIndex(Eq.eq("foo")));
            }

        }

        @Nested
        @DisplayName("List")
        class List {

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
                java.util.List<String> underlying = asList("foo", "bar", "baz");
                Vector<String> original = Vector.wrap(underlying);
                Vector<String> slice2 = original.slice(1, 3);
                Vector<String> slice3 = original.slice(2, 100);
                underlying.set(0, "qwerty");
                underlying.set(2, "quux");
                assertThat(original, contains("qwerty", "bar", "quux"));
                assertThat(slice2, contains("bar", "quux"));
                assertThat(slice3, contains("quux"));
            }

            @Test
            void equality() {
                assertEquals(Vector.wrap(asList(2, 3, 4)),
                        Vector.wrap(asList(1, 2, 3, 4, 5, 6)).slice(1, 4));
            }

            @Test
            void findPositive() {
                assertEquals(just("bar"), Vector.wrap(asList("foo", "bar", "baz")).slice(1, 2)
                        .find(Eq.eq("bar")));
            }

            @Test
            void findNegative() {
                assertEquals(nothing(), Vector.wrap(asList("foo", "bar", "baz")).slice(1, 2)
                        .find(Eq.eq("foo")));
            }

            @Test
            void findIndexPositive() {
                assertEquals(just(0), Vector.wrap(asList("foo", "bar", "baz")).slice(1, 2)
                        .findIndex(Eq.eq("bar")));
            }

            @Test
            void findIndexNegative() {
                assertEquals(nothing(), Vector.wrap(asList("foo", "bar", "baz")).slice(1, 2)
                        .findIndex(Eq.eq("foo")));
            }

        }

    }

    @Nested
    @DisplayName("reverse")
    class Reverse {

        @Nested
        @DisplayName("array")
        class Array {

            @Test
            void returnsEmptyVectorIfEmpty() {
                assertSame(Vector.empty(), Vector.wrap(new Integer[]{}).reverse());
            }

            @Test
            void returnsSelfIfOneElement() {
                Vector<Integer> subject = Vector.wrap(new Integer[]{});
                assertSame(subject, subject.reverse());
            }

            @Test
            void threeElements() {
                assertEquals(Vector.wrap(new Integer[]{3, 2, 1}), Vector.wrap(new Integer[]{1, 2, 3}).reverse());
            }

            @Test
            void doubleReverseReturnsOriginalReference() {
                Vector<Integer> subject = Vector.wrap(new Integer[]{1, 2, 3});
                assertSame(subject, subject.reverse().reverse());
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                Integer[] underlying = {1, 2, 3, 4};
                Vector<Integer> original = Vector.wrap(underlying);
                Vector<Integer> reversed = original.reverse();
                underlying[2] = 10;
                assertThat(reversed, contains(4, 10, 2, 1));
            }

            @Test
            void equality() {
                assertEquals(Vector.wrap(new Integer[]{3, 2, 1}),
                        Vector.wrap(new Integer[]{1, 2, 3}).reverse());
            }

        }

        @Nested
        @DisplayName("List")
        class List {

            @Test
            void returnsEmptyVectorIfEmpty() {
                assertSame(Vector.empty(), Vector.wrap(emptyList()).reverse());
            }

            @Test
            void returnsSelfIfOneElement() {
                Vector<Integer> subject = Vector.wrap(emptyList());
                assertSame(subject, subject.reverse());
            }

            @Test
            void threeElements() {
                assertEquals(Vector.wrap(asList(3, 2, 1)), Vector.wrap(asList(1, 2, 3)).reverse());
            }

            @Test
            void doubleReverseReturnsOriginalReference() {
                Vector<Integer> subject = Vector.wrap(asList(1, 2, 3));
                assertSame(subject, subject.reverse().reverse());
            }

            @Test
            void willNotMakeCopiesOfUnderlying() {
                java.util.List<Integer> underlying = asList(1, 2, 3, 4);
                Vector<Integer> original = Vector.wrap(underlying);
                Vector<Integer> reversed = original.reverse();
                underlying.set(2, 10);
                assertThat(reversed, contains(4, 10, 2, 1));
            }

            @Test
            void equality() {
                assertEquals(Vector.wrap(asList(3, 2, 1)),
                        Vector.wrap(asList(1, 2, 3)).reverse());
            }

        }
    }

    @Nested
    @DisplayName("zipWithIndex")
    class ZipWithIndex {

        private Vector<String> subject;

        @BeforeEach
        void beforeEach() {
            subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
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
        void willNotMakeCopiesOfUnderlying() {
            List<String> underlying = asList("foo", "bar", "baz");
            Vector<String> original = Vector.wrap(underlying);
            Vector<Tuple2<String, Integer>> subject = original.zipWithIndex();
            underlying.set(1, "qwerty");
            assertThat(subject, contains(tuple("foo", 0), tuple("qwerty", 1), tuple("baz", 2)));
        }

        @Test
        void equality() {
            assertEquals(Vector.wrap(asList(1, 2, 3)).zipWithIndex(),
                    Vector.wrap(new Integer[]{1, 2, 3}).zipWithIndex());
        }

    }

    @Nested
    @DisplayName("findByIndex")
    class FindByIndex {

        @Nested
        @DisplayName("array")
        class Array {

            @Test
            void returnsFirstFound() {
                assertEquals(just(1), Vector.wrap(new String[]{"foo", "bar", "baz", "bar", "foo"})
                        .findIndex(Eq.eq("bar")));
            }

            @Test
            void correctWhenReversed() {
                assertEquals(just(2), Vector.wrap(new String[]{"foo", "bar", "baz"})
                        .reverse()
                        .findIndex(Eq.eq("foo")));
            }

        }

        @Nested
        @DisplayName("List")
        class List {

            @Test
            void returnsFirstFound() {
                assertEquals(just(1), Vector.wrap(asList("foo", "bar", "baz", "bar", "foo"))
                        .findIndex(Eq.eq("bar")));
            }

            @Test
            void correctWhenReversed() {
                assertEquals(just(2), Vector.wrap(asList("foo", "bar", "baz"))
                        .reverse()
                        .findIndex(Eq.eq("foo")));
            }

        }

    }

    @Nested
    @DisplayName("cross")
    class Cross {

    }

}
