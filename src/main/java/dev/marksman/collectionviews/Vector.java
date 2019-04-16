package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import dev.marksman.collectionviews.concrete.vectors;

import java.util.Iterator;
import java.util.List;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;

public interface Vector<A> extends Iterable<A> {
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
        return vectors.empty();
    }

    static <A> Vector<A> wrap(A[] arr) {
        return vectors.wrap(arr);
    }

    static <A> Vector<A> wrap(List<A> list) {
        return vectors.wrap(list);
    }

    static <A> Vector<A> take(int count, Iterable<A> source) {
        return vectors.take(count, source);
    }

    static <A> Vector<A> drop(int count, Vector<A> source) {
        return vectors.drop(count, source);
    }

    static <A> Vector<A> slice(int startIndex, int endIndexExclusive, Iterable<A> source) {
        return vectors.slice(startIndex, endIndexExclusive, source);
    }

    static <A> NonEmptyVector<A> of(A first, A... more) {
        return NonEmptyVector.wrap(first, more);
    }
}
