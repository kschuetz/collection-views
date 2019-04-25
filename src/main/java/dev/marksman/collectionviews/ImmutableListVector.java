package dev.marksman.collectionviews;

import java.util.List;

class ImmutableListVector<A> extends AbstractVector<A> implements ImmutableNonEmptyVector<A> {
    /**
     * underlying must contain at least one element
     */
    private final List<A> underlying;

    ImmutableListVector(List<A> underlying) {
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
