package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

class RepeatingVector<A> extends ConcreteVector<A> implements ImmutableNonEmptyVector<A> {
    private final int size; // must be >= 1
    private final A value;

    RepeatingVector(int size, A value) {
        this.size = size;
        this.value = value;
    }

    @Override
    public A head() {
        return value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public A unsafeGet(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return value;
    }

    @Override
    public <B> ImmutableNonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return new RepeatingVector<>(size, f.apply(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o instanceof RepeatingVector<?>) {
            RepeatingVector<Object> other = (RepeatingVector<Object>) o;
            return other.size == size &&
                    (value == null
                            ? other.value == null
                            : value.equals(other.value));
        } else {
            return super.equals(o);
        }

    }

}
