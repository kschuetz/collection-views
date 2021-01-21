package dev.marksman.collectionviews;

import java.util.Iterator;

final class ImmutableVectorSlidingIterator<A> implements Iterator<ImmutableNonEmptyVector<A>> {
    private final ImmutableVector<A> source;
    private final int windowSize;
    private final int maxIndex;
    private int index;

    ImmutableVectorSlidingIterator(ImmutableVector<A> source, int windowSize, int maxIndex) {
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
    public ImmutableNonEmptyVector<A> next() {
        ImmutableNonEmptyVector<A> result = source.slice(index, index + windowSize).toNonEmptyOrThrow();
        index += 1;
        return result;
    }
}
