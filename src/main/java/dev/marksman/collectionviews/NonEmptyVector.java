package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;

import java.util.Iterator;
import java.util.List;

public interface NonEmptyVector<A> extends NonEmptyIterable<A>, Vector<A> {

    @Override
    default boolean isEmpty() {
        return false;
    }

    @Override
    default Vector<A> tail() {
        return Vector.drop(1, this);
    }

    @Override
    default Iterator<A> iterator() {
        return new VectorIterator<>(this);
    }

    static <A> NonEmptyVector<A> wrap(A first, A[] more) {
        return Vectors.nonEmptyWrap(first, more);
    }

    static <A> NonEmptyVector<A> wrap(A first, List<A> more) {
        return Vectors.nonEmptyWrap(first, more);
    }

    static <A> NonEmptyVector<A> wrap(A first, Vector<A> more) {
        return Vectors.nonEmptyWrap(first, more);
    }

    static <A> Maybe<NonEmptyVector<A>> tryWrap(A[] arr) {
        return Vectors.tryNonEmptyWrap(arr);
    }

    static <A> Maybe<NonEmptyVector<A>> tryWrap(List<A> list) {
        return Vectors.tryNonEmptyWrap(list);
    }

    static <A> Maybe<NonEmptyVector<A>> tryWrap(Vector<A> vec) {
        return Vectors.tryNonEmptyWrap(vec);
    }
}
