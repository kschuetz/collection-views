package dev.marksman.collectionviews;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static dev.marksman.collectionviews.NonEmptyIterable.nonEmptyIterable;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class NonEmptyIterableTest {

    @Test
    void testSingleton() {
        NonEmptyIterable<Integer> subject = nonEmptyIterable(1, emptyList());
        assertThat(subject, contains(1));
        assertEquals(1, subject.head());
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void testMultiple() {
        List<Integer> tail = asList(2, 3, 4, 5, 6);
        NonEmptyIterable<Integer> subject = nonEmptyIterable(1, tail);
        assertThat(subject, contains(1, 2, 3, 4, 5, 6));
        assertEquals(1, subject.head());
        assertThat(subject.tail(), contains(2, 3, 4, 5, 6));
    }

    @Test
    void testIterator() {
        NonEmptyIterable<Integer> subject = nonEmptyIterable(1, asList(2, 3));
        Iterator<Integer> iterator = subject.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());  // called second time
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(1, iterator.next());
        assertTrue(iterator.hasNext());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(2, iterator.next());
        assertTrue(iterator.hasNext());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(3, iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }
}
