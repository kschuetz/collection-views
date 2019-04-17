package dev.marksman.collectionviews;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class VectorsTest {

    @Test
    void emptyVector() {
        Vector<String> subject = Vectors.empty();
        assertTrue(subject.isEmpty());
        assertEquals(0, subject.size());
        assertEquals(nothing(), subject.get(0));
        assertEquals(nothing(), subject.get(1));
        assertEquals(nothing(), subject.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(0));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
        assertThat(subject, emptyIterable());
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void wrapArray() {
        String[] arr = new String[]{"foo", "bar", "baz"};
        Vector<String> subject = Vectors.wrap(arr);
        assertFalse(subject.isEmpty());
        assertEquals(3, subject.size());
        assertEquals(just("foo"), subject.get(0));
        assertEquals(just("bar"), subject.get(1));
        assertEquals(just("baz"), subject.get(2));
        assertEquals(nothing(), subject.get(3));
        assertEquals(nothing(), subject.get(-1));
        assertEquals("foo", subject.unsafeGet(0));
        assertEquals("bar", subject.unsafeGet(1));
        assertEquals("baz", subject.unsafeGet(2));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
        assertThat(subject, contains("foo", "bar", "baz"));
        assertThat(subject.tail(), contains("bar", "baz"));
    }

    @Test
    void wrapSingletonArray() {
        String[] arr = new String[]{"foo"};
        Vector<String> subject = Vectors.wrap(arr);
        assertFalse(subject.isEmpty());
        assertEquals(1, subject.size());
        assertEquals(just("foo"), subject.get(0));
        assertEquals(nothing(), subject.get(2));
        assertEquals(nothing(), subject.get(-1));
        assertEquals("foo", subject.unsafeGet(0));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
        assertThat(subject, contains("foo"));
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void wrapEmptyArray() {
        Integer[] arr = new Integer[]{};
        Vector<Integer> subject = Vectors.wrap(arr);
        assertTrue(subject.isEmpty());
        assertEquals(0, subject.size());
        assertEquals(nothing(), subject.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(0));
        assertThat(subject, emptyIterable());
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void wrapArrayNullArgument() {
        Integer[] arr = null;
        assertThrows(NullPointerException.class, () -> Vectors.wrap(arr));
    }

    @Test
    void wrapArrayWillNotAcceptArrayContainingAnyNulls() {
        Integer[] arr = new Integer[]{1, 2, null, 3};
        assertThrows(IllegalStateException.class, () -> Vectors.wrap(arr));
    }

    @Test
    void wrapArrayWillNotMakeCopy() {
        Integer[] arr = new Integer[]{1, 2, 3};
        Vector<Integer> subject = Vectors.wrap(arr);
        assertThat(subject, contains(1, 2, 3));
        arr[0] = 4;
        assertThat(subject, contains(4, 2, 3));
    }

    @Test
    void wrapArrayIterator() {
        String[] arr = new String[]{"foo", "bar", "baz"};
        Vector<String> subject = Vectors.wrap(arr);
        Iterator<String> iterator = subject.iterator();
        assertTrue(iterator.hasNext());
        //noinspection ConstantConditions
        assertTrue(iterator.hasNext());  // called second time
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals("foo", iterator.next());
        assertTrue(iterator.hasNext());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals("bar", iterator.next());
        assertTrue(iterator.hasNext());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals("baz", iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void wrapList() {
        String[] arr = new String[]{"foo", "bar", "baz"};
        Vector<String> subject = Vectors.wrap(arr);
        assertFalse(subject.isEmpty());
        assertEquals(3, subject.size());
        assertEquals(just("foo"), subject.get(0));
        assertEquals(just("bar"), subject.get(1));
        assertEquals(just("baz"), subject.get(2));
        assertEquals(nothing(), subject.get(3));
        assertEquals(nothing(), subject.get(-1));
        assertEquals("foo", subject.unsafeGet(0));
        assertEquals("bar", subject.unsafeGet(1));
        assertEquals("baz", subject.unsafeGet(2));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
        assertThat(subject, contains("foo", "bar", "baz"));
        assertThat(subject.tail(), contains("bar", "baz"));
    }

    @Test
    void wrapSingletonList() {
        String[] arr = new String[]{"foo"};
        Vector<String> subject = Vectors.wrap(arr);
        assertFalse(subject.isEmpty());
        assertEquals(1, subject.size());
        assertEquals(just("foo"), subject.get(0));
        assertEquals(nothing(), subject.get(2));
        assertEquals(nothing(), subject.get(-1));
        assertEquals("foo", subject.unsafeGet(0));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
        assertThat(subject, contains("foo"));
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void wrapEmptyList() {
        ArrayList<Integer> list = new ArrayList<>();
        Vector<Integer> subject = Vectors.wrap(list);
        assertTrue(subject.isEmpty());
        assertEquals(0, subject.size());
        assertEquals(nothing(), subject.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(0));
        assertThat(subject, emptyIterable());
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void wrapListNullArgument() {
        List<String> list = null;
        assertThrows(NullPointerException.class, () -> Vectors.wrap(list));
    }

    @Test
    void wrapListWillNotAcceptListContainingAnyNulls() {
        List<String> list = asList("foo", "bar", null, "baz");
        assertThrows(IllegalStateException.class, () -> Vectors.wrap(list));
    }

    @Test
    void wrapListWillNotMakeCopy() {
        List<Integer> list = asList(1, 2, 3);
        Vector<Integer> subject = Vectors.wrap(list);
        assertThat(subject, contains(1, 2, 3));
        list.set(0, 4);
        assertThat(subject, contains(4, 2, 3));
    }

    @Test
    void wrapListIterator() {
        List<String> list = asList("foo", "bar", "baz");
        Vector<String> subject = Vectors.wrap(list);
        Iterator<String> iterator = subject.iterator();
        assertTrue(iterator.hasNext());
        //noinspection ConstantConditions
        assertTrue(iterator.hasNext());  // called second time
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals("foo", iterator.next());
        assertTrue(iterator.hasNext());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals("bar", iterator.next());
        assertTrue(iterator.hasNext());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals("baz", iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void takeOnlyTakesAsMuchAsItCan() {
        assertThat(Vectors.take(1_000_000, asList(1, 2, 3)),
                contains(1, 2, 3));
    }

    @Test
    void takeOnlyTakesWhatWasAskedFor() {
        assertThat(Vectors.take(3, asList(1, 2, 3)),
                contains(1, 2, 3));
        assertThat(Vectors.take(2, asList(1, 2, 3)),
                contains(1, 2));
        assertThat(Vectors.take(1, asList(1, 2, 3)),
                contains(1));
        assertThat(Vectors.take(0, asList(1, 2, 3)),
                emptyIterable());
    }

    @Test
    void takeWithBadArguments() {
        assertThrows(IllegalArgumentException.class, () -> Vectors.take(-1, singletonList(1)));
        assertThrows(NullPointerException.class, () -> Vectors.take(0, null));
    }

    @Test
    void takeWillNotMakeCopiesOfUnderlyingForVectors() {
        List<String> originalUnderlying = asList("foo", "bar", "baz");
        Vector<String> original = Vectors.wrap(originalUnderlying);
        Vector<String> sliced = Vectors.take(2, original);
        assertThat(sliced, contains("foo", "bar"));
        originalUnderlying.set(0, "qwerty");
        assertThat(sliced, contains("qwerty", "bar"));
    }

    @Test
    void takeWillNotMakeCopiesOfUnderlyingForLists() {
        List<String> originalList = asList("foo", "bar", "baz");
        Vector<String> slice1 = Vectors.take(100, originalList);
        Vector<String> slice2 = Vectors.take(3, originalList);
        Vector<String> slice3 = Vectors.take(1, originalList);
        originalList.set(0, "qwerty");
        originalList.set(2, "quux");
        assertThat(slice1, contains("qwerty", "bar", "quux"));
        assertThat(slice2, contains("qwerty", "bar", "quux"));
        assertThat(slice3, contains("qwerty"));
    }

    @Test
    void takeWillReturnOriginalVectorReferenceIfPossible() {
        Vector<String> original = Vectors.wrap(asList("foo", "bar", "baz"));
        Vector<String> slice1 = Vectors.take(100, original);
        Vector<String> slice2 = Vectors.take(3, slice1);
        assertSame(original, slice1);
        assertSame(original, slice2);
    }

    @Test
    void takeWillNotAcceptNullsAsElements() {
        assertThrows(IllegalStateException.class, () -> Vectors.take(3, asList("foo", null, "bar")));
    }

//    @Test
//    void takeWillNotEvaluateIterableBeyondWhatIsNeeded() {
//        // TODO
//        Vector<String> subject = Vectors.take(3, asList("foo", "bar", "baz", null, "qwerty"));
//        assertThat(subject, contains("foo", "bar", "baz"));
//    }

}