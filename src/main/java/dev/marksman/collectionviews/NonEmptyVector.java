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
    default NonEmptyVector<A> ensureImmutable() {
        return Vectors.ensureImmutable(this);
    }

    @Override
    default Iterator<A> iterator() {
        return new VectorIterator<>(this);
    }

    /**
     * Wraps an array in a NonEmptyVector without making a copy of the array.
     * <p>
     * Requires an additional element at the front to guarantee non-emptiness.
     * The resulting Vector will be of size 1 + more.length.
     * <p>
     * If you don't have an additional element, use tryNonEmptyWrap.
     * If you have an array that you know is not empty, use wrapOrThrow.
     *
     * @param first
     * @param more
     * @param <A>
     * @return
     */
    static <A> NonEmptyVector<A> wrap(A first, A[] more) {
        return Vectors.nonEmptyWrap(first, more);
    }

    static <A> NonEmptyVector<A> wrap(A first, List<A> more) {
        return Vectors.nonEmptyWrap(first, more);
    }

    /**
     * Creates a NonEmptyVector from a Vector, and an additional element at the
     * front to guarantee non-emptiness.
     * <p>
     * While it may be tempting to use this method as a way to cons items onto existing Vectors,
     * beware:  doing this multiple times will remove the O(1) guarantees of
     * size, unsafeGet, and get. (These will become O(n), violating the contract of Vector).
     *
     * @param first
     * @param more
     * @param <A>
     * @return
     */
    static <A> NonEmptyVector<A> wrap(A first, Vector<A> more) {
        return Vectors.nonEmptyWrap(first, more);
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
