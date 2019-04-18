package dev.marksman.collectionviews;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class NonEmptyVectorTest {

    @Test
    void nonEmptyWrapArray3IsNotEmpty() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{"bar", "baz"});
        assertFalse(subject.isEmpty());
    }

    @Test
    void nonEmptyWrapArray3SizeIs3() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{"bar", "baz"});
        assertEquals(3, subject.size());
    }

    @Test
    void nonEmptyWrapArray3Head() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{"bar", "baz"});
        assertEquals("foo", subject.head());
    }

    @Test
    void nonEmptyWrapArray3GetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{"bar", "baz"});
        assertEquals(just("foo"), subject.get(0));
        assertEquals(just("bar"), subject.get(1));
        assertEquals(just("baz"), subject.get(2));
    }

    @Test
    void nonEmptyWrapArray3GetForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{"bar", "baz"});
        assertEquals(nothing(), subject.get(3));
        assertEquals(nothing(), subject.get(-1));
    }

    @Test
    void nonEmptyWrapArray3UnsafeGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{"bar", "baz"});
        assertEquals("foo", subject.unsafeGet(0));
        assertEquals("bar", subject.unsafeGet(1));
        assertEquals("baz", subject.unsafeGet(2));
    }

    @Test
    void nonEmptyWrapArray3UnsafeGetThrowsForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{"bar", "baz"});
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
    }

    @Test
    void nonEmptyWrapArray3IteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{"bar", "baz"});
        assertThat(subject, contains("foo", "bar", "baz"));
    }

    @Test
    void nonEmptyWrapArray3TailIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{"bar", "baz"});
        assertThat(subject.tail(), contains("bar", "baz"));
    }

    @Test
    void nonEmptyWrapArrayWillNotMakeCopy() {
        String[] originalArray = {"bar", "baz"};
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", originalArray);
        assertThat(subject, contains("foo", "bar", "baz"));
        originalArray[1] = "quux";
        assertThat(subject, contains("foo", "bar", "quux"));
    }

    @Test
    void nonEmptyWrapList3IsNotEmpty() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", asList("bar", "baz"));
        assertFalse(subject.isEmpty());
    }

    @Test
    void nonEmptyWrapList3SizeIs3() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", asList("bar", "baz"));
        assertEquals(3, subject.size());
    }

    @Test
    void nonEmptyWrapList3Head() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", asList("bar", "baz"));
        assertEquals("foo", subject.head());
    }

    @Test
    void nonEmptyWrapList3GetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", asList("bar", "baz"));
        assertEquals(just("foo"), subject.get(0));
        assertEquals(just("bar"), subject.get(1));
        assertEquals(just("baz"), subject.get(2));
    }

    @Test
    void nonEmptyWrapList3GetForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", asList("bar", "baz"));
        assertEquals(nothing(), subject.get(3));
        assertEquals(nothing(), subject.get(-1));
    }

    @Test
    void nonEmptyWrapList3UnsafeGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", asList("bar", "baz"));
        assertEquals("foo", subject.unsafeGet(0));
        assertEquals("bar", subject.unsafeGet(1));
        assertEquals("baz", subject.unsafeGet(2));
    }

    @Test
    void nonEmptyWrapList3UnsafeGetThrowsForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", asList("bar", "baz"));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
    }

    @Test
    void nonEmptyWrapList3IteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", asList("bar", "baz"));
        assertThat(subject, contains("foo", "bar", "baz"));
    }

    @Test
    void nonEmptyWrapList3TailIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", asList("bar", "baz"));
        assertThat(subject.tail(), contains("bar", "baz"));
    }

    @Test
    void nonEmptyWrapListWillNotMakeCopy() {
        List<String> originalList = asList("bar", "baz");
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", originalList);
        assertThat(subject, contains("foo", "bar", "baz"));
        originalList.set(1, "quux");
        assertThat(subject, contains("foo", "bar", "quux"));
    }

    @Test
    void nonEmptyWrapVector3IsNotEmpty() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.of("bar", "baz"));
        assertFalse(subject.isEmpty());
    }

    @Test
    void nonEmptyWrapVector3SizeIs3() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.of("bar", "baz"));
        assertEquals(3, subject.size());
    }

    @Test
    void nonEmptyWrapVector3Head() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.of("bar", "baz"));
        assertEquals("foo", subject.head());
    }

    @Test
    void nonEmptyWrapVector3GetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.of("bar", "baz"));
        assertEquals(just("foo"), subject.get(0));
        assertEquals(just("bar"), subject.get(1));
        assertEquals(just("baz"), subject.get(2));
    }

    @Test
    void nonEmptyWrapVector3GetForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.of("bar", "baz"));
        assertEquals(nothing(), subject.get(3));
        assertEquals(nothing(), subject.get(-1));
    }

    @Test
    void nonEmptyWrapVector3UnsafeGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.of("bar", "baz"));
        assertEquals("foo", subject.unsafeGet(0));
        assertEquals("bar", subject.unsafeGet(1));
        assertEquals("baz", subject.unsafeGet(2));
    }

    @Test
    void nonEmptyWrapVector3UnsafeGetThrowsForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.of("bar", "baz"));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
    }

    @Test
    void nonEmptyWrapVector3IteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.of("bar", "baz"));
        assertThat(subject, contains("foo", "bar", "baz"));
    }

    @Test
    void nonEmptyWrapVector3TailIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.of("bar", "baz"));
        assertThat(subject.tail(), contains("bar", "baz"));
    }

    @Test
    void nonEmptyVector3IsNotEmpty() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo", "bar", "baz");
        assertFalse(subject.isEmpty());
    }

    @Test
    void nonEmptyVector3SizeIs3() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo", "bar", "baz");
        assertEquals(3, subject.size());
    }

    @Test
    void nonEmptyVector3Head() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo", "bar", "baz");
        assertEquals("foo", subject.head());
    }

    @Test
    void nonEmptyVector3GetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo", "bar", "baz");
        assertEquals(just("foo"), subject.get(0));
        assertEquals(just("bar"), subject.get(1));
        assertEquals(just("baz"), subject.get(2));
    }

    @Test
    void nonEmptyVector3GetForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo", "bar", "baz");
        assertEquals(nothing(), subject.get(3));
        assertEquals(nothing(), subject.get(-1));
    }

    @Test
    void nonEmptyVector3UnsafeGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo", "bar", "baz");
        assertEquals("foo", subject.unsafeGet(0));
        assertEquals("bar", subject.unsafeGet(1));
        assertEquals("baz", subject.unsafeGet(2));
    }

    @Test
    void nonEmptyVector3UnsafeGetThrowsForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo", "bar", "baz");
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
    }

    @Test
    void nonEmptyVector3IteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo", "bar", "baz");
        assertThat(subject, contains("foo", "bar", "baz"));
    }

    @Test
    void nonEmptyVector3TailIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo", "bar", "baz");
        assertThat(subject.tail(), contains("bar", "baz"));
    }

    @Test
    void nonEmptySingletonArrayIsNotEmpty() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{});
        assertFalse(subject.isEmpty());
    }

    @Test
    void nonEmptySingletonArraySizeIs1() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{});
        assertEquals(1, subject.size());
    }

    @Test
    void nonEmptySingletonArrayHead() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{});
        assertEquals("foo", subject.head());
    }

    @Test
    void nonEmptySingletonArrayGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{});
        assertEquals(just("foo"), subject.get(0));
    }

    @Test
    void nonEmptySingletonArrayGetForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{});
        assertEquals(nothing(), subject.get(1));
        assertEquals(nothing(), subject.get(-1));
    }

    @Test
    void nonEmptySingletonArrayUnsafeGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{});
        assertEquals("foo", subject.unsafeGet(0));
    }

    @Test
    void nonEmptySingletonArrayUnsafeGetThrowsForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
    }

    @Test
    void nonEmptySingletonArrayIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{});
        assertThat(subject, contains("foo"));
    }

    @Test
    void nonEmptySingletonArrayTailIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", new String[]{});
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void nonEmptySingletonListIsNotEmpty() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", emptyList());
        assertFalse(subject.isEmpty());
    }

    @Test
    void nonEmptySingletonListSizeIs1() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", emptyList());
        assertEquals(1, subject.size());
    }

    @Test
    void nonEmptySingletonListHead() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", emptyList());
        assertEquals("foo", subject.head());
    }

    @Test
    void nonEmptySingletonListGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", emptyList());
        assertEquals(just("foo"), subject.get(0));
    }

    @Test
    void nonEmptySingletonListGetForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", emptyList());
        assertEquals(nothing(), subject.get(1));
        assertEquals(nothing(), subject.get(-1));
    }

    @Test
    void nonEmptySingletonListUnsafeGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", emptyList());
        assertEquals("foo", subject.unsafeGet(0));
    }

    @Test
    void nonEmptySingletonListUnsafeGetThrowsForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", emptyList());
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
    }

    @Test
    void nonEmptySingletonListIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", emptyList());
        assertThat(subject, contains("foo"));
    }

    @Test
    void nonEmptySingletonListTailIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", emptyList());
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void nonEmptySingletonVectorIsNotEmpty() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.empty());
        assertFalse(subject.isEmpty());
    }

    @Test
    void nonEmptySingletonVectorSizeIs1() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.empty());
        assertEquals(1, subject.size());
    }

    @Test
    void nonEmptySingletonVectorHead() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.empty());
        assertEquals("foo", subject.head());
    }

    @Test
    void nonEmptySingletonVectorGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.empty());
        assertEquals(just("foo"), subject.get(0));
    }

    @Test
    void nonEmptySingletonVectorGetForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.empty());
        assertEquals(nothing(), subject.get(1));
        assertEquals(nothing(), subject.get(-1));
    }

    @Test
    void nonEmptySingletonVectorUnsafeGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.empty());
        assertEquals("foo", subject.unsafeGet(0));
    }

    @Test
    void nonEmptySingletonVectorUnsafeGetThrowsForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.empty());
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
    }

    @Test
    void nonEmptySingletonVectorIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.empty());
        assertThat(subject, contains("foo"));
    }

    @Test
    void nonEmptySingletonVectorTailIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.wrap("foo", Vector.empty());
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void nonEmptySingletonIsNotEmpty() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo");
        assertFalse(subject.isEmpty());
    }

    @Test
    void nonEmptySingletonSizeIs1() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo");
        assertEquals(1, subject.size());
    }

    @Test
    void nonEmptySingletonHead() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo");
        assertEquals("foo", subject.head());
    }

    @Test
    void nonEmptySingletonGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo");
        assertEquals(just("foo"), subject.get(0));
    }

    @Test
    void nonEmptySingletonGetForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo");
        assertEquals(nothing(), subject.get(1));
        assertEquals(nothing(), subject.get(-1));
    }

    @Test
    void nonEmptySingletonUnsafeGetForValidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo");
        assertEquals("foo", subject.unsafeGet(0));
    }

    @Test
    void nonEmptySingletonUnsafeGetThrowsForInvalidIndices() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo");
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
        assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
    }

    @Test
    void nonEmptySingletonIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo");
        assertThat(subject, contains("foo"));
    }

    @Test
    void nonEmptySingletonTailIteratesCorrectly() {
        NonEmptyVector<String> subject = NonEmptyVector.of("foo");
        assertThat(subject.tail(), emptyIterable());
    }

    @Test
    void nonEmptyTryWrapArraySuccess() {
        NonEmptyVector<String> result = NonEmptyVector.tryWrap(new String[]{"foo"}).orElseThrow(AssertionError::new);
        assertThat(result, contains("foo"));
        assertEquals("foo", result.head());
        assertEquals(1, result.size());
    }

    @Test
    void nonEmptyTryWrapListSuccess() {
        NonEmptyVector<String> result = NonEmptyVector.tryWrap(singletonList("foo")).orElseThrow(AssertionError::new);
        assertThat(result, contains("foo"));
        assertEquals("foo", result.head());
        assertEquals(1, result.size());
    }

    @Test
    void nonEmptyTryWrapVectorSuccess() {
        NonEmptyVector<String> result = NonEmptyVector.tryWrap(Vector.of("foo")).orElseThrow(AssertionError::new);
        assertThat(result, contains("foo"));
        assertEquals("foo", result.head());
        assertEquals(1, result.size());
    }

    @Test
    void nonEmptyTryWrapArrayFailure() {
        assertEquals(nothing(), NonEmptyVector.tryWrap(new String[]{}));
    }

    @Test
    void nonEmptyTryWrapListFailure() {
        assertEquals(nothing(), NonEmptyVector.tryWrap(emptyList()));
    }

    @Test
    void nonEmptyTryWrapVectorFailure() {
        assertEquals(nothing(), NonEmptyVector.tryWrap(Vector.empty()));
    }

    @Test
    void nonEmptyWrapOrThrowArraySuccess() {
        NonEmptyVector<String> result = NonEmptyVector.wrapOrThrow(new String[]{"foo"});
        assertThat(result, contains("foo"));
        assertEquals("foo", result.head());
        assertEquals(1, result.size());
    }

    @Test
    void nonEmptyWrapOrThrowListSuccess() {
        NonEmptyVector<String> result = NonEmptyVector.wrapOrThrow(singletonList("foo"));
        assertEquals("foo", result.head());
        assertEquals(1, result.size());
    }

    @Test
    void nonEmptyWrapOrThrowVectorSuccess() {
        NonEmptyVector<String> result = NonEmptyVector.wrapOrThrow(Vector.of("foo"));
        assertThat(result, contains("foo"));
        assertEquals("foo", result.head());
        assertEquals(1, result.size());
    }

    @Test
    void nonEmptyWrapOrThrowArrayFailure() {
        assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.wrapOrThrow(new String[]{}));
    }

    @Test
    void nonEmptyWrapOrThrowListFailure() {
        assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.wrapOrThrow(emptyList()));
    }

    @Test
    void nonEmptyWrapOrThrowVectorFailure() {
        assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.wrapOrThrow(Vector.empty()));
    }
}
