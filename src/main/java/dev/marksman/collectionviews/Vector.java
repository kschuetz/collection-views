package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import static com.jnape.palatable.lambda.adt.Maybe.maybe;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;

public interface Vector<A> extends Iterable<A>, RandomAccess {
    int size();

    A unsafeGet(int index);

    default boolean isEmpty() {
        return size() == 0;
    }

    default Vector<A> tail() {
        return drop(1);
    }

    default Maybe<A> get(int index) {
        if (index >= 0 && index < size()) {
            return maybe(unsafeGet(index));
        } else {
            return nothing();
        }
    }

    @Override
    default Iterator<A> iterator() {
        return new VectorIterator<>(this);
    }

    default Vector<A> take(int count) {
        return Vectors.take(count, this);
    }

    default Vector<A> drop(int count) {
        return Vectors.drop(count, this);
    }

    default Vector<A> slice(int startIndex, int endIndexExclusive) {
        return Vectors.sliceFromIterable(startIndex, endIndexExclusive, this);
    }

    static <A> Vector<A> empty() {
        return Vectors.empty();
    }

    static <A> Vector<A> wrap(A[] underlying) {
        return Vectors.wrap(underlying);
    }

    static <A> Vector<A> wrap(List<A> underlying) {
        return Vectors.wrap(underlying);
    }

    static <A> Vector<A> takeFromIterable(int count, Iterable<A> source) {
        return Vectors.takeFromIterable(count, source);
    }

    static <A> Vector<A> sliceFromIterable(int startIndex, int endIndexExclusive, Iterable<A> source) {
        return Vectors.sliceFromIterable(startIndex, endIndexExclusive, source);
    }

    @SafeVarargs
    static <A> NonEmptyVector<A> of(A first, A... more) {
        return Vectors.nonEmptyWrap(first, more);
    }
}
