package dev.marksman.collectionviews;

import java.util.List;

class ProtectedListVector<A> implements ProtectedNonEmptyVector<A> {
    /**
     * underlying must contain at least one element
     */
    private final List<A> underlying;

    ProtectedListVector(List<A> underlying) {
        this.underlying = underlying;
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

}
