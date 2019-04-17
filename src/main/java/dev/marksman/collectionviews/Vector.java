package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;

public interface Vector<A> extends Iterable<A>, RandomAccess {
    int size();

    A unsafeGet(int index);

    default boolean isEmpty() {
        return size() == 0;
    }

    default Vector<A> tail() {
        return drop(1, this);
    }

    default Maybe<A> get(int index) {
        if (index >= 0 && index < size()) {
            return just(unsafeGet(index));
        } else {
            return nothing();
        }
    }

    @Override
    default Iterator<A> iterator() {
        return new VectorIterator<>(this);
    }

    static <A> Vector<A> empty() {
        return Vectors.empty();
    }

    static <A> Vector<A> wrap(A[] arr) {
        return Vectors.wrap(arr);
    }

    static <A> Vector<A> wrap(List<A> list) {
        return Vectors.wrap(list);
    }

    static <A> Vector<A> take(int count, Iterable<A> source) {
        return Vectors.take(count, source);
    }

    static <A> Vector<A> drop(int count, Vector<A> source) {
        return Vectors.drop(count, source);
    }

    static <A> Vector<A> slice(int startIndex, int endIndexExclusive, Iterable<A> source) {
        return Vectors.slice(startIndex, endIndexExclusive, source);
    }

    @SafeVarargs
    static <A> NonEmptyVector<A> of(A first, A... more) {
        return Vectors.nonEmptyWrap(first, more);
    }
}
