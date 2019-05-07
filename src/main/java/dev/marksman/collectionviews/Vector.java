package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import static com.jnape.palatable.lambda.adt.Maybe.maybe;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;

/**
 * A finite, ordered view of a collection.
 * <p>
 * A {@code Vector} guarantees the following:
 * <ul>
 * <li>A {@link Vector#size} method that executes in O(1).
 * <li>A {@link Vector#get} method that retrieves an element by index in O(1).
 * <li>An {@link Vector#isEmpty} method that executes in O(1).
 * <li>Iteration will always terminate.
 * <li>Protected from mutation by the bearer.
 * <li>The bearer cannot gain access to a reference to the underlying collection.
 * </ul>
 * <p>
 * Additionally, the ability to transform the {@code Vector} to new {@code Vector}s, without
 * affecting or copying the underlying collection, is provided through the following methods:
 * <ul>
 * <li>{@link Vector#fmap}
 * <li>{@link Vector#take}
 * <li>{@link Vector#drop}
 * <li>{@link Vector#slice}
 * </ul>
 * <p>
 * While {@code Vector} does implement {@link Iterable}, it does <i>not</i> implement {@link java.util.Collection}.
 * If you need to convert the {@code Vector} to a {@code java.util.Collection}, you will need to make a copy.
 * Consider using Lambda's {@code toCollection} function to accomplish this.
 *
 * @param <A> the element type
 */
public interface Vector<A> extends Iterable<A>, RandomAccess {

    /**
     * Returns the size of this {@code Vector}.
     * <p>
     * Executes in O(1).
     *
     * @return the number of elements in this {@code Vector}
     */
    int size();

    /**
     * Gets an element from this {@code Vector} at an index.
     * <p>
     * Executes in O(1).
     *
     * @param index the index of the element to retrieve.
     *              Must be between 0 and <code>size() - 1</code>, otherwise will
     *              throw an {@link IndexOutOfBoundsException}
     * @return the element at {@code index}.  May be null, if the underlying data contains a null at that index.
     * @throws IndexOutOfBoundsException if index is less than 0 or greater than or equal to {@code size()}
     */
    A unsafeGet(int index);

    /**
     * Returns a new {@code Vector} that drops the first {@code count} elements of this {@code Vector}.
     * <p>
     * Does not make copies of any underlying data structures.
     * <p>
     * Use caution when taking a small slice of a huge {@code Vector} that you no longer need.
     * The smaller slice will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     *
     * @param count the number of elements to drop from this {@code Vector}.
     *              Must be &gt;= 0.
     *              May exceed size of this {@code Vector}, in which case, the result will be an
     *              empty {@code Vector}.
     * @return a {@code Vector<A>}
     */
    default Vector<A> drop(int count) {
        return Vectors.drop(count, this);
    }

    /**
     * Maps a function over this {@code Vector}.
     * <p>
     * Returns a new {@link Vector} of the same size (but possibly a different type).
     * <p>
     * Does not make any copies of underlying data structures.
     * <p>
     * This method is stack-safe, so a {@code Vector} can be mapped as many times as the heap permits.
     *
     * @param f   a function from {@code A} to {@code B}.
     *            Not null.
     *            This function should be referentially transparent and not perform side-effects.
     *            It may be called zero or more times for each element.
     * @param <B> The type of the elements contained in the output Vector.
     * @return a {@code Vector<B>} of the same size
     */
    default <B> Vector<B> fmap(Fn1<? super A, ? extends B> f) {
        return Vectors.map(f, this);
    }

    /**
     * Gets an element from this {@code Vector} at an index.
     * <p>
     * Executes in O(1).
     * Will never return null.
     *
     * @param index the index of the element to retrieve
     * @return an element wrapped in a {@link Maybe#just} if the index is in range and the element is not null.
     * {@link Maybe#nothing} otherwise.
     */
    default Maybe<A> get(int index) {
        if (index >= 0 && index < size()) {
            return maybe(unsafeGet(index));
        } else {
            return nothing();
        }
    }

    /**
     * Tests whether this {@code Vector} is empty.
     * <p>
     * Executes in O(1).
     *
     * @return true if this {@code Vector} is empty, false otherwise.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns an iterator over this {@code Vector}'s elements.
     *
     * @return an Iterator
     */
    @Override
    default Iterator<A> iterator() {
        return VectorHelpers.vectorIterator(this);
    }

    default Vector<A> reverse() {
        return Vectors.reverse(this);
    }

    /**
     * Creates a slice of this {@code Vector}.
     * <p>
     * Does not make copies of any underlying data structures.
     * <p>
     * Use caution when taking a small slice of a huge {@code Vector} that you no longer need.
     * The smaller slice will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     * To avoid this situation, use {@link Vector#copySliceFrom} instead.
     *
     * @param startIndex        the index of the element to begin the slice.
     *                          Must be &gt;= 0.
     *                          May exceed the size of this {@code Vector}, in which case an empty {@code Vector} will be returned.
     * @param endIndexExclusive the end index (exclusive) of the slice.  Must be &gt;= {@code startIndex}.
     *                          May exceed the size of this {@code Vector}, in which case the slice will
     *                          contain as many elements as available.
     * @return a {@code Vector<A>}
     */
    default Vector<A> slice(int startIndex, int endIndexExclusive) {
        return Vectors.slice(startIndex, endIndexExclusive, this);
    }

    /**
     * Returns the tail of this {@code Vector}.
     * <p>
     * The tail of a {@link Vector} is the same {@code Vector} with the first element dropped.
     * May be empty.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a {@code Vector<A>}
     */
    default Vector<A> tail() {
        return drop(1);
    }

    /**
     * Returns a new {@code Vector} containing at most the first {@code count} elements of this {@code Vector}.
     * <p>
     * Does not make copies of any underlying data structures.
     * <p>
     * Use caution when taking a small slice of a huge {@link Vector} that you no longer need.
     * The smaller slice will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     * To avoid this situation, use {@link Vector#copyFrom(int, Iterable)} instead.
     *
     * @param count the maximum number of elements to take from this {@code Vector}.
     *              Must be &gt;= 0.
     *              May exceed size of this {@code Vector}.
     * @return a {@code Vector<A>}
     */
    default Vector<A> take(int count) {
        return Vectors.take(count, this);
    }

    /**
     * Converts this {@code Vector} to an {@code ImmutableVector}.
     * <p>
     * This method will make a copy of the underlying data structure if necessary to guarantee immutability.
     * <p>
     * If this {@link Vector} is already an {@link ImmutableVector}, no copies are made and this method is a no-op.
     *
     * @return an {@code ImmutableVector} of the same type and containing the same elements
     */
    default ImmutableVector<A> toImmutable() {
        return ImmutableVectors.ensureImmutable(this);
    }

    /**
     * Attempts to convert this {@code Vector} to a {@code NonEmptyVector}.
     * <p>
     * If successful, returns a {@link NonEmptyVector} containing the same elements as this one, wrapped in a {@link Maybe#just}.
     * <p>
     * If this {@code Vector} is empty, returns {@link Maybe#nothing}.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a {@code Maybe<NonEmptyVector<A>>}
     */
    default Maybe<? extends NonEmptyVector<A>> toNonEmpty() {
        return ImmutableVectors.maybeNonEmptyWrap(this);
    }

    /**
     * Attempts to convert this {@code Vector} to a {@code NonEmptyVector}.
     * <p>
     * If successful, returns a {@link NonEmptyVector} containing the same elements as this one.
     * Use this if you are confident that this {@link Vector} is not empty.
     * <p>
     * If this {@code Vector} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a {@code NonEmptyVector<A>}
     * @throws IllegalArgumentException if this {@code Vector} is empty
     */
    default NonEmptyVector<A> toNonEmptyOrThrow() {
        return Vectors.nonEmptyWrapOrThrow(this);
    }

    /**
     * Zips this {@code Vector} with its indices.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a new {@code Vector} containing pairs consisting of all elements of this {@code Vector} paired with their index.
     * Indices start at 0.
     */
    default Vector<Tuple2<A, Integer>> zipWithIndex() {
        return Vectors.zipWithIndex(this);
    }

    /**
     * Returns an empty {@code ImmutableVector}.
     *
     * @param <A> the element type
     * @return an empty {@code ImmutableVector<A>}
     */
    static <A> ImmutableVector<A> empty() {
        return Vectors.empty();
    }

    /**
     * Creates a {@code ImmutableNonEmptyVector} with the given elements.
     *
     * @param first the first element
     * @param more  the remaining elements
     * @param <A>   the element type
     * @return an {@code ImmutableNonEmptyVector<A>}
     */
    @SafeVarargs
    static <A> ImmutableNonEmptyVector<A> of(A first, A... more) {
        return Vectors.of(first, more);
    }

    /**
     * Creates an {@code ImmutableVector} that repeats the same element {@code size} times.
     * <p>
     * Uses O(1) memory.
     * <p>
     * See {@link NonEmptyVector#fill} if you require an {@link ImmutableNonEmptyVector} to be returned.
     *
     * @param size  the number of elements.
     *              Must be &gt;= 0.
     * @param value the value that will be repeated for all elements of the {@code ImmutableVector}
     * @param <A>   the element type
     * @return an {@code ImmutableVector<A>} of {@code size} elements, with each element having
     * the value {@code value}
     */
    static <A> ImmutableVector<A> fill(int size, A value) {
        return Vectors.fill(size, value);
    }

    /**
     * Creates an {@code ImmutableVector} where elements are lazily evaluated.
     * <p>
     * Uses O(1) memory.
     * <p>
     * See {@link NonEmptyVector#lazyFill} if you require a {@link ImmutableNonEmptyVector} to be returned.
     *
     * @param size          the number of elements.
     *                      Must be &gt;= 0.
     * @param valueSupplier a function that accepts an index and returns the computed value for that index.
     *                      Not null.
     *                      This function should be referentially transparent and not perform side-effects.
     *                      It may be called zero or more times for each element.
     * @param <A>           the element type
     * @return an {@code ImmutableVector<A>}
     */
    static <A> ImmutableVector<A> lazyFill(int size, Fn1<Integer, A> valueSupplier) {
        return Vectors.lazyFill(size, valueSupplier);
    }

    /**
     * Creates a {@code Vector} that wraps an array.
     * <p>
     * Does not make any copies of the given array.
     * The created {@link Vector} will hold on to a reference to the array, but will never alter it in any way.
     * <p>
     * Bearers of the created {@code Vector} will be unable to gain access to the underlying array, so it is safe to share.
     * <p>
     * Since no copy is made, be aware that anyone that holds a direct reference to the array can still mutate it.
     * Use {@link Vector#copyFrom} instead if you want to avoid this situation.
     *
     * @param underlying array to wrap; not null
     * @param <A>        the element type
     * @return a {@code Vector<A>}
     */
    static <A> Vector<A> wrap(A[] underlying) {
        return Vectors.wrap(underlying);
    }

    /**
     * Creates a {@code Vector} that wraps a {@code java.util.List}.
     * <p>
     * Does not make any copies of the given {@link java.util.List}.
     * The created {@link Vector} will hold a reference to the given {@code List}, but will not alter it in any way.
     * <p>
     * Bearers of the created {@code Vector} will be unable to gain access to the underlying {@code List}, so it is safe to share.
     * <p>
     * Since no copy is made, be aware that anyone that holds a direct reference to the {@code List} can still mutate it.
     * Mutating the {@code List} is not advised.
     * Operations that change the size of the underlying {@code List} will result in unpredictable behavior.
     * Use {@link Vector#copyFrom} if you want to avoid this situation.
     *
     * @param underlying {@code List} to wrap; not null
     * @param <A>        the element type
     * @return a {@code Vector<A>}
     */
    static <A> Vector<A> wrap(List<A> underlying) {
        return Vectors.wrap(underlying);
    }

    /**
     * Creates an {@code ImmutableVector} that is copied from any {@code Iterable}.
     * <p>
     * The entire {@link Iterable} will be eagerly iterated.
     * Be careful not to pass in an infinite {@code Iterable} or this method will not terminate.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If {@code source} already is an {@link ImmutableVector}, it will be returned directly.
     *
     * @param source an {@code Iterable<A>} that will be iterated eagerly in its entirety;  not null
     * @param <A>    the element type
     * @return an {@code ImmutableVector<A>}
     */
    static <A> ImmutableVector<A> copyFrom(Iterable<A> source) {
        return ImmutableVectors.copyFrom(source);
    }

    /**
     * Creates an {@code ImmutableVector} that is copied from an array.
     *
     * @param source the array to copy from.
     *               Not null.
     *               This method will not alter or hold on to a reference of this array.
     * @param <A>    the element type
     * @return an {@code ImmutableVector<A>}
     */
    static <A> ImmutableVector<A> copyFrom(A[] source) {
        return ImmutableVectors.copyFrom(source);
    }

    /**
     * Creates an {@code ImmutableVector} that is copied from any {@code Iterable}, but consuming a maximum number of elements.
     * <p>
     * The {@link Iterable} will be eagerly iterated, but only up to a maximum of {@code maxCount} elements.
     * If {@code maxCount} elements are not available, then the all of the elements available will be returned.
     * <p>
     * This method will make a copy of the data provided, unless {@code source} is
     * an {@link ImmutableVector} and its size is less than or equal to {@code maxCount},
     * in which case it will be returned directly.
     * <p>
     * If {@code source} is an {@code ImmutableVector} that is greater than {@code maxCount} in size,
     * a copy will always be made, therefore making it memory-safe to take a small slice of
     * a huge {@link Vector} that you no longer need.
     *
     * @param maxCount the maximum number of elements to consume from the source.
     *                 Must be &gt;= 0.
     * @param source   an {@code Iterable<A>} that will be iterated eagerly for up to {@code maxCount} elements.
     *                 Not null.
     *                 It is safe for {@code source} to be infinite.
     * @param <A>      the element type
     * @return an {@code ImmutableVector} that contains at most {@code maxCount} elements
     */
    static <A> ImmutableVector<A> copyFrom(int maxCount, Iterable<A> source) {
        return ImmutableVectors.copyFrom(maxCount, source);
    }

    /**
     * Returns a new {@code ImmutableVector} that is copied from an array.
     *
     * @param maxCount the maximum number of elements to copy from the array.
     *                 Must be &gt;= 0.
     * @param source   the array to copy from.
     *                 Not null.
     *                 This method will not alter or hold on to a reference of this array.
     * @param <A>      the element type
     * @return an {@code ImmutableVector<A>}
     */
    static <A> ImmutableVector<A> copyFrom(int maxCount, A[] source) {
        return ImmutableVectors.copyFrom(maxCount, source);
    }

    /**
     * Creates an {@code ImmutableVector} by copying a slice from an {@code Iterable}.
     * <p>
     * The {@link Iterable} will be eagerly iterated, but only for the number of elements needed to fulfill the requested slice.
     * If not enough elements are not available, then this method yields as many elements that were available.
     * <p>
     * This method will make a copy of the data provided, except in the case {@code startIndex} is 0
     * and {@code source} is an {@link ImmutableVector} whose size is less than or equal to {@code count},
     * in which case it will be returned directly.
     * <p>
     * It is memory-safe to use this method to take a small slice of a huge {@link Vector} that you no longer need.
     *
     * @param startIndex        the index of the element to begin the slice.
     *                          Must be &gt;= 0.
     *                          May exceed the size of the {@link Iterable}.
     * @param endIndexExclusive the end index (exclusive) of the slice.
     *                          Must be &gt;= {@code startIndex}.
     *                          May exceed the size of the {@link Iterable}.
     * @param source            an {@code Iterable<A>} that will be iterated eagerly for up to {@code endIndexExclusive} elements.
     *                          Not null.
     *                          It is safe for {@code source} to be infinite.
     * @param <A>               the element type
     * @return an {@code ImmutableVector<A>}
     */
    static <A> ImmutableVector<A> copySliceFrom(int startIndex, int endIndexExclusive, Iterable<A> source) {
        return ImmutableVectors.copySliceFrom(startIndex, endIndexExclusive, source);
    }

}
