package dev.marksman.collectionviews;

class WrappedArrayVector<A> implements NonEmptyVector<A> {
    /**
     * underlying must contain at least one element
     */
    private final A[] underlying;
    private final boolean ownsAllReferences;

    WrappedArrayVector(A[] underlying, boolean ownsAllReferences) {
        this.underlying = underlying;
        this.ownsAllReferences = ownsAllReferences;
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

    @Override
    public boolean ownsAllReferencesToUnderlying() {
        return ownsAllReferences;
    }
}
