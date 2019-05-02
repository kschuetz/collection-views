package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;

/**
 * A {@link Vector} that is guaranteed at compile-time to safe from mutation anywhere.  In other words,
 * it owns the sole reference to the underlying collection.
 * <p>
 * In addition to guarantees of {@link Vector}, provides the following benefits :
 * <ul>
 * <li>{@link ImmutableVector#fmap} always returns a {@code ImmutableVector}.</li>
 * <li>{@link ImmutableVector#tail} always returns a {@code ImmutableVector}.</li>
 * <li>{@link ImmutableVector#take} always returns a {@code ImmutableVector}.</li>
 * <li>{@link ImmutableVector#drop} always returns a {@code ImmutableVector}.</li>
 * <li>{@link ImmutableVector#slice} always returns a {@code ImmutableVector}.</li>
 * <li>{@link ImmutableVector#toImmutable} always returns itself.</li>
 * </ul>
 *
 * @param <A> the element type
 */
public interface ImmutableVector<A> extends Vector<A>, Immutable {

    /**
     * Returns a new {@link ImmutableVector} that drops the first {@code count} elements.
     * <p>
     * Does not make copies of any underlying data structures.
     * <p>
     * Use caution when taking a small slice of a huge {@link ImmutableVector} that you no longer need,
     * as the smaller slice will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     *
     * @param count the number of elements to drop from the {@link ImmutableVector}.  Must be &gt;= 0.
     *              May exceed size of {@link ImmutableVector}, in which case, the result will be an
     *              empty {@link ImmutableVector}.
     * @return an {@code ImmutableVector<A>}
     */
    @Override
    default ImmutableVector<A> drop(int count) {
        return Vectors.immutableDrop(count, this);
    }

    /**
     * Maps a function over the elements in an {@link ImmutableVector} and returns
     * a new {@link ImmutableVector} of the same size (but possibly a different type).
     * <p>
     * Does not make any copies of underlying data structures.
     * <p>
     * This method is stack-safe, so a Vector can be mapped as many times as the heap permits.
     *
     * @param f   a function from {@code A} to {@code B}.
     *            This function should be referentially transparent and not perform side-effects.
     *            It may be called zero or more times for each element.
     * @param <B> The type of the elements contained in the output Vector.
     * @return an {@code ImmutableVector<B>} of the same size
     */
    @Override
    default <B> ImmutableVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return ImmutableVectors.map(f, this);
    }

    /**
     * Create a slice of an existing {@link ImmutableVector}.
     * <p>
     * Does not make copies of any underlying data structures.
     * <p>
     * Use caution when taking a small slice of a huge {@link ImmutableVector} that you no longer need,
     * as the smaller slice will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     * To avoid this situation, use {@link Vector#copySliceFrom} instead.
     *
     * @param startIndex        the index of the element to begin the slice.  Must be &gt;= 0.
     *                          May exceed the size of the {@link Vector}, in which case an empty {@link Vector} will be returned.
     * @param endIndexExclusive the end index (exclusive) of the slice.  Must be &gt;= {@code startIndex}.
     *                          May exceed the size of the {@link Vector}, in which case the slice will
     *                          contain as many elements as available.
     * @return a {@code ImmutableVector<A>}
     */
    @Override
    default ImmutableVector<A> slice(int startIndex, int endIndexExclusive) {
        return ImmutableVectors.slice(startIndex, endIndexExclusive, this);
    }

    /**
     * Returns the tail of the {@link ImmutableVector}, i.e. the same {@link ImmutableVector} with the first element dropped.
     * May be empty.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return an {@link ImmutableVector} of the same type
     */
    @Override
    default ImmutableVector<A> tail() {
        return drop(1);
    }

    /**
     * Returns a new {@link ImmutableVector} that contains at most the first {@code count}
     * elements of this {@link ImmutableVector}.
     * <p>
     * Does not make copies of any underlying data structures.
     * <p>
     * Use caution when taking a small slice of a huge {@link ImmutableVector} that you no longer need,
     * as the smaller slice will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     * To avoid this situation, use {@link Vector#copyFrom(int, Iterable)} instead.
     *
     * @param count the maximum number of elements to take from the {@link ImmutableVector}.  Must be &gt;= 0.
     *              May exceed size of {@link ImmutableVector}.
     * @return a {@code ImmutableVector<A>} containing between 0 and {@code count} elements
     */
    @Override
    default ImmutableVector<A> take(int count) {
        return ImmutableVectors.take(count, this);
    }

    /**
     * Returns an {@link ImmutableVector} containing the same elements as this one.
     * Since this is an {@link ImmutableVector} already, this method simply returns
     * itself.
     *
     * @return itself
     */
    @Override
    default ImmutableVector<A> toImmutable() {
        return this;
    }

    /**
     * Attempts to convert this {@link ImmutableVector} to a {@link ImmutableNonEmptyVector}.
     * If successful, returns a {@link ImmutableNonEmptyVector} containing the same elements as this one,
     * wrapped in a {@link Maybe#just}.
     * <p>
     * If this {@link Vector} is empty, returns {@link Maybe#nothing}.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    @Override
    default Maybe<? extends ImmutableNonEmptyVector<A>> toNonEmpty() {
        return ImmutableVectors.tryNonEmptyConvert(this);
    }

    /**
     * Attempts to convert this {@link ImmutableVector} to a {@link ImmutableNonEmptyVector}.
     * If successful, returns a {@link ImmutableNonEmptyVector} containing the same elements as this one.
     * Use this if you are confident that this {@link ImmutableVector} is not empty.
     * <p>
     * If this {@link ImmutableVector} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    @Override
    default ImmutableNonEmptyVector<A> toNonEmptyOrThrow() {
        return ImmutableVectors.nonEmptyConvertOrThrow(this);
    }

}
