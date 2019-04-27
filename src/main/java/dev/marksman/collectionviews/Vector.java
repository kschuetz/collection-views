package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import static com.jnape.palatable.lambda.adt.Maybe.maybe;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;

/**
 * A finite, ordered view of a collection that guarantees the following:
 * <ul>
 * <li>A {@code size} method that executes in O(1).</li>
 * <li>A {@code get} method that retrieves an element by index in O(1).</li>
 * <li>An {@code isEmpty} method that executes in O(1).</li>
 * <li>Iteration will always terminate.  </li>
 * <li>Protected from mutation by the bearer. </li>
 * <li>The bearer cannot gain access to a reference to the underlying collection.</li>
 * </ul>
 * <p>
 * Additionally, the ability to transform the {@code Vector} to new {@code Vector}s, without
 * affecting or copying the underlying collection, is provided through the following methods:
 * <ul>
 * <li>{@link Vector#fmap}</li>
 * <li>{@link Vector#take}</li>
 * <li>{@link Vector#drop}</li>
 * <li>{@link Vector#slice}</li>
 * </ul>
 * <p>
 * While {@code Vector} does implement {@link Iterable}, it does <i>not</i> implement {@link java.util.Collection}.
 * If you need to convert the {@code Vector} to a {@link java.util.Collection}, you will need to make a copy.
 * Consider using Lambda's {@code toCollection} function to accomplish this.
 *
 * @param <A> the element type
 */
public interface Vector<A> extends Iterable<A>, RandomAccess {
    /**
     * The size of the {@link Vector}.  Executes in O(1).
     *
     * @return the number of elements in the {@link Vector}.
     */
    int size();

    /**
     * Get an element from the {@link Vector} at an index.  Executes in O(1).
     *
     * @param index the index of the element to retrieve.  Must be between 0 and <code>size() - 1</code>, otherwise will
     *              throw an {@link IndexOutOfBoundsException}
     * @return the element at {@code index}.  May be {@code null}, if the underlying data contains a null at that index.
     */
    A unsafeGet(int index);

    /**
     * Tests whether the {@link Vector} is empty.  Executes in O(1).
     *
     * @return {@code true} if the {@link Vector} is empty, {@code false} otherwise.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the tail of the {@link Vector}, i.e. the same {@link Vector} with the first element dropped.
     * May be empty.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a {@link Vector} of the same type
     */
    default Vector<A> tail() {
        return drop(1);
    }

    /**
     * Get an element from the Vector at an index.  Executes in O(1).
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
     * Maps a function over the elements in a {@link Vector} and returns a new {@link Vector}
     * of the same size (but possibly a different type).
     * <p>
     * Does not make any copies of underlying data structures.
     * <p>
     * This method is stack-safe, so a Vector can be mapped as many times as the heap permits.
     *
     * @param f   a function from {@code A} to {@code B}.
     *            This function should be referentially transparent and not perform side-effects.
     *            It may be called zero or more times for each element.
     * @param <B> The type of the elements contained in the output Vector.
     * @return a {@code Vector<B>} of the same size
     */
    default <B> Vector<B> fmap(Fn1<? super A, ? extends B> f) {
        return Vectors.map(f, this);
    }

    /**
     * Returns an {@link ImmutableVector} containing the same elements as this one.
     * This method will make a copy of the underlying data structure if necessary to
     * guarantee immutability.
     * <p>
     * If this {@link Vector} is an {@link ImmutableVector} already, no copies are made
     * and this method is a no-op.
     *
     * @return an {@link ImmutableVector} of the same type and containing the same elements
     */
    default ImmutableVector<A> toImmutable() {
        return Vectors.ensureImmutable(this);
    }

    /**
     * Returns a {@link NonEmptyVector} containing the same elements as this one,
     * wrapped in a {@link Maybe#just}, if this {@link Vector} is not empty.
     * <p>
     * If this {@link Vector} is empty, returns {@link Maybe#nothing}.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    default Maybe<? extends NonEmptyVector<A>> toNonEmpty() {
        return Vectors.tryNonEmptyWrap(this);
    }

    /**
     * Returns a {@link NonEmptyVector} containing the same elements as this one,
     * if this {@link Vector} is not empty.  Use this if you are confident that this {@link Vector}
     * is not empty.
     * <p>
     * If this {@link Vector} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    default NonEmptyVector<A> toNonEmptyOrThrow() {
        return Vectors.nonEmptyWrapOrThrow(this);
    }

    /**
     * Returns an iterator over elements of type {@code A}.
     *
     * @return an Iterator.
     */
    @Override
    default Iterator<A> iterator() {
        return new VectorIterator<>(this);
    }

    /**
     * Returns a new {@link Vector} that contains at most the first {@code count} elements of this {@link Vector}.
     * <p>
     * Does not make copies of any underlying data structures.
     * <p>
     * Use caution when taking a small slice of a huge {@link Vector}, as the smaller slice
     * will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     * To avoid this situation, use {@link Vector#copyTakeFromIterable} instead.
     *
     * @param count the maximum number of elements to take from the {@link Vector}.  Must be &gt;= 0.
     *              May exceed size of {@link Vector}.
     * @return a {@code Vector<A>} containing between 0 and {@code count} elements
     */
    default Vector<A> take(int count) {
        return Vectors.take(count, this);
    }

    /**
     * Returns a new {@link Vector} that drops the first {@code count} elements.
     * <p>
     * Does not make copies of any underlying data structures.
     * <p>
     * Use caution when taking a small slice of a huge {@link Vector}, as the smaller slice
     * will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     *
     * @param count the number of elements to drop from the {@link Vector}.  Must be &gt;= 0.
     *              May exceed size of {@link Vector}, in which case, the result will be an
     *              empty {@link Vector}.
     * @return a {@code Vector<A>}
     */
    default Vector<A> drop(int count) {
        return Vectors.drop(count, this);
    }

    /**
     * Create a slice of an existing {@link Vector}.
     * <p>
     * Does not make copies of any underlying data structures.
     * <p>
     * Use caution when taking a small slice of a huge Vector that you no longer need, as the smaller slice
     * will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     * To avoid this situation, use {@link Vector#copySliceFromIterable} instead.
     *
     * @param startIndex        the index of the element to begin the slice.  Must be &gt;= 0.
     *                          May exceed the size of the {@link Vector}, in which case an empty {@link Vector} will be returned.
     * @param endIndexExclusive the end index (exclusive) of the slice.  Must be &gt;= {@code startIndex}.
     *                          May exceed the size of the {@link Vector}, in which case the slice will
     *                          contain as many elements as available.
     * @return a {@code Vector<A>}
     */
    default Vector<A> slice(int startIndex, int endIndexExclusive) {
        return Vectors.sliceFromIterable(startIndex, endIndexExclusive, this);
    }

    /**
     * Returns an empty {@link ImmutableVector}.
     *
     * @param <A> the element type
     * @return an empty {@code ImmutableVector<A>}
     */
    static <A> ImmutableVector<A> empty() {
        return Vectors.empty();
    }

    /**
     * Constructs a new {@link ImmutableNonEmptyVector} with the given elements.
     *
     * @param first the first element
     * @param more  the remaining elements
     * @param <A>   the element type
     * @return an {@code ImmutableNonEmptyVector<A>}, which is guaranteed to be non-empty and to be safe from mutation.
     */
    @SafeVarargs
    static <A> ImmutableNonEmptyVector<A> of(A first, A... more) {
        return Vectors.of(first, more);
    }

    /**
     * Creates a {@link Vector} that wraps an array.
     * <p>
     * Does not make any copies of the given array.
     * The {@link Vector} will hold on to a reference to the array, but will never alter it in any way.
     * <p>
     * Since bearers of this {@link Vector} will be unable to mutate or gain access to the underlying array,
     * it is safe to share.
     * <p>
     * Since this does not make a copy of the array, be aware that anyone that holds a direct reference to
     * the array can still mutate it.  Use {@link Vector#copyFromArray} instead if you want to avoid this situation.
     *
     * @param underlying array to wrap
     * @param <A>        the element type
     * @return a {@code Vector<A>}
     */
    static <A> Vector<A> wrap(A[] underlying) {
        return Vectors.wrap(underlying);
    }

    /**
     * Creates a {@link Vector} that wraps a {@link java.util.List}.
     * <p>
     * Does not make any copies of the given {@link java.util.List}.
     * The {@link Vector} will hold a reference to the given {@link java.util.List}, but will not alter it in any way.
     * <p>
     * Since bearers of this {@link Vector} will be unable to mutate or gain access to the underlying {@link java.util.List},
     * it is safe to share.
     * <p>
     * Since this does not make a copy of the {@link java.util.List}, be aware that anyone that holds a direct reference to
     * the {@link java.util.List} can still mutate it.  Mutating the {@link java.util.List} is not advised.
     * Operations that change the size of the underlying {@link java.util.List} will result in unpredictable behavior.
     * Use {@link Vector#copyFrom} if you want to avoid this situation.
     *
     * @param underlying {@link List} to wrap
     * @param <A>        the element type
     * @return a {@code Vector<A>}
     */
    static <A> Vector<A> wrap(List<A> underlying) {
        return Vectors.wrap(underlying);
    }

    /**
     * Builds a {@link Vector} from an {@link Iterable}.
     * <p>
     * Will only make a copy of the data if necessary.
     * If source is a {@link java.util.List} or another {@link Vector},
     * the data won't be copied.
     * <p>
     * If you require an {@link ImmutableVector}, see {@link Vector#copyTakeFromIterable}, which
     * will always perform a copy instead of wrapping.
     *
     * @param count  maximum number of elements to take from source.  Must be &gt;= 0.
     *               May exceed the number of elements in source, in which case,
     *               all of the elements available will be taken.
     * @param source any {@link Iterable}, even those that are potentially infinite
     * @param <A>    the element type
     * @return a {@code Vector<A>}
     */
    static <A> Vector<A> takeFromIterable(int count, Iterable<A> source) {
        return Vectors.takeFromIterable(count, source);
    }

    /**
     * Builds a {@link Vector} from an {@link Iterable}.
     * <p>
     * Will only make a copy of the data if necessary.
     * If source is a {@link java.util.List} or another {@link Vector},
     * the data won't be copied.
     * <p>
     * If you require an {@link ImmutableVector}, see {@link Vector#copySliceFromIterable}, which
     * will always perform a copy instead of wrapping.
     *
     * @param startIndex        the index of the element to begin the slice.  Must be &gt;= 0.
     *                          May exceed the size of the {@link Vector}, in which case an empty {@link Vector} will be returned.
     * @param endIndexExclusive the end index (exclusive) of the slice.  Must be &gt;= {@code startIndex}.
     *                          May exceed the size of the {@link Vector}, in which case the slice will
     *                          contain as many elements as available.
     * @param source            any {@link Iterable}, even those that are potentially infinite
     * @param <A>               the element type
     * @return a {@code Vector<A>}
     */
    static <A> Vector<A> sliceFromIterable(int startIndex, int endIndexExclusive, Iterable<A> source) {
        return Vectors.sliceFromIterable(startIndex, endIndexExclusive, source);
    }

    /**
     * Constructs a new {@link ImmutableVector} from any {@link Iterable}.
     * <p>
     * The entire {@link Iterable} will be eagerly iterated, so be careful not
     * to pass in an infinite {@link Iterable} or this method will not terminate.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If {@code source} already is an {@link ImmutableVector}, it will be returned directly.
     *
     * @param source an {@code Iterable<A>} that will be iterated eagerly in its entirety
     * @param <A>    the element type
     * @return an {@code ImmutableVector<A>}
     */
    static <A> ImmutableVector<A> copyFrom(Iterable<A> source) {
        return Vectors.copyAllFromIterable(source);
    }

    /**
     * Returns a new {@link ImmutableVector} that is copied from an array.
     *
     * @param source the array to copy from.
     *               This method will not alter or hold on to a reference of this array.
     * @param <A>    the element type
     * @return an {@code ImmutableVector<A>}
     */
    static <A> ImmutableVector<A> copyFromArray(A[] source) {
        return Vectors.copyFromArray(source);
    }

    /**
     * Constructs a new {@link ImmutableVector} from any {@link Iterable}, but consuming a maximum number of elements.
     * <p>
     * The {@link Iterable} will be eagerly iterated, but only up to a maximum of {@code count}
     * elements.  If {@code count} elements are not available, then the all of the elements
     * available will be returned.
     * <p>
     * This method will make a copy of the data provided, unless {@code source} is
     * an {@link ImmutableVector} and its size is less than or equal to {@code count},
     * in which case it will be returned directly.
     * <p>
     * If {@code source} is an {@link ImmutableVector} that is greater than {@code count} in size,
     * a copy will always be made, therefore making it memory-safe to take a small slice of
     * a huge {@link Vector} that you no longer need.
     *
     * @param count  the maximum number of elements to consume from the source.  Must be &gt;= 0.
     * @param source an {@code Iterable<A>} that will be iterated eagerly for up to {@code count} elements.
     *               It is safe for {@code source} to be infinite.
     * @param <A>    the element type
     * @return an {@link ImmutableVector} that contains at most {@code count} elements.
     */
    static <A> ImmutableVector<A> copyTakeFromIterable(int count, Iterable<A> source) {
        return Vectors.copyTakeFromIterable(count, source);
    }

    /**
     * Constructs a new {@link ImmutableVector} slice from an {@link Iterable}.
     * <p>
     * The {@link Iterable} will be eagerly iterated, but only for the number of elements
     * needed to fulfill the requested slice.
     * If not enough elements are not available, then this method yields as many
     * elements that were available.
     * <p>
     * This method will make a copy of the data provided, except in the case {@code startIndex} is 0
     * and {@code source} is an {@link ImmutableVector} whose size is less than or equal to {@code count},
     * in which case it will be returned directly.
     * <p>
     * It is memory-safe to use this method to take a small slice of a huge {@link Vector} that you no longer need.
     *
     * @param startIndex        the index of the element to begin the slice.  Must be &gt;= 0.
     *                          May exceed the size of the {@link Iterable}, in which case an empty {@link ImmutableVector} will be returned.
     * @param endIndexExclusive the end index (exclusive) of the slice.  Must be &gt;= {@code startIndex}.
     *                          May exceed the size of the {@link Iterable}, in which case the slice will
     *                          contain as many elements as available.
     * @param source            an {@code Iterable<A>} that will be iterated eagerly for up to {@code endIndexExclusive} elements.
     *                          It is safe for {@code source} to be infinite.
     * @param <A>               the element type
     * @return an {@code ImmutableVector<A>} that contains at most <code>endIndexExclusive - startIndex</code> elements.
     */
    static <A> ImmutableVector<A> copySliceFromIterable(int startIndex, int endIndexExclusive, Iterable<A> source) {
        return Vectors.copySliceFromIterable(startIndex, endIndexExclusive, source);
    }
}