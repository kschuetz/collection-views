package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

import java.util.Objects;

import static dev.marksman.collectionviews.Validation.*;

final class RepeatingVector<A> extends ConcreteVector<A> implements ImmutableNonEmptyVector<A> {
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
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return value;
    }

    @Override
    public <B> ImmutableNonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return new RepeatingVector<>(size, f.apply(value));
    }

    @Override
    public int hashCode() {
        final int h = value == null ? 0 : value.hashCode();
        int hashCode = 1;
        for (int i = 0; i < size; i++) {
            hashCode = 31 * hashCode + h;
        }
        return hashCode;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o instanceof RepeatingVector<?>) {
            RepeatingVector<Object> other = (RepeatingVector<Object>) o;
            return other.size == size
                    && (Objects.equals(value, other.value));
        } else {
            return super.equals(o);
        }

    }

    @Override
    public ImmutableVector<A> drop(int count) {
        validateTake(count);
        if (count == 0) {
            return this;
        } else if (count < size) {
            return new RepeatingVector<>(size - count, value);
        } else {
            return Vectors.empty();
        }
    }

    @Override
    public ImmutableVector<A> slice(int startIndex, int endIndexExclusive) {
        validateSlice(startIndex, endIndexExclusive);
        endIndexExclusive = Math.min(endIndexExclusive, size);
        if (endIndexExclusive >= startIndex) {
            return take(endIndexExclusive - startIndex);
        } else {
            return Vector.empty();
        }
    }

    @Override
    public ImmutableVector<A> take(int count) {
        validateDrop(count);
        if (count == 0) {
            return Vectors.empty();
        } else if (count >= size) {
            return this;
        } else {
            return new RepeatingVector<>(count, value);
        }
    }

}
