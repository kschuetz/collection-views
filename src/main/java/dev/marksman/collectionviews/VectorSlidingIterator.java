package dev.marksman.collectionviews;

import java.util.Iterator;

final class VectorSlidingIterator<A> implements Iterator<NonEmptyVector<A>> {
    private final Vector<A> source;
    private final int windowSize;
    private final int maxIndex;
    private int index;

    VectorSlidingIterator(Vector<A> source, int windowSize, int maxIndex) {
        this.source = source;
        this.windowSize = windowSize;
        this.maxIndex = maxIndex;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < maxIndex;
    }

    @Override
    public NonEmptyVector<A> next() {
        NonEmptyVector<A> result = source.slice(index, index + windowSize).toNonEmptyOrThrow();
        index += 1;
        return result;
    }
}
