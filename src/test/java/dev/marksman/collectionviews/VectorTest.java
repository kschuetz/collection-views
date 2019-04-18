package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.builtin.fn1.Repeat;
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

    @Test
    void emptyVectorAlwaysYieldsSameReference() {
        Vector<Integer> v1 = emptyVector();
        Vector<String> v2 = emptyVector();
        assertSame(v1, v2);
    }

    @Test
    void emptyVectorIsEmpty() {
        assertTrue(Vector.empty().isEmpty());
    }

    @Test
    void emptyVectorSizeIsZero() {
        assertEquals(0, Vector.empty().size());
    }

    @Test
    void emptyVectorGetReturnsNothing() {
        Vector<Object> subject = Vector.empty();
        assertEquals(nothing(), subject.get(0));
        assertEquals(nothing(), subject.get(1));
        assertEquals(nothing(), subject.get(-1));
    }

    @Test
    void emptyVectorUnsafeGetThrows() {
        Vector<Object> subject = Vector.empty();
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(0));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
    }

    @Test
    void emptyVectorIteratesCorrectly() {
        assertThat(Vector.empty(), emptyIterable());
    }

    @Test
    void emptyVectorTailIsEmpty() {
        assertThat(Vector.empty().tail(), emptyIterable());
    }

    @Test
    void wrapArray3IsNotEmpty() {
        Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
        assertFalse(subject.isEmpty());
    }

    @Test
    void wrapArray3SizeIs3() {
        Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
        assertEquals(3, subject.size());
    }

    @Test
    void wrapArray3GetForValidIndices() {
        Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
        assertEquals(just("foo"), subject.get(0));
        assertEquals(just("bar"), subject.get(1));
        assertEquals(just("baz"), subject.get(2));
    }

    @Test
    void wrapArray3GetForInvalidIndices() {
        Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
        assertEquals(nothing(), subject.get(3));
        assertEquals(nothing(), subject.get(-1));
    }

    @Test
    void wrapArray3UnsafeGetForValidIndices() {
        Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
        assertEquals("foo", subject.unsafeGet(0));
        assertEquals("bar", subject.unsafeGet(1));
        assertEquals("baz", subject.unsafeGet(2));
    }

    @Test
    void wrapArray3UnsafeGetThrowsForInvalidIndices() {
        Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
    }

    @Test
    void wrapArray3IteratesCorrectly() {
        Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
        assertThat(subject, contains("foo", "bar", "baz"));
    }

    @Test
    void wrapArray3TailIteratesCorrectly() {
        Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
        assertThat(subject.tail(), contains("bar", "baz"));
    }

    @Test
    void wrapSingletonArrayIteratesCorrectly() {
        Vector<String> subject = Vector.wrap(new String[]{"foo"});
        assertThat(subject, contains("foo"));
    }

    @Test
    void wrapSingletonArrayTailIsEmpty() {
        Vector<String> subject = Vector.wrap(new String[]{"foo"});
        assertTrue(subject.tail().isEmpty());
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void wrapEmptyArrayIsEmpty() {
        Vector<Integer> subject = Vector.wrap(new Integer[]{});
        assertTrue(subject.isEmpty());
    }

    @Test
    void wrapEmptyArraySizeIsZero() {
        Vector<Integer> subject = Vector.wrap(new Integer[]{});
        assertEquals(0, subject.size());
    }

    @Test
    void wrapEmptyArrayIteratesCorrectly() {
        Vector<Integer> subject = Vector.wrap(new Integer[]{});
        assertThat(subject, emptyIterable());
    }

    @Test
    void wrapEmptyArrayTailIsEmpty() {
        Vector<Integer> subject = Vector.wrap(new Integer[]{});
        assertTrue(subject.tail().isEmpty());
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void wrapArrayThrowsOnNullArgument() {
        Integer[] arr = null;
        assertThrows(NullPointerException.class, () -> Vector.wrap(arr));
    }

    @Test
    void wrapArrayWillNotMakeCopy() {
        Integer[] arr = new Integer[]{1, 2, 3};
        Vector<Integer> subject = Vector.wrap(arr);
        assertThat(subject, contains(1, 2, 3));
        arr[0] = 4;
        assertThat(subject, contains(4, 2, 3));
    }

    @Test
    void wrapArrayTailWillNotMakeCopy() {
        Integer[] arr = new Integer[]{1, 2, 3};
        Vector<Integer> subject = Vector.wrap(arr);
        assertThat(subject.tail(), contains(2, 3));
        arr[2] = 4;
        assertThat(subject.tail(), contains(2, 4));
    }

    @Test
    void wrapArrayGetWillNeverReturnNull() {
        Vector<String> subject = Vector.wrap(new String[]{"foo", null, "baz"});
        assertEquals(just("foo"), subject.get(0));
        assertEquals(nothing(), subject.get(1));
        assertEquals(just("baz"), subject.get(2));
    }

    @Test
    void wrapArrayIteratorNextReturnsCorrectElements() {
        Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
        Iterator<String> iterator = subject.iterator();
        assertEquals("foo", iterator.next());
        assertEquals("bar", iterator.next());
        assertEquals("baz", iterator.next());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void wrapArrayIteratorHasNextCanBeCalledMultipleTimes() {
        Vector<String> subject = Vector.wrap(new String[]{"foo", "bar", "baz"});
        Iterator<String> iterator = subject.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertEquals("foo", iterator.next());
    }

    @Test
    void wrapArrayIteratorHasNextReturnsFalseIfNothingRemains() {
        Vector<String> subject = Vector.wrap(new String[]{"foo"});
        Iterator<String> iterator = subject.iterator();
        iterator.next();
        assertFalse(iterator.hasNext());
    }

    @Test
    void wrapArrayIteratorNextThrowsIfNothingRemains() {
        Vector<String> subject = Vector.wrap(new String[]{"foo"});
        Iterator<String> iterator = subject.iterator();
        iterator.next();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void wrapArrayIteratorThrowsIfRemoveIsCalled() {
        Vector<String> subject = Vector.wrap(new String[]{"foo"});
        Iterator<String> iterator = subject.iterator();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void wrapList3IsNotEmpty() {
        Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
        assertFalse(subject.isEmpty());
    }

    @Test
    void wrapList3SizeIs3() {
        Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
        assertEquals(3, subject.size());
    }

    @Test
    void wrapList3GetForValidIndices() {
        Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
        assertEquals(just("foo"), subject.get(0));
        assertEquals(just("bar"), subject.get(1));
        assertEquals(just("baz"), subject.get(2));
    }

    @Test
    void wrapList3GetForInvalidIndices() {
        Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
        assertEquals(nothing(), subject.get(3));
        assertEquals(nothing(), subject.get(-1));
    }

    @Test
    void wrapList3UnsafeGetForValidIndices() {
        Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
        assertEquals("foo", subject.unsafeGet(0));
        assertEquals("bar", subject.unsafeGet(1));
        assertEquals("baz", subject.unsafeGet(2));
    }

    @Test
    void wrapList3UnsafeGetThrowsForInvalidIndices() {
        Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
    }

    @Test
    void wrapList3IteratesCorrectly() {
        Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
        assertThat(subject, contains("foo", "bar", "baz"));
    }

    @Test
    void wrapList3TailIteratesCorrectly() {
        Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
        assertThat(subject.tail(), contains("bar", "baz"));
    }

    @Test
    void wrapSingletonListIteratesCorrectly() {
        Vector<String> subject = Vector.wrap(singletonList("foo"));
        assertThat(subject, contains("foo"));
    }

    @Test
    void wrapSingletonListTailIsEmpty() {
        Vector<String> subject = Vector.wrap(singletonList("foo"));
        assertTrue(subject.tail().isEmpty());
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void wrapEmptyListIsEmpty() {
        Vector<Integer> subject = Vector.wrap(emptyList());
        assertTrue(subject.isEmpty());
    }

    @Test
    void wrapEmptyListSizeIsZero() {
        Vector<Integer> subject = Vector.wrap(emptyList());
        assertEquals(0, subject.size());
    }

    @Test
    void wrapEmptyListIteratesCorrectly() {
        Vector<Integer> subject = Vector.wrap(emptyList());
        assertThat(subject, emptyIterable());
    }

    @Test
    void wrapEmptyListTailIsEmpty() {
        Vector<Integer> subject = Vector.wrap(emptyList());
        assertTrue(subject.tail().isEmpty());
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void wrapListThrowsOnNullArgument() {
        Integer[] arr = null;
        assertThrows(NullPointerException.class, () -> Vector.wrap(arr));
    }

    @Test
    void wrapListWillNotMakeCopy() {
        List<Integer> list = asList(1, 2, 3);
        Vector<Integer> subject = Vector.wrap(list);
        assertThat(subject, contains(1, 2, 3));
        list.set(0, 4);
        assertThat(subject, contains(4, 2, 3));
    }

    @Test
    void wrapListTailWillNotMakeCopy() {
        List<Integer> list = asList(1, 2, 3);
        Vector<Integer> subject = Vector.wrap(list);
        assertThat(subject.tail(), contains(2, 3));
        list.set(2, 4);
        assertThat(subject.tail(), contains(2, 4));
    }

    @Test
    void wrapListGetWillNeverReturnNull() {
        Vector<String> subject = Vector.wrap(asList("foo", null, "baz"));
        assertEquals(just("foo"), subject.get(0));
        assertEquals(nothing(), subject.get(1));
        assertEquals(just("baz"), subject.get(2));
    }

    @Test
    void wrapListIteratorNextReturnsCorrectElements() {
        Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
        Iterator<String> iterator = subject.iterator();
        assertEquals("foo", iterator.next());
        assertEquals("bar", iterator.next());
        assertEquals("baz", iterator.next());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void wrapListIteratorHasNextCanBeCalledMultipleTimes() {
        Vector<String> subject = Vector.wrap(asList("foo", "bar", "baz"));
        Iterator<String> iterator = subject.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertEquals("foo", iterator.next());
    }

    @Test
    void wrapListIteratorHasNextReturnsFalseIfNothingRemains() {
        Vector<String> subject = Vector.wrap(singletonList("foo"));
        Iterator<String> iterator = subject.iterator();
        iterator.next();
        assertFalse(iterator.hasNext());
    }

    @Test
    void wrapListIteratorNextThrowsIfNothingRemains() {
        Vector<String> subject = Vector.wrap(singletonList("foo"));
        Iterator<String> iterator = subject.iterator();
        iterator.next();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void wrapListIteratorThrowsIfRemoveIsCalled() {
        Vector<String> subject = Vector.wrap(singletonList("foo"));
        Iterator<String> iterator = subject.iterator();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void takeThrowsOnNegativeCount() {
        assertThrows(IllegalArgumentException.class, () -> Vector.take(-1, singletonList(1)));
    }

    @Test
    void takeThrowsOnNullSource() {
        assertThrows(NullPointerException.class, () -> Vector.take(0, null));
    }

    @Test
    void takeTakesAsMuchAsItCan() {
        assertThat(Vector.take(1_000_000, asList(1, 2, 3)),
                contains(1, 2, 3));
    }

    @Test
    void takeOnlyTakesWhatWasAskedFor() {
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
    void takeWillNotEvaluateIterableUnlessNecessary() {
        Iterable<String> iterable = () -> {
            throw new AssertionError("Iterable was evaluated");
        };
        Vector.take(0, iterable);
    }

    @Test
    void takeWillNotMakeCopiesOfUnderlyingForVectors() {
        List<String> originalUnderlying = asList("foo", "bar", "baz");
        Vector<String> original = Vector.wrap(originalUnderlying);
        Vector<String> sliced = Vector.take(2, original);
        assertThat(sliced, contains("foo", "bar"));
        originalUnderlying.set(0, "qwerty");
        assertThat(sliced, contains("qwerty", "bar"));
    }

    @Test
    void takeWillNotMakeCopiesOfUnderlyingForLists() {
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
    void takeReturnsOriginalVectorReferenceIfPossible() {
        Vector<String> original = Vector.wrap(asList("foo", "bar", "baz"));
        Vector<String> slice1 = Vector.take(100, original);
        Vector<String> slice2 = Vector.take(3, slice1);
        assertSame(original, slice1);
        assertSame(original, slice2);
    }

    @Test
    void dropThrowsOnNegativeCount() {
        assertThrows(IllegalArgumentException.class, () -> Vector.drop(-1, Vector.of(1)));
    }

    @Test
    void dropThrowsOnNullSource() {
        assertThrows(NullPointerException.class, () -> Vector.drop(0, null));
    }

    @Test
    void dropOfZeroReturnsSameReference() {
        Vector<Integer> source = Vector.of(1, 2, 3);
        Vector<Integer> sliced = Vector.drop(0, source);
        assertSame(source, sliced);
    }

    @Test
    void dropOfCountEqualToSizeReturnsEmptyVector() {
        assertEquals(emptyVector(), Vector.drop(3, Vector.of(1, 2, 3)));
    }

    @Test
    void dropOfCountExceedingSizeReturnsEmptyVector() {
        assertEquals(emptyVector(), Vector.drop(4, Vector.of(1, 2, 3)));
        assertEquals(emptyVector(), Vector.drop(1_000_000, Vector.of(1, 2, 3)));
    }

    @Test
    void dropOneElement() {
        Vector<Integer> source = Vector.of(1, 2, 3);
        assertThat(Vector.drop(1, source), contains(2, 3));
    }

    @Test
    void dropTwoElements() {
        Vector<Integer> source = Vector.of(1, 2, 3);
        assertThat(Vector.drop(2, source), contains(3));
    }

    @Test
    void dropWillNotMakeCopiesOfUnderlyingArrays() {
        String[] underlying = {"foo", "bar", "baz"};
        Vector<String> source = Vector.wrap(underlying);
        Vector<String> drop1 = Vector.drop(1, source);
        assertThat(drop1, contains("bar", "baz"));
        underlying[1] = "qwerty";
        assertThat(drop1, contains("qwerty", "baz"));
    }

    @Test
    void dropWillNotMakeCopiesOfUnderlyingLists() {
        List<String> underlying = asList("foo", "bar", "baz");
        Vector<String> source = Vector.wrap(underlying);
        Vector<String> drop1 = Vector.drop(1, source);
        assertThat(drop1, contains("bar", "baz"));
        underlying.set(1, "qwerty");
        assertThat(drop1, contains("qwerty", "baz"));
    }

    @Test
    void sliceThrowsOnNegativeStartIndex() {
        assertThrows(IllegalArgumentException.class, () -> Vector.slice(-1, 1, Vector.empty()));
    }

    @Test
    void sliceThrowsOnNegativeEndIndex() {
        assertThrows(IllegalArgumentException.class, () -> Vector.slice(0, -1, Vector.empty()));
    }

    @Test
    void sliceThrowsOnNullSource() {
        assertThrows(NullPointerException.class, () -> Vector.slice(0, 0, null));
    }

    @Test
    void sliceReturnsEmptyVectorIfWidthIsZero() {
        Iterable<String> infinite = Repeat.repeat("foo");
        assertEquals(Vector.empty(), Vector.slice(0, 0, infinite));
        assertEquals(Vector.empty(), Vector.slice(1_000_000, 1_000_000, infinite));
    }

    @Test
    void sliceReturnsEmptyVectorIfWidthLessThanZero() {
        Iterable<String> infinite = Repeat.repeat("foo");
        assertEquals(Vector.empty(), Vector.slice(10, 9, infinite));
        assertEquals(Vector.empty(), Vector.slice(1_000_000, 0, infinite));
    }

    @Test
    void sliceListTakesAsMuchAsItCan() {
        assertThat(Vector.slice(1, 1_000_000, asList(1, 2, 3)),
                contains(2, 3));
    }

    @Test
    void sliceVectorTakesAsMuchAsItCan() {
        assertThat(Vector.slice(1, 1_000_000, Vector.of(1, 2, 3)),
                contains(2, 3));
    }

    @Test
    void sliceListOnlyTakesWhatWasAskedFor() {
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
    void sliceVectorOnlyTakesWhatWasAskedFor() {
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
    void sliceWillNotEvaluateIterableUnlessNecessary() {
        Iterable<String> iterable = () -> {
            throw new AssertionError("Iterable was evaluated");
        };
        Vector.slice(0, 0, iterable);
        Vector.slice(1, 0, iterable);
        Vector.slice(1, 1, iterable);
    }

    @Test
    void sliceListOfStartIndexEqualToSizeReturnsEmptyVector() {
        assertEquals(emptyVector(), Vector.slice(3, 6, asList(1, 2, 3)));
    }

    @Test
    void sliceVectorOfStartIndexEqualToSizeReturnsEmptyVector() {
        assertEquals(emptyVector(), Vector.slice(3, 6, Vector.of(1, 2, 3)));
    }

    @Test
    void sliceListOfStartIndexExceedingSizeReturnsEmptyVector() {
        assertEquals(emptyVector(), Vector.slice(4, 3, asList(1, 2, 3)));
        assertEquals(emptyVector(), Vector.slice(1_000_000, 3, asList(1, 2, 3)));
    }

    @Test
    void sliceVectorOfStartIndexExceedingSizeReturnsEmptyVector() {
        assertEquals(emptyVector(), Vector.slice(4, 3, Vector.of(1, 2, 3)));
        assertEquals(emptyVector(), Vector.slice(1_000_000, 3, Vector.of(1, 2, 3)));
    }

    @Test
    void sliceWillNotMakeCopiesOfUnderlyingForVectors() {
        List<String> originalUnderlying = asList("foo", "bar", "baz");
        Vector<String> original = Vector.wrap(originalUnderlying);
        Vector<String> sliced = Vector.slice(1, 10000, original);
        assertThat(sliced, contains("bar", "baz"));
        originalUnderlying.set(2, "qwerty");
        assertThat(sliced, contains("bar", "qwerty"));
    }

    @Test
    void sliceWillNotMakeCopiesOfUnderlyingForLists() {
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

    @Test
    void sliceCanHandleInfiniteSource() {
        Vector<String> subject = Vector.slice(100, 102, Repeat.repeat("foo"));
        assertThat(subject, contains("foo", "foo"));
    }

}