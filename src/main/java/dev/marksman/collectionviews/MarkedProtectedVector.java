package dev.marksman.collectionviews;

class MarkedProtectedVector<A> implements ProtectedNonEmptyVector<A> {
    private final NonEmptyVector<A> underlying;

    MarkedProtectedVector(NonEmptyVector<A> underlying) {
        this.underlying = underlying;
    }

    @Override
    public A head() {
        return underlying.head();
    }

    @Override
    public int size() {
        return underlying.size();
    }

    @Override
    public A unsafeGet(int index) {
        return underlying.unsafeGet(index);
    }
}
