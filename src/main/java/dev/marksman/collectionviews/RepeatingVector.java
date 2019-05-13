package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;

import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static dev.marksman.collectionviews.Validation.*;

final class RepeatingVector<A> extends ConcreteVector<A>
        implements ImmutableNonEmptyVector<A>, Primitive {
    private final int size; // must be >= 1
    private final A value;

    RepeatingVector(int size, A value) {
        assert (size >= 1);
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
    public Maybe<A> find(Fn1<? super A, ? extends Boolean> predicate) {
        Objects.requireNonNull(predicate);
        if (predicate.apply(value)) {
            return just(value);
        } else {
            return nothing();
        }
    }

    @Override
    public Maybe<Integer> findIndex(Fn1<? super A, ? extends Boolean> predicate) {
        Objects.requireNonNull(predicate);
        if (predicate.apply(value)) {
            return just(0);
        } else {
            return nothing();
        }
    }

    @Override
    public <B> ImmutableNonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        Objects.requireNonNull(f);
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
        validateDrop(count);
        if (count == 0) {
            return this;
        } else if (count < size) {
            return new RepeatingVector<>(size - count, value);
        } else {
            return Vectors.empty();
        }
    }

    @Override
    public ImmutableVector<A> dropRight(int count) {
        return drop(count);
    }

    @Override
    public ImmutableVector<A> dropWhile(Fn1<? super A, ? extends Boolean> predicate) {
        Objects.requireNonNull(predicate);
        if (predicate.apply(value)) {
            return Vectors.empty();
        } else {
            return this;
        }
    }

    @Override
    public ImmutableNonEmptyVector<A> reverse() {
        return this;
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
        validateTake(count);
        if (count == 0) {
            return Vectors.empty();
        } else if (count >= size) {
            return this;
        } else {
            return new RepeatingVector<>(count, value);
        }
    }

    @Override
    public ImmutableVector<A> takeRight(int count) {
        return take(count);
    }

    @Override
    public ImmutableVector<A> takeWhile(Fn1<? super A, ? extends Boolean> predicate) {
        Objects.requireNonNull(predicate);
        if (predicate.apply(value)) {
            return this;
        } else {
            return Vectors.empty();
        }
    }

}
