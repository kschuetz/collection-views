package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;

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
     * <p>
     * Does not make copies of any underlying data structures.
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
     * Maps a function over the elements in a Vector and returns a new Vector
     * of the same size (but possibly a different type).
     * <p>
     * Does not make any copies of underlying data structures.
     * <p>
     * This method is stack-safe, so a Vector can be mapped as many times as the heap permits.
     *
     * @param f
     * @param <B> The type of the elements contained in the output Vector.
     * @return
     */
    default <B> Vector<B> fmap(Fn1<? super A, ? extends B> f) {
        return Vectors.map(f, this);
    }

    /**
     * Returns an ImmutableVector containing the same elements as this one.
     * Will make a copy of the underlying data structure if necessary to
     * guarantee immutability.
     * <p>
     * If this Vector is an ImmutableVector already, no copies are made
     * and this method is a no-op.
     *
     * @return
     */
    default ImmutableVector<A> toImmutable() {
        return Vectors.ensureImmutable(this);
    }

    /**
     * Returns a NonEmptyVector containing the same elements as this one,
     * wrapped in a Maybe.just, if this Vector is not empty.
     * <p>
     * If this Vector is empty, returns Maybe.nothing.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    default Maybe<? extends NonEmptyVector<A>> toNonEmpty() {
        return Vectors.tryNonEmptyWrap(this);
    }

    /**
     * Returns a NonEmptyVector containing the same elements as this one,
     * if this Vector is not empty.
     * <p>
     * If this Vector is empty, throws an IllegalArgumentException.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    default NonEmptyVector<A> toNonEmptyOrThrow() {
        return Vectors.nonEmptyWrapOrThrow(this);
    }

    @Override
    default Iterator<A> iterator() {
        return new VectorIterator<>(this);
    }

    /**
     * Returns a new Vector that contains at most the first count elements of this Vector.
     * <p>
     * Does not make copies of any underlying data structures.
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
     * <p>
     * Does not make copies of any underlying data structures.
     * <p>
     * Use caution when taking a small slice of a huge Vector that you no longer need, as the smaller slice
     * will hold onto a reference of the larger one, and will prevent it from being GC'ed.
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
     * <p>
     * Does not make copies of any underlying data structures.
     * <p>
     * Use caution when taking a small slice of a huge Vector that you no longer need, as the smaller slice
     * will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     * To avoid this situation, use {@link Vector#copyTakeFromIterable} instead.
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
    static <A> ImmutableVector<A> empty() {
        return Vectors.empty();
    }

    /**
     * Creates a {@link Vector} that wraps an array.
     * <p>
     * Does not make any copies of the given array.
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
     * Does not make any copies of the given List.
     * The Vector will hold a reference to the given List, but will not alter it in any way.
     * <p>
     * Since bearers of this Vector will be unable to mutate or gain access to the underlying List,
     * it is safe to share.
     * <p>
     * Since this does not make a copy of the List, be aware that anyone that holds a direct reference to
     * the List can still mutate it.  Mutating the List is not advised.  Operations that change the size
     * of the underlying List will result in unpredictable behavior.
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
     * <p>
     * If you require an ImmutableVector, see {@link Vector#copyTakeFromIterable}, which
     * will always perform a copy instead of wrapping.
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
     * Will only make a copy of the data if necessary to guarantee Vector characteristics.
     * If source is a {@link java.util.List} or another Vector,
     * the data won't be copied.
     * <p>
     * If you require an ImmutableVector, see {@link Vector#copySliceFromIterable}, which
     * will always perform a copy instead of wrapping.
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
     * @return an ImmutableNonEmptyVector, which is guaranteed to be non-empty and to be safe from mutation.
     */
    @SafeVarargs
    static <A> ImmutableNonEmptyVector<A> of(A first, A... more) {
        return Vectors.of(first, more);
    }

    /**
     * Constructs a new ImmutableVector from any {@link Iterable}.
     * <p>
     * The entire Iterable will be eagerly iterated, so be careful not
     * to pass in an infinite Iterable or this method will not terminate.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If <code>source</code> already is an ImmutableVector, it will be returned directly.
     *
     * @param source an Iterable<A> that will be iterated eagerly in its entirety
     * @param <A>
     * @return
     */
    static <A> ImmutableVector<A> copyAllFromIterable(Iterable<A> source) {
        return Vectors.copyAllFromIterable(source);
    }

    /**
     * Constructs a new ImmutableVector from any {@link Iterable}, but consuming a maximum number of elements.
     * <p>
     * The Iterable will be eagerly iterated, but only up to a maximum of <code>count</code>
     * elements.  If <code>count</code> elements are not available, then the all of the elements
     * available will be returned.
     * <p>
     * This method will make a copy of the data provided, unless <code>source</code> is
     * an ImmutableVector and its size is less than or equal to <code>count</code>,
     * in which case it will be returned directly.
     *
     * If <code>source</code> is an ImmutableVector that is greater than <code>count</code> in size,
     * a copy will always be made, therefore making it memory-safe to take a small slice of
     * a huge Vector that you no longer need.
     *
     * @param count  the maximum number of elements to consume from the source.  Must be >= 0.
     * @param source an Iterable<A> that will be iterated eagerly for up to <code>count</code> elements.
     *               It is safe for <code>source</code> to be infinite.
     * @param <A>
     * @return an ImmutableVector that contains at most <code>count</code> elements.
     */
    static <A> ImmutableVector<A> copyTakeFromIterable(int count, Iterable<A> source) {
        return Vectors.copyTakeFromIterable(count, source);
    }

    /**
     * Constructs a new ImmutableVector slice from an {@link Iterable}.
     * <p>
     * The Iterable will be eagerly iterated, but only for the number of elements
     * needed to fulfill the requested slice.
     * If not enough elements are not available, then the this method yields as many
     * elements that were available.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If <code>source</code> already is an ImmutableVector, a slice of it will be returned without making a copy.
     *
     * @param startIndex
     * @param endIndexExclusive
     * @param source            an Iterable<A> that will be iterated eagerly for up to <code>endIndexExclusive</code> elements.
     *                          It is safe for <code>source</code> to be infinite.
     * @param <A>
     * @return an ImmutableVector that contains at most <code>endIndexExclusive - startIndex</code> elements.
     */
    static <A> ImmutableVector<A> copySliceFromIterable(int startIndex, int endIndexExclusive, Iterable<A> source) {
        return Vectors.copySliceFromIterable(startIndex, endIndexExclusive, source);
    }
}