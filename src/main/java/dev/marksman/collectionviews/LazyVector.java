package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

class LazyVector<A> extends ConcreteVector<A> implements ImmutableNonEmptyVector<A> {
    private final int size; // must be >= 1
    private final Fn1<Integer, A> valueSupplier;

    LazyVector(int size, Fn1<Integer, A> valueSupplier) {
        this.size = size;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public A head() {
        return valueSupplier.apply(0);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public A unsafeGet(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return valueSupplier.apply(index);
    }

}
