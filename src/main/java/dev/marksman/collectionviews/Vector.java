package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import static com.jnape.palatable.lambda.adt.Maybe.maybe;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;

public interface Vector<A> extends Iterable<A>, RandomAccess {
    /**
     * The size of the Vector.  Executes in O(1).
     *
     * @return the number of elements in the Vector.
     */
    int size();

    /**
     * Get an element from the Vector at an index.  Executes in O(1).
     *
     * @param index the index of the element to retrieve.  Must be between 0 and size - 1, otherwise will
     *              throw an IndexOutOfBoundsException.
     * @return the element at index.  May be null, if the underlying data contains a null at that index.
     */
    A unsafeGet(int index);

    /**
     * Tests whether the Vector is empty.  Executes in O(1).
     *
     * @return true if the Vector is empty, false otherwise.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the tail of the Vector, i.e. the same Vector with the first element dropped.
     * May be empty.
     *
     * @return
     */
    default Vector<A> tail() {
        return drop(1);
    }

    /**
     * Get an element from the Vector at an index.  Executes in O(1).
     * Will never return null.
     *
     * @param index
     * @return
     */
    default Maybe<A> get(int index) {
        if (index >= 0 && index < size()) {
            return maybe(unsafeGet(index));
        } else {
            return nothing();
        }
    }

    /**
     * If it is known that this Vector fully contains its underlying collection
     * and it is safe from mutation elsewhere, this method returns true.
     * <p>
     * If this is not known, this method returns false.
     * <p>
     * Used as an optimization when copying.
     */
    default boolean ownsAllReferencesToUnderlying() {
        return false;
    }

    default ProtectedVector<A> ensureProtected() {
        return Vectors.ensureProtected(this);
    }

    @Override
    default Iterator<A> iterator() {
        return new VectorIterator<>(this);
    }

    /**
     * Returns a new Vector that at most the first count elements.
     * <p>
     * Does *not* make a copy of the underlying data.
     * <p>
     * Use caution when taking a small slice of a huge Vector, as the smaller slice
     * will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     *
     * @param count the maximum number of elements to take from the Vector.  Must be >= 0.
     *              May exceed size of Vector.
     * @return
     */
    default Vector<A> take(int count) {
        return Vectors.take(count, this);
    }

    /**
     * Returns a new Vector that drops the first count elements.
     * Does *not* make a copy of the underlying data.
     * <p>
     * Use caution when taking a small slice of a huge Vector, as the smaller slice
     * will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     *
     * @param count the number of elements to drop from the Vector.  Must be >= 0.
     *              May exceed size of Vector, in which case, the result will be an
     *              empty Vector.
     * @return
     */
    default Vector<A> drop(int count) {
        return Vectors.drop(count, this);
    }

    /**
     * Create a slice of an existing Vector.
     * Does *not* make a copy of the underlying data.
     * <p>
     * Use caution when taking a small slice of a huge Vector, as the smaller slice
     * will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     *
     * @param startIndex        the index of the element to begin the slice.  Must be >= 0.
     *                          May exceed the size of the Vector, in which case an empty Vector will be returned.
     * @param endIndexExclusive the end index (exclusive) of the slice.  Must be >= startIndex.
     *                          May exceed the size of the Vector, in which case the slice will
     *                          contain as many elements as available.
     * @return
     */
    default Vector<A> slice(int startIndex, int endIndexExclusive) {
        return Vectors.sliceFromIterable(startIndex, endIndexExclusive, this);
    }

    /**
     * Returns an empty {@link Vector}.
     *
     * @param <A>
     * @return
     */
    static <A> ProtectedVector<A> empty() {
        return Vectors.empty();
    }

    /**
     * Creates a {@link Vector} that wraps an array.
     * <p>
     * Does *not* make a copy of the given array.
     * The Vector will hold a reference to the given array, but will not alter it in any way.
     * <p>
     * Since bearers of this Vector will be unable to mutate or gain access to the underlying array,
     * it is safe to share.
     * <p>
     * Since this does not make a copy of the array, be aware that anyone that holds a direct reference to
     * the array can still mutate it.
     *
     * @param underlying array to wrap
     * @param <A>
     * @return
     */
    static <A> Vector<A> wrap(A[] underlying) {
        return Vectors.wrap(underlying);
    }

    /**
     * Creates a {@link Vector} that wraps a {@link java.util.List}.
     * <p>
     * Does *not* make a copy of the underlying list or alter it in any way.
     * Will hold a reference to the given List.
     * <p>
     * Since bearers of this Vector will be unable to mutate or gain access to the underlying List,
     * it is safe to share.
     * <p>
     * Since this does not make a copy of the List, be aware that anyone that holds a direct reference to
     * the List can still mutate it.  Mutating the List is not advised.  Operations that change the size
     * of the underlying List will result in unpredictabe behavior.
     *
     * @param underlying {@link List} to wrap
     * @param <A>
     * @return
     */
    static <A> Vector<A> wrap(List<A> underlying) {
        return Vectors.wrap(underlying);
    }

    /**
     * Builds a Vector from an {@link Iterable}.
     * <p>
     * Will only make a copy of the data if necessary.
     * If source is a {@link java.util.List} or another {@link Vector},
     * the data won't be copied.
     *
     * @param count  maximum number of elements to take from source.  Must be >= 0.
     *               May exceed the number of elements in source, in which case,
     *               all of the elements available will be taken.
     * @param source any {@link Iterable}, even those that are potentially infinite
     * @param <A>
     * @return
     */
    static <A> Vector<A> takeFromIterable(int count, Iterable<A> source) {
        return Vectors.takeFromIterable(count, source);
    }

    /**
     * Builds a Vector from an {@link Iterable}.
     * <p>
     * Will only make a copy of the data if necessary.
     * If source is a {@link java.util.List} or another Vector,
     * the data won't be copied.
     *
     * @param startIndex
     * @param endIndexExclusive
     * @param source
     * @param <A>
     * @return
     */
    static <A> Vector<A> sliceFromIterable(int startIndex, int endIndexExclusive, Iterable<A> source) {
        return Vectors.sliceFromIterable(startIndex, endIndexExclusive, source);
    }

    /**
     * Constructs a new Vector with the given elements.
     *
     * @param first the first element
     * @param more  the remaining elements
     * @param <A>
     * @return a NonEmptyVector that is guaranteed to be safe from mutation.
     */
    @SafeVarargs
    static <A> NonEmptyVector<A> of(A first, A... more) {
        return Vectors.of(first, more);
    }
}
