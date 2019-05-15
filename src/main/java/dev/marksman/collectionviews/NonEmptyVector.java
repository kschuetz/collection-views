package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;

import java.util.Iterator;
import java.util.List;

import static com.jnape.palatable.lambda.adt.Maybe.just;

/**
 * A {@code Vector} that is guaranteed at compile-time to contain at least one element.
 * <p>
 * In addition to guarantees of {@link Vector}, provides the following benefits:
 * <ul>
 * <li>{@link NonEmptyVector#head} method that returns the first element.</li>
 * <li>Implements {@link NonEmptyIterable}.</li>
 * <li>{@link NonEmptyVector#fmap} always returns a {@code NonEmptyVector}.</li>
 * <li>{@link NonEmptyVector#reverse} always returns a {@code NonEmptyVector}.</li>
 * <li>{@link NonEmptyVector#zipWithIndex} always returns a {@code NonEmptyVector}.</li>
 * </ul>
 *
 * @param <A> the element type
 */
public interface NonEmptyVector<A> extends NonEmptyIterable<A>, Vector<A> {

    default <B> NonEmptyVector<Tuple2<A, B>> cross(NonEmptyVector<B> other) {
        return Vectors.nonEmptyCross(this, other);
    }

    /**
     * Maps a function over this {@code NonEmptyVector}.
     * <p>
     * Returns a new {@link NonEmptyVector} of the same size (but possibly a different type).
     * <p>
     * Does not make any copies of underlying data structures.
     * <p>
     * This method is stack-safe, so a {@code NonEmptyVector} can be mapped as many times as the heap permits.
     *
     * @param f   a function from {@code A} to {@code B}.
     *            Not null.
     *            This function should be referentially transparent and not perform side-effects.
     *            It may be called zero or more times for each element.
     * @param <B> The type of the elements contained in the output Vector.
     * @return a {@code NonEmptyVector<B>} of the same size
     */
    @Override
    default <B> NonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return Vectors.nonEmptyMap(f, this);
    }

    /**
     * Returns the first element.
     *
     * @return a element of type {@code A}
     */
    @Override
    default A head() {
        return unsafeGet(0);
    }

    /**
     * Tests whether this {@code NonEmptyVector} is empty.
     * <p>
     * Always returns false for {@link NonEmptyVector}s.
     *
     * @return always false
     */
    @Override
    default boolean isEmpty() {
        return false;
    }

    /**
     * Returns an iterator over this {@code NonEmptyVector}'s elements.
     *
     * @return an Iterator.
     */
    @Override
    default Iterator<A> iterator() {
        return VectorHelpers.vectorIterator(this);
    }

    /**
     * Creates a {@code NonEmptyVector} with this {@code NonEmptyVector}'s elements in reversed order.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a {@code NonEmptyVector<A>}
     */
    @Override
    default NonEmptyVector<A> reverse() {
        return Vectors.nonEmptyReverse(this);
    }

    /**
     * Returns the tail of this {@code NonEmptyVector}. i.e. the same {@link Vector} with the first element dropped.
     * <p>
     * The tail of a {@link NonEmptyVector} is the same {@code Vector} with the first element dropped.
     * May be empty.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a {@code Vector<A>}
     */
    @Override
    default Vector<A> tail() {
        return drop(1);
    }

    @Override
    default NonEmptyIterable<? extends Vector<A>> tails() {
        return Vectors.nonEmptyTails(this);
    }

    /**
     * Converts this {@code NonEmptyVector} to an {@code ImmutableNonEmptyVector}.
     * <p>
     * This method will make a copy of the underlying data structure if necessary to guarantee immutability.
     * <p>
     * If this {@link NonEmptyVector} is already an {@link ImmutableNonEmptyVector}, no copies are made and this method is a no-op.
     *
     * @return an {@code ImmutableNonEmptyVector} of the same type and containing the same elements
     */
    @Override
    default ImmutableNonEmptyVector<A> toImmutable() {
        return ImmutableVectors.ensureImmutable(this);
    }

    /**
     * Attempts to convert this {@code Vector} to a {@code NonEmptyVector}.
     * <p>
     * Since this will always be successful for {@link NonEmptyVector}s,
     * this method always returns itself wrapped in a {@link Maybe#just}.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return this {@code NonEmptyVector} wrapped in a {@link Maybe#just}
     */
    @Override
    default Maybe<? extends NonEmptyVector<A>> toNonEmpty() {
        return just(this);
    }

    /**
     * Attempts to convert this {@code Vector} to a {@code NonEmptyVector}.
     * <p>
     * Since this will always be successful for {@link NonEmptyVector}s,
     * this method always returns itself.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return this {@code NonEmptyVector}
     */
    @Override
    default NonEmptyVector<A> toNonEmptyOrThrow() {
        return this;
    }

    default <B, R> NonEmptyVector<R> zipWith(Fn2<A, B, R> fn, NonEmptyVector<B> other) {
        return Vectors.nonEmptyZipWith(fn, this, other);
    }

    /**
     * Zips this {@code NonEmptyVector} with its indices.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a new {@code NonEmptyVector} containing pairs consisting of all elements of this {@code NonEmptyVector} paired with their index.
     * Indices start at 0.
     */
    @Override
    default NonEmptyVector<Tuple2<A, Integer>> zipWithIndex() {
        return Vectors.nonEmptyZipWithIndex(this);
    }

    /**
     * Attempts to create a {@code NonEmptyVector} that wraps an array.
     * <p>
     * Does not make any copies of the given array.
     * The created {@link NonEmptyVector} will hold on to a reference to the array, but will never alter it in any way.
     * <p>
     * Bearers of the created {@code NonEmptyVector} will be unable to gain access to the underlying array, it is safe to share.
     * <p>
     * Since no copy is made, be aware that anyone that holds a direct reference to the array can still mutate it.
     * Use {@link Vector#copyFrom} instead if you want to avoid this situation.
     *
     * @param underlying the array to wrap;  not null
     * @param <A>        the element type
     * @return a {@code NonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code underlying} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<NonEmptyVector<A>> maybeWrap(A[] underlying) {
        return Vectors.maybeNonEmptyWrap(underlying);
    }

    /**
     * Attempts to create a {@code NonEmptyVector} that wraps a {@code java.util.List}.
     * <p>
     * Does not make any copies of the given {@link java.util.List}.
     * The created {@link NonEmptyVector} will hold a reference to the given {@link java.util.List}, but will not alter it in any way.
     * <p>
     * Bearers of the {@code NonEmptyVector} will be unable to gain access to the underlying {@link java.util.List}, it is safe to share.
     * <p>
     * Since no copy is made, be aware that anyone that holds a direct reference to the {@code List} can still mutate it.
     * Mutating the {@code List} is not advised.
     * Operations that change the size of the underlying {@code List} will result in unpredictable behavior.
     * Use {@link Vector#copyFrom} if you want to avoid this situation.
     *
     * @param underlying {@code java.util.List} to wrap; not null
     * @param <A>        the element type
     * @return a {@code NonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code underlying} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<NonEmptyVector<A>> maybeWrap(List<A> underlying) {
        return Vectors.maybeNonEmptyWrap(underlying);
    }

    /**
     * Attempts to create a {@code NonEmptyVector} that wraps an array.
     * If it is not possible, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make any copies of the given array.
     * The created {@link NonEmptyVector} will hold on to a reference to the array, but will never alter it in any way.
     * <p>
     * Bearers of the created {@code NonEmptyVector} will be unable to gain access to the underlying array, it is safe to share.
     * <p>
     * Since no copy is made, be aware that anyone that holds a direct reference to the array can still mutate it.
     * Use {@link NonEmptyVector#copyFromOrThrow(Object[])} instead if you want to avoid this situation.
     *
     * @param underlying array to wrap; not null
     * @param <A>        the element type
     * @return a {@code NonEmptyVector<A>} if {@code underlying} is non-empty; throws an {@link IllegalArgumentException} otherwise
     */
    static <A> NonEmptyVector<A> wrapOrThrow(A[] underlying) {
        return Vectors.nonEmptyWrapOrThrow(underlying);
    }

    /**
     * Attempts to create a {@code NonEmptyVector} that wraps a {@code java.util.List}.
     * If it is not possible, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make any copies of the given {@link java.util.List}.
     * The created {@link NonEmptyVector} will hold a reference to the given {@code java.util.List}, but will not alter it in any way.
     * <p>
     * Bearers of the created {@link NonEmptyVector} will be unable to gain access to the underlying {@code List}, it is safe to share.
     * <p>
     * Since no copy is made, be aware that anyone that holds a direct reference to the {@code List} can still mutate it.
     * Mutating the {@code List} is not advised.
     * Operations that change the size of the underlying {@code List} will result in unpredictable behavior.
     * Use {@link NonEmptyVector#copyFromOrThrow(Iterable)} if you want to avoid this situation.
     *
     * @param underlying {@code List} to wrap; not null
     * @param <A>        the element type
     * @return a {@code NonEmptyVector<A>} if {@code underlying} is non-empty; throws an {@link IllegalArgumentException} otherwise
     */
    static <A> NonEmptyVector<A> wrapOrThrow(List<A> underlying) {
        return Vectors.nonEmptyWrapOrThrow(underlying);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptyVector} that is copied from any {@code Iterable}.
     * <p>
     * The entire {@link Iterable} will be eagerly iterated.
     * Be careful not to pass in an infinite {@code Iterable} or this method will not terminate.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If {@code source} already is an {@link ImmutableNonEmptyVector}, it will be returned directly.
     *
     * @param source an {@code Iterable<A>} that may be iterated eagerly in its entirety; not null
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptyVector<A>> maybeCopyFrom(Iterable<A> source) {
        return ImmutableVectors.maybeNonEmptyCopyFrom(source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptyVector} that is copied from an array.
     *
     * @param source the array to copy from.
     *               Not null.
     *               This method will not alter or hold on to a reference of this array.
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptyVector<A>> maybeCopyFrom(A[] source) {
        return ImmutableVectors.maybeNonEmptyCopyFrom(source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptyVector} that is copied from any {@link Iterable}, but consuming a maximum number of elements.
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
     *                 If 0, this method will always return {@link Maybe#nothing}.
     * @param source   an {@code Iterable<A>} that will be iterated eagerly for up to {@code maxCount} elements.
     *                 Not null.
     *                 It is safe for {@code source} to be infinite.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptyVector<A>> maybeCopyFrom(int maxCount, Iterable<A> source) {
        return ImmutableVectors.maybeNonEmptyCopyFrom(maxCount, source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptyVector} that is copied from an array, but with a maximum number of elements.
     *
     * @param maxCount the maximum number of elements to copy from the array.
     *                 Must be &gt;= 0.
     *                 If 0, this method will always return {@link Maybe#nothing}.
     * @param source   the array to copy from.
     *                 Not null.
     *                 This method will not alter or hold on to a reference of this array.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptyVector<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptyVector<A>> maybeCopyFrom(int maxCount, A[] source) {
        return ImmutableVectors.maybeNonEmptyCopyFrom(maxCount, source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptyVector} from any {@code Iterable}.
     * If the {@link Iterable} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * The entire {@link Iterable} will be eagerly iterated.
     * Be careful not to pass in an infinite {@code Iterable} or this method will not terminate.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If {@code source} already is an {@link ImmutableVector}, it will be returned directly.
     *
     * @param source an {@code Iterable<A>} that will be iterated eagerly in its entirety; not null
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptyVector<A>}
     */
    static <A> ImmutableNonEmptyVector<A> copyFromOrThrow(Iterable<A> source) {
        return ImmutableVectors.nonEmptyCopyFromOrThrow(source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptyVector} that is copied from an array.
     * If the array is empty, throws an {@link IllegalArgumentException}.
     *
     * @param source the array to copy from.
     *               Not null.
     *               This method will not alter or hold on to a reference of this array.
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptyVector<A>}
     */
    static <A> ImmutableNonEmptyVector<A> copyFromOrThrow(A[] source) {
        return ImmutableVectors.nonEmptyCopyFromOrThrow(source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptyVector} from any {@code Iterable}, but consuming a maximum number of elements.
     * If the {@link Iterable} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * The {@code Iterable} will be eagerly iterated, but only up to a maximum of {@code maxCount} elements.
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
     * @param maxCount the maximum number of elements to consume from the source.  Must be &gt;= 1.
     * @param source   an {@code Iterable<A>} that will be iterated eagerly for up to {@code maxCount} elements.
     *                 Not null.
     *                 It is safe for {@code source} to be infinite.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptyVector<A>}
     */
    static <A> ImmutableNonEmptyVector<A> copyFromOrThrow(int maxCount, Iterable<A> source) {
        return ImmutableVectors.nonEmptyCopyFromOrThrow(maxCount, source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptyVector} that is copied from an array, but with a maximum number of elements.
     * If the array is empty, throws an {@link IllegalArgumentException}.
     *
     * @param maxCount the maximum number of elements to copy from the array.
     *                 Must be &gt;= 1.
     * @param source   the array to copy from.
     *                 Not null.
     *                 This method will not alter or hold on to a reference of this array.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptyVector<A>}
     */
    static <A> ImmutableNonEmptyVector<A> copyFromOrThrow(int maxCount, A[] source) {
        return ImmutableVectors.nonEmptyCopyFromOrThrow(maxCount, source);
    }

    /**
     * Creates an {@code ImmutableNonEmptyVector} that repeats the same element {@code size} times.
     * <p>
     * Uses O(1) memory.
     *
     * @param size  the number of elements.
     *              Must be &gt;= 1.
     * @param value the value that will be repeated all elements of the {@link ImmutableNonEmptyVector}
     * @param <A>   the element type
     * @return an {@code ImmutableVector<A>} of {@code size} elements, with each element having
     * the value {@code value}
     */
    static <A> ImmutableNonEmptyVector<A> fill(int size, A value) {
        return Vectors.nonEmptyFill(size, value);
    }

    /**
     * Creates an {@code ImmutableNonEmptyVector} where elements are lazily evaluated.
     * <p>
     * Uses O(1) memory.
     *
     * @param size          the number of elements.
     *                      Must be &gt;= 1.
     * @param valueSupplier a function that accepts an index and returns the computed value for
     *                      that index.   This function should be referentially transparent and not
     *                      perform side-effects. It may be called zero or more times for each element.
     * @param <A>           the element type
     * @return an {@code ImmutableNonEmptyVector<A>}
     */
    static <A> ImmutableNonEmptyVector<A> lazyFill(int size, Fn1<Integer, A> valueSupplier) {
        return Vectors.nonEmptyLazyFill(size, valueSupplier);
    }

}
