package dev.marksman.collectionviews;

class ImmutableArrayVector<A> extends AbstractVector<A> implements ImmutableNonEmptyVector<A> {
    /**
     * underlying must contain at least one element
     */
    private final A[] underlying;

    ImmutableArrayVector(A[] underlying) {
        this.underlying = underlying;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public A head() {
        return underlying[0];
    }

    @Override
    public int size() {
        return underlying.length;
    }

    @Override
    public A unsafeGet(int index) {
        if (index < 0 || index >= underlying.length) throw new IndexOutOfBoundsException();
        return underlying[index];
    }

}
