package dev.marksman.collectionviews.concrete;

import dev.marksman.collectionviews.NonEmptyVector;

import java.util.List;

class VectorWrappedList<A> implements NonEmptyVector<A> {
    private final List<A> underlying;

    VectorWrappedList(List<A> underlying) {
        this.underlying = underlying;
    }

    @Override
    public A head() {
        return underlying.get(0);
    }

    @Override
    public int size() {
        return underlying.size();
    }

    @Override
    public A unsafeGet(int index) {
        return underlying.get(index);
    }
}
