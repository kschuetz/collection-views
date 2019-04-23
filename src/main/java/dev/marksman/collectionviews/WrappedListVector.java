package dev.marksman.collectionviews;

import java.util.List;

class WrappedListVector<A> implements NonEmptyVector<A> {
    /**
     * underlying must contain at least one element
     */
    private final List<A> underlying;
    private final boolean ownsAllReferences;

    WrappedListVector(List<A> underlying, boolean ownsAllReferences) {
        this.underlying = underlying;
        this.ownsAllReferences = ownsAllReferences;
    }

    @Override
    public boolean isEmpty() {
        return false;
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

    @Override
    public boolean ownsAllReferencesToUnderlying() {
        return ownsAllReferences;
    }
}
