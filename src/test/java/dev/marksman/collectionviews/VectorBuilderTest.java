package dev.marksman.collectionviews;

import org.junit.jupiter.api.Test;

import static dev.marksman.collectionviews.EmptyVector.emptyVector;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class VectorBuilderTest {

    @Test
    void emptyBuilderYieldsEmptyVector() {
        VectorBuilder<String> builder = Vector.builder();
        assertSame(emptyVector(), builder.build());
    }

    @Test
    void add() {
        assertEquals(Vector.of("foo"), Vector.<String>builder().add("foo").build());
    }

    @Test
    void addTwice() {
        assertEquals(Vector.of("foo", "bar"), Vector.<String>builder().add("foo").add("bar").build());
    }

    @Test
    void addAllList() {
        assertEquals(Vector.of("foo", "bar", "baz"),
                Vector.<String>builder().addAll(asList("foo", "bar", "baz")).build());
    }

    @Test
    void addAllVector() {
        Vector<String> vector = Vector.of("foo", "bar", "baz");
        assertEquals(vector, Vector.<String>builder().addAll(vector).build());
    }

    @Test
    void addAllNonEmptyVector() {
        NonEmptyVector<String> vector = Vector.of("foo", "bar", "baz");
        assertEquals(vector, Vector.<String>builder().addAll(vector).build());
    }

    @Test
    void canBeReused() {
        VectorBuilder<Integer> builder = Vector.builder();
        NonEmptyVectorBuilder<Integer> b1 = builder.add(1);
        assertEquals(Vector.of(1), b1.build());
        NonEmptyVectorBuilder<Integer> b2 = b1.addAll(asList(2, 3, 4));
        assertEquals(Vector.of(1), b1.build());
        assertEquals(Vector.of(1, 2, 3, 4), b2.build());
        NonEmptyVectorBuilder<Integer> b3 = b1.add(5);
        assertEquals(Vector.of(1, 5), b3.build());
    }

    @Test
    void withInitialCapacity() {
        VectorBuilder<Integer> builder = Vector.builder(100);
        NonEmptyVectorBuilder<Integer> b1 = builder.add(1);
        assertEquals(Vector.of(1), b1.build());
        NonEmptyVectorBuilder<Integer> b2 = b1.addAll(asList(2, 3, 4));
        assertEquals(Vector.of(1), b1.build());
        assertEquals(Vector.of(1, 2, 3, 4), b2.build());
        NonEmptyVectorBuilder<Integer> b3 = b1.add(5);
        assertEquals(Vector.of(1, 5), b3.build());
    }

}
