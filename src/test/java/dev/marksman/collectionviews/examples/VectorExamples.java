package dev.marksman.collectionviews.examples;

import dev.marksman.collectionviews.ImmutableNonEmptyVector;
import dev.marksman.collectionviews.Vector;

public class VectorExamples {
    public static void main(String[] args) {
        ImmutableNonEmptyVector<Integer> vec1 = Vector.of(1, 2, 3, 4);
        System.out.println("vec1 = " + vec1);
    }
}
