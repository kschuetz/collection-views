package dev.marksman.collectionviews;

import org.junit.jupiter.api.Test;

import static dev.marksman.collectionviews.EmptySet.emptySet;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class SetBuilderTest {

    @Test
    void emptyBuilderYieldsEmptySet() {
        SetBuilder<String> builder = Set.builder();
        assertSame(emptySet(), builder.build());
    }

    @Test
    void add() {
        assertEquals(Set.of("foo", "foo"), Set.<String>builder().add("foo").build());
    }

    @Test
    void addTwice() {
        assertEquals(Set.of("foo", "foo", "bar"), Set.<String>builder().add("foo").add("bar").build());
    }

    @Test
    void addAllList() {
        assertEquals(Set.of("foo", "foo", "bar", "baz"),
                Set.<String>builder().addAll(asList("foo", "bar", "baz")).build());
    }

    @Test
    void addAllSet() {
        Set<String> set = Set.of("foo", "foo", "bar", "baz");
        assertEquals(set, Set.<String>builder().addAll(set).build());
    }

    @Test
    void addAllNonEmptySet() {
        NonEmptySet<String> set = Set.of("foo", "foo", "bar", "baz");
        assertEquals(set, Set.<String>builder().addAll(set).build());
    }

    @Test
    void canBeReused() {
        SetBuilder<Integer> builder = Set.builder();
        NonEmptySetBuilder<Integer> b1 = builder.add(1);
        assertEquals(Set.of(1), b1.build());
        NonEmptySetBuilder<Integer> b2 = b1.addAll(asList(2, 3, 4, 4));
        assertEquals(Set.of(1), b1.build());
        assertEquals(Set.of(1, 2, 3, 4, 4), b2.build());
        NonEmptySetBuilder<Integer> b3 = b1.add(5);
        assertEquals(Set.of(1, 5), b3.build());
    }

    @Test
    void withInitialCapacity() {
        SetBuilder<Integer> builder = Set.builder(100);
        NonEmptySetBuilder<Integer> b1 = builder.add(1);
        assertEquals(Set.of(1), b1.build());
        NonEmptySetBuilder<Integer> b2 = b1.addAll(asList(2, 3, 4, 4));
        assertEquals(Set.of(1), b1.build());
        assertEquals(Set.of(1, 2, 3, 4, 4), b2.build());
        NonEmptySetBuilder<Integer> b3 = b1.add(5);
        assertEquals(Set.of(1, 5), b3.build());
    }

}
