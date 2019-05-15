package dev.marksman.collectionviews;

import java.util.List;

final class ImmutableListVector<A> extends ConcreteVector<A>
        implements ImmutableNonEmptyVector<A>, Primitive {
    /**
     * underlying must contain at least one element.
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
    public int size() {
        return underlying.size();
    }

    @Override
    public A unsafeGet(int index) {
        return underlying.get(index);
    }

}
