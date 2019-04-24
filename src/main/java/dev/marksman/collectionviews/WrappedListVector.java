package dev.marksman.collectionviews;

import java.util.ArrayList;
import java.util.List;

import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;

class WrappedListVector<A> implements NonEmptyVector<A> {
    /**
     * underlying must contain at least one element
     */
    private final List<A> underlying;

    WrappedListVector(List<A> underlying) {
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

    @Override
    public ImmutableNonEmptyVector<A> ensureImmutable() {
        ArrayList<A> copied = toCollection(ArrayList::new, underlying);
        return new ImmutableListVector<>(copied);
    }
}
