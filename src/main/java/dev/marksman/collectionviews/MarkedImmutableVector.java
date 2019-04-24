package dev.marksman.collectionviews;

class MarkedImmutableVector<A> implements ImmutableNonEmptyVector<A> {
    private final NonEmptyVector<A> underlying;

    MarkedImmutableVector(NonEmptyVector<A> underlying) {
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
