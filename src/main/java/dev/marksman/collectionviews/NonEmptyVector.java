package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;

import java.util.Iterator;
import java.util.List;

import static com.jnape.palatable.lambda.adt.Maybe.just;

/**
 * A {@link Vector} that is guaranteed at compile-time to contain at least one element.
 * <p>
 * In addition to guarantees of {@link Vector}, provides the following benefits:
 * <ul>
 * <li>{@link NonEmptyVector#head} method that returns the first element.</li>
 * <li>Implements {@link NonEmptyIterable}.</li>
 * <li>{@link NonEmptyVector#fmap} always returns a {@code NonEmptyVector}.</li>
 * </ul>
 *
 * @param <A> the element type
 */
public interface NonEmptyVector<A> extends NonEmptyIterable<A>, Vector<A> {

    /**
     * Maps a function over the elements in a {@link NonEmptyVector} and returns a new {@link NonEmptyVector}
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
     * @return a {@code NonEmptyVector<B>} of the same size
     */
    @Override
    default <B> NonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return Vectors.mapNonEmpty(f, this);
    }

    /**
     * Tests whether the {@link Vector} is empty.  Executes in O(1).
     * <p>
     * Always returns {@code false} for {@link NonEmptyVector}s.
     *
     * @return always {@code false}
     */
    @Override
    default boolean isEmpty() {
        return false;
    }

    /**
     * Returns an iterator over elements of type {@code A}.
     *
     * @return an Iterator.
     */
    @Override
    default Iterator<A> iterator() {
        return VectorHelpers.vectorIterator(this);
    }

    /**
     * Returns the tail of the {@link Vector}, i.e. the same {@link Vector} with the first element dropped.
     * May be empty.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a {@link Vector} of the same type
     */
    @Override
    default Vector<A> tail() {
        return drop(1);
    }

    /**
     * Returns an {@link ImmutableNonEmptyVector} containing the same elements as this one.
     * This method will make a copy of the underlying data structure if necessary to
     * guarantee immutability.
     * <p>
     * If this {@link NonEmptyVector} is already an {@link ImmutableNonEmptyVector}, no copies are made
     * and this method is a no-op.
     *
     * @return an {@link ImmutableNonEmptyVector} of the same type and containing the same elements
     */
    @Override
    default ImmutableNonEmptyVector<A> toImmutable() {
        return ImmutableVectors.ensureImmutable(this);
    }

    /**
     * Attempts to convert this {@link Vector} to a {@link NonEmptyVector}.
     * Since this will always be successful for {@link NonEmptyVector}s,
     * this method always returns itself wrapped in a {@link Maybe#just}.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    @Override
    default Maybe<? extends NonEmptyVector<A>> toNonEmpty() {
        return just(this);
    }

    /**
     * Attempts to convert this {@link Vector} to a {@link NonEmptyVector}.
     * Since this will always be successful for {@link NonEmptyVector}s,
     * this method always returns itself.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    @Override
    default NonEmptyVector<A> toNonEmptyOrThrow() {
        return this;
    }

    /**
     * Attempts to create a {@link NonEmptyVector} that wraps an array.
     * <p>
     * Does not make any copies of the given array.
     * The {@link NonEmptyVector} will hold on to a reference to the array, but will never alter it in any way.
     * <p>
     * Since bearers of this {@link NonEmptyVector} will be unable to mutate or gain access to the underlying array,
     * it is safe to share.
     * <p>
     * Since this does not make a copy of the array, be aware that anyone that holds a direct reference to
     * the array can still mutate it.  Use {@link Vector#copyFrom} instead if you want to avoid this situation.
     *
     * @param underlying array to wrap
     * @param <A>        the element type
     * @return a {@code NonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code underlying} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<NonEmptyVector<A>> tryWrap(A[] underlying) {
        return Vectors.tryNonEmptyWrap(underlying);
    }

    /**
     * Attempts to create a {@link NonEmptyVector} that wraps a {@link java.util.List}.
     * <p>
     * Does not make any copies of the given {@link java.util.List}.
     * The {@link NonEmptyVector} will hold a reference to the given {@link java.util.List}, but will not alter it in any way.
     * <p>
     * Since bearers of this {@link NonEmptyVector} will be unable to mutate or gain access to the underlying {@link java.util.List},
     * it is safe to share.
     * <p>
     * Since this does not make a copy of the {@link java.util.List}, be aware that anyone that holds a direct reference to
     * the {@link java.util.List} can still mutate it.  Mutating the {@link java.util.List} is not advised.
     * Operations that change the size of the underlying {@link java.util.List} will result in unpredictable behavior.
     * Use {@link Vector#copyFrom} if you want to avoid this situation.
     *
     * @param underlying {@link List} to wrap
     * @param <A>        the element type
     * @return a {@code NonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code underlying} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<NonEmptyVector<A>> tryWrap(List<A> underlying) {
        return Vectors.tryNonEmptyWrap(underlying);
    }

    /**
     * Attempts to create a {@link NonEmptyVector} that wraps an array.
     * If it is not possible, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make any copies of the given array.
     * The {@link NonEmptyVector} will hold on to a reference to the array, but will never alter it in any way.
     * <p>
     * Since bearers of this {@link NonEmptyVector} will be unable to mutate or gain access to the underlying array,
     * it is safe to share.
     * <p>
     * Since this does not make a copy of the array, be aware that anyone that holds a direct reference to
     * the array can still mutate it.  Use {@link NonEmptyVector#copyFromOrThrow(Object[])} instead if you want to avoid this situation.
     *
     * @param underlying array to wrap
     * @param <A>        the element type
     * @return a {@code NonEmptyVector<A>} if {@code underlying} is non-empty; throws an {@link IllegalArgumentException} otherwise
     */
    static <A> NonEmptyVector<A> wrapOrThrow(A[] underlying) {
        return Vectors.nonEmptyWrapOrThrow(underlying);
    }

    /**
     * Attempts to create a {@link NonEmptyVector} that wraps a {@link java.util.List}.
     * If it is not possible, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make any copies of the given {@link java.util.List}.
     * The {@link NonEmptyVector} will hold a reference to the given {@link java.util.List}, but will not alter it in any way.
     * <p>
     * Since bearers of this {@link NonEmptyVector} will be unable to mutate or gain access to the underlying {@link java.util.List},
     * it is safe to share.
     * <p>
     * Since this does not make a copy of the {@link java.util.List}, be aware that anyone that holds a direct reference to
     * the {@link java.util.List} can still mutate it.  Mutating the {@link java.util.List} is not advised.
     * Operations that change the size of the underlying {@link java.util.List} will result in unpredictable behavior.
     * Use {@link NonEmptyVector#copyFromOrThrow(Iterable)} if you want to avoid this situation.
     *
     * @param underlying {@link List} to wrap
     * @param <A>        the element type
     * @return a {@code NonEmptyVector<A>} if {@code underlying} is non-empty; throws an {@link IllegalArgumentException} otherwise
     */
    static <A> NonEmptyVector<A> wrapOrThrow(List<A> underlying) {
        return Vectors.nonEmptyWrapOrThrow(underlying);
    }

    /**
     * Attempts to constructs a new {@link ImmutableNonEmptyVector} from any {@link Iterable}.
     * <p>
     * If determined to be non-empty, the entire {@link Iterable} will be eagerly iterated, so be careful not
     * to pass in an infinite {@link Iterable} or this method will not terminate.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If {@code source} already is an {@link ImmutableVector}, it will be returned directly.
     *
     * @param source an {@code Iterable<A>} that may be iterated eagerly in its entirety
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptyVector<A>> tryCopyFrom(Iterable<A> source) {
        return ImmutableVectors.tryNonEmptyCopyFrom(source);
    }

    /**
     * Attempts to constructs a new {@link ImmutableNonEmptyVector} that is copied from an array.
     *
     * @param source the array to copy from.
     *               This method will not alter or hold on to a reference of this array.
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptyVector<A>> tryCopyFrom(A[] source) {
        return ImmutableVectors.tryNonEmptyCopyFrom(source);
    }

    /**
     * Attempts to construct a new {@link ImmutableNonEmptyVector} from any {@link Iterable},
     * but consuming a maximum number of elements.
     * <p>
     * If determined to be non-empty, the {@link Iterable} will be eagerly iterated,
     * but only up to a maximum of {@code maxCount} elements.  If {@code maxCount} elements
     * are not available, then the all of the elements available will be returned.
     * <p>
     * This method will make a copy of the data provided, unless {@code source} is
     * an {@link ImmutableVector} and its size is less than or equal to {@code maxCount},
     * in which case it will be returned directly.
     * <p>
     * If {@code source} is an {@link ImmutableVector} that is greater than {@code maxCount} in size,
     * a copy will always be made, therefore making it memory-safe to take a small slice of
     * a huge {@link Vector} that you no longer need.
     *
     * @param maxCount the maximum number of elements to consume from the source.  Must be &gt;= 0.
     *                 If 0, this method will always return {@link Maybe#nothing}.
     * @param source   an {@code Iterable<A>} that will be iterated eagerly for up to {@code maxCount} elements.
     *                 It is safe for {@code source} to be infinite.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptyVector<A>> tryCopyFrom(int maxCount, Iterable<A> source) {
        return ImmutableVectors.tryNonEmptyCopyFrom(maxCount, source);
    }

    /**
     * Attempts to constructs a new {@link ImmutableNonEmptyVector} that is copied from an array,
     * but with a maximum number of elements.
     *
     * @param maxCount the maximum number of elements to copy from the array. Must be &gt;= 0.
     *                 If 0, this method will always return {@link Maybe#nothing}.
     * @param source   the array to copy from.
     *                 This method will not alter or hold on to a reference of this array.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptyVector<A>> tryCopyFrom(int maxCount, A[] source) {
        return ImmutableVectors.tryNonEmptyCopyFrom(maxCount, source);
    }

    /**
     * Attempts to constructs a new {@link ImmutableNonEmptyVector} from any {@link Iterable}.
     * If the {@link Iterable} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * If determined to be non-empty, the entire {@link Iterable} will be eagerly iterated, so be careful not
     * to pass in an infinite {@link Iterable} or this method will not terminate.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If {@code source} already is an {@link ImmutableVector}, it will be returned directly.
     *
     * @param source an {@code Iterable<A>} that will be iterated eagerly in its entirety
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptyVector<A>}
     */
    static <A> ImmutableNonEmptyVector<A> copyFromOrThrow(Iterable<A> source) {
        return ImmutableVectors.nonEmptyCopyFromOrThrow(source);
    }

    /**
     * Attempts to constructs a new {@link ImmutableNonEmptyVector} that is copied from an array.
     * If the array is empty, throws an {@link IllegalArgumentException}.
     *
     * @param source the array to copy from.
     *               This method will not alter or hold on to a reference of this array.
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptyVector<A>}
     */
    static <A> ImmutableNonEmptyVector<A> copyFromOrThrow(A[] source) {
        return ImmutableVectors.nonEmptyCopyFromOrThrow(source);
    }

    /**
     * Attempts to construct a new {@link ImmutableNonEmptyVector} from any {@link Iterable},
     * but consuming a maximum number of elements.
     * If the {@link Iterable} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * If determined to be non-empty, the {@link Iterable} will be eagerly iterated,
     * but only up to a maximum of {@code maxCount} elements.  If {@code maxCount} elements
     * are not available, then the all of the elements available will be returned.
     * <p>
     * This method will make a copy of the data provided, unless {@code source} is
     * an {@link ImmutableVector} and its size is less than or equal to {@code maxCount},
     * in which case it will be returned directly.
     * <p>
     * If {@code source} is an {@link ImmutableVector} that is greater than {@code maxCount} in size,
     * a copy will always be made, therefore making it memory-safe to take a small slice of
     * a huge {@link Vector} that you no longer need.
     *
     * @param maxCount the maximum number of elements to consume from the source.  Must be &gt;= 1.
     * @param source   an {@code Iterable<A>} that will be iterated eagerly for up to {@code maxCount} elements.
     *                 It is safe for {@code source} to be infinite.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptyVector<A>}
     */
    static <A> ImmutableNonEmptyVector<A> copyFromOrThrow(int maxCount, Iterable<A> source) {
        return ImmutableVectors.nonEmptyCopyFromOrThrow(maxCount, source);
    }

    /**
     * Attempts to constructs a new {@link ImmutableNonEmptyVector} that is copied from an array,
     * but with a maximum number of elements.
     * If the array is empty, throws an {@link IllegalArgumentException}.
     *
     * @param maxCount the maximum number of elements to copy from the array. Must be &gt;= 1.
     * @param source   the array to copy from.
     *                 This method will not alter or hold on to a reference of this array.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptyVector<A>}
     */
    static <A> ImmutableNonEmptyVector<A> copyFromOrThrow(int maxCount, A[] source) {
        return ImmutableVectors.nonEmptyCopyFromOrThrow(maxCount, source);
    }

    /**
     * Constructs a new {@link ImmutableNonEmptyVector} of size {@code size} containing {@code value} at
     * each index, using O(1) memory.
     *
     * @param size  the number of elements.  Must be &gt;= 1.
     * @param value the value that will be occupy all elements of the {@link Vector}
     * @param <A>   the element type
     * @return an {@code ImmutableVector<A>} of {@code size} elements, with each element having
     * the value {@code value}
     */
    static <A> ImmutableNonEmptyVector<A> fill(int size, A value) {
        return Vectors.nonEmptyFill(size, value);
    }

    /**
     * Constructs a new {@link ImmutableNonEmptyVector} of size {@code size} with elements computed by their
     * indices using a provided function.  Uses O(1) memory.
     *
     * @param size          the number of elements.  Must be &gt;= 1.
     * @param valueSupplier a function that accepts an index and returns the computed value for
     *                      that index.   This function should be referentially transparent and not
     *                      perform side-effects. It may be called zero or more times for each element.
     * @param <A>           the element type
     * @return an {@code ImmutableVector<A>} of {@code size} elements
     */
    static <A> ImmutableNonEmptyVector<A> lazyFill(int size, Fn1<Integer, A> valueSupplier) {
        return Vectors.nonEmptyLazyFill(size, valueSupplier);
    }

}
