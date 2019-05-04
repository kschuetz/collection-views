package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

class LazyVector<A> extends ConcreteVector<A> implements ImmutableNonEmptyVector<A> {
    private final int size; // must be >= 1
    private final IndexChain indexChain;
    private final Fn1<Integer, A> valueSupplier;

    LazyVector(int size, Fn1<Integer, A> valueSupplier) {
        this.size = size;
        this.valueSupplier = valueSupplier;
        this.indexChain = IndexChain.identity();
    }

    private LazyVector(int size, Fn1<Integer, A> valueSupplier, IndexChain indexChain) {
        this.size = size;
        this.valueSupplier = valueSupplier;
        this.indexChain = indexChain;
    }

    @Override
    public A head() {
        return valueSupplier.apply(indexChain.apply(0));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public A unsafeGet(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return valueSupplier.apply(indexChain.apply(index));
    }

    @Override
    public ImmutableVector<A> drop(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must be >= 0");
        }
        if (count == 0) {
            return this;
        } else if (count < size) {
            return new LazyVector<>(size - count, valueSupplier, indexChain.contraMap(n -> count + n));
        } else {
            return Vectors.empty();
        }
    }

    @Override
    public ImmutableVector<A> slice(int startIndex, int endIndexExclusive) {
        if (startIndex < 0) {
            throw new IllegalArgumentException("startIndex must be >= 0");
        }
        if (endIndexExclusive < 0) {
            throw new IllegalArgumentException("endIndex must be >= 0");
        }
        if (startIndex == 0) {
            return take(endIndexExclusive);
        }
        endIndexExclusive = Math.min(endIndexExclusive, size);
        if (endIndexExclusive >= startIndex) {
            return new LazyVector<>(endIndexExclusive - startIndex, valueSupplier, indexChain.contraMap(n -> startIndex + n));
        } else {
            return Vector.empty();
        }
    }

    @Override
    public ImmutableVector<A> take(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must be >= 0");
        }
        if (count == 0) {
            return Vectors.empty();
        } else if (count >= size) {
            return this;
        } else {
            return new LazyVector<>(count, valueSupplier, indexChain);
        }
    }

}
