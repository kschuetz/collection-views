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
        return drop(1);
    }

    @Override
    default ImmutableNonEmptyVector<A> ensureImmutable() {
        return Vectors.ensureImmutable(this);
    }

    @Override
    default Iterator<A> iterator() {
        return new VectorIterator<>(this);
    }

    static <A> Maybe<NonEmptyVector<A>> tryWrap(A[] arr) {
        return Vectors.tryNonEmptyWrap(arr);
    }

    static <A> Maybe<NonEmptyVector<A>> tryWrap(List<A> list) {
        return Vectors.tryNonEmptyWrap(list);
    }

    /**
     * Converts a Vector to a NonEmptyVector if it is not empty.
     * Does not copy underlying data.
     *
     * @param vec
     * @param <A>
     * @return a NonEmptyVector wrapped in Maybe.just if the input is non-empty,
     * otherwise Maybe.nothing
     */
    static <A> Maybe<NonEmptyVector<A>> tryWrap(Vector<A> vec) {
        return Vectors.tryNonEmptyWrap(vec);
    }


    /**
     * Wraps an array in a NonEmptyVector if it is not empty, otherwise throws.
     * <p>
     * Useful if you already know that the array is not empty.
     * <p>
     * Does not copy underlying data.
     *
     * @param arr an array of 1 or more elements.  Throws IllegalArgumentException otherwise.
     * @param <A>
     * @return
     */
    static <A> NonEmptyVector<A> wrapOrThrow(A[] arr) {
        return Vectors.nonEmptyWrapOrThrow(arr);
    }

    static <A> NonEmptyVector<A> wrapOrThrow(List<A> list) {
        return Vectors.nonEmptyWrapOrThrow(list);
    }

    static <A> NonEmptyVector<A> wrapOrThrow(Vector<A> vec) {
        return Vectors.nonEmptyWrapOrThrow(vec);
    }

    @SafeVarargs
    static <A> NonEmptyVector<A> of(A first, A... more) {
        return Vectors.of(first, more);
    }
}
