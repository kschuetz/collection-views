package dev.marksman.collectionviews;

class ImmutableReverseVector<A> extends ConcreteVector<A> implements ImmutableNonEmptyVector<A> {
    private final ImmutableNonEmptyVector<A> underlying;

    private ImmutableReverseVector(ImmutableNonEmptyVector<A> underlying) {
        this.underlying = underlying;
    }

    @Override
    public A head() {
        return unsafeGet(0);
    }

    @Override
    public int size() {
        return underlying.size();
    }

    @Override
    public A unsafeGet(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return underlying.unsafeGet(size() - 1 - index);
    }

    static <A> ImmutableNonEmptyVector<A> immutableReverseVector(ImmutableNonEmptyVector<A> underlying) {
        if (underlying.size() < 2) {
            return underlying;
        } else if (underlying instanceof ImmutableReverseVector<?>) {
            return ((ImmutableReverseVector<A>) underlying).underlying;
        } else {
            return new ImmutableReverseVector<>(underlying);
        }
    }

}
