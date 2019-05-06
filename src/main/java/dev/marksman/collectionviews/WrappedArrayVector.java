package dev.marksman.collectionviews;

import java.util.Arrays;

final class WrappedArrayVector<A> extends ConcreteVector<A> implements NonEmptyVector<A> {
    /**
     * underlying must contain at least one element
     */
    private final A[] underlying;

    WrappedArrayVector(A[] underlying) {
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
        if (index < 0 || index >= underlying.length) {
            throw new IndexOutOfBoundsException();
        }
        return underlying[index];
    }

    @Override
    public ImmutableNonEmptyVector<A> toImmutable() {
        A[] copied = Arrays.copyOf(underlying, underlying.length);
        return new ImmutableArrayVector<>(copied);
    }

}
