package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

import static dev.marksman.collectionviews.Validation.*;

final class LazyVector<A> extends ConcreteVector<A>
        implements ImmutableNonEmptyVector<A>, Primitive {
    private final int size; // must be >= 1
    private final int offset;
    private final Fn1<Integer, A> valueSupplier;

    LazyVector(int size, int offset, Fn1<Integer, A> valueSupplier) {
        assert (size >= 1);
        this.size = size;
        this.offset = offset;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public A head() {
        return valueSupplier.apply(offset);
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
        return valueSupplier.apply(index + offset);
    }

    @Override
    public ImmutableVector<A> drop(int count) {
        validateDrop(count);
        if (count == 0) {
            return this;
        } else if (count < size) {
            return new LazyVector<>(size - count, offset + count, valueSupplier);
        } else {
            return Vectors.empty();
        }
    }

    @Override
    public ImmutableVector<A> slice(int startIndex, int endIndexExclusive) {
        validateSlice(startIndex, endIndexExclusive);
        if (startIndex == 0) {
            return take(endIndexExclusive);
        }
        endIndexExclusive = Math.min(endIndexExclusive, size);
        if (endIndexExclusive >= startIndex) {
            return new LazyVector<>(endIndexExclusive - startIndex, offset + startIndex, valueSupplier);
        } else {
            return Vector.empty();
        }
    }

    @Override
    public ImmutableVector<A> take(int count) {
        validateTake(count);
        if (count == 0) {
            return Vectors.empty();
        } else if (count >= size) {
            return this;
        } else {
            return new LazyVector<>(count, offset, valueSupplier);
        }
    }

}
