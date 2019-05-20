package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import dev.marksman.enhancediterables.ImmutableFiniteIterable;
import dev.marksman.enhancediterables.NonEmptyFiniteIterable;
import dev.marksman.enhancediterables.NonEmptyIterable;

/**
 * A {@code Vector} that is guaranteed at compile-time to be safe from mutation anywhere.
 * In other words, it owns the sole reference to the underlying collection.
 * <p>
 * In addition to guarantees of {@link Vector}, provides the following benefits :
 * <ul>
 * <li>{@link ImmutableVector#fmap} always returns a {@code ImmutableVector}.</li>
 * <li>{@link ImmutableVector#take} always returns a {@code ImmutableVector}.</li>
 * <li>{@link ImmutableVector#drop} always returns a {@code ImmutableVector}.</li>
 * <li>{@link ImmutableVector#slice} always returns a {@code ImmutableVector}.</li>
 * <li>{@link ImmutableVector#reverse} always returns a {@code ImmutableVector}.</li>
 * <li>{@link ImmutableVector#zipWithIndex} always returns a {@code ImmutableVector}.</li>
 * <li>{@link ImmutableVector#toImmutable} always returns itself.</li>
 * </ul>
 *
 * @param <A> the element type
 */
public interface ImmutableVector<A> extends Vector<A>, ImmutableFiniteIterable<A>, Immutable {

    /**
     * Returns the cartesian product of this {@code ImmutableVector} with another {@code ImmutableVector}.
     * <p>
     * Does not make copies of any underlying collections.
     * <p>
     * The returned {@link ImmutableVector} will have a size of {@code size()} × {@code other.size()},
     * but will allocate no extra memory (aside from a few bytes for housekeeping).
     *
     * @param other an {@code ImmutableVector} of any type
     * @param <B>   the type of the other {@code ImmutableVector}
     * @return a {@code ImmutableVector<Tuple2<A, B>>}
     */
    default <B> ImmutableVector<Tuple2<A, B>> cross(ImmutableVector<B> other) {
        return ImmutableVectors.cross(this, other);
    }

    /**
     * Returns a new {@code ImmutableVector} that drops the first {@code count} elements of this {@code ImmutableVector}.
     * <p>
     * Does not make copies of any underlying collections.
     * <p>
     * Use caution when taking a small slice of a huge {@link ImmutableVector} that you no longer need.
     * The smaller slice will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     *
     * @param count the number of elements to drop from this {@code ImmutableVector}.
     *              Must be &gt;= 0.
     *              May exceed size of this {@code ImmutableVector}, in which case, the result will be an
     *              empty {@code ImmutableVector}.
     * @return an {@code ImmutableVector<A>}
     */
    @Override
    default ImmutableVector<A> drop(int count) {
        return ImmutableVectors.drop(count, this);
    }

    /**
     * Returns a new {@code ImmutableVector} that drops all except the last {@code count} elements of this {@code ImmutableVector}.
     * <p>
     * Does not make copies of any underlying collections.
     * <p>
     * Use caution when taking a small slice of a huge {@code Vector} that you no longer need.
     * The smaller slice will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     *
     * @param count the number of elements to drop from the end of this {@code ImmutableVector}.
     *              Must be &gt;= 0.
     *              May exceed size of this {@code ImmutableVector}, in which case, the result will be an
     *              empty {@code ImmutableVector}.
     * @return a {@code ImmutableVector<A>}
     */
    @Override
    default ImmutableVector<A> dropRight(int count) {
        return ImmutableVectors.dropRight(count, this);
    }

    /**
     * Returns a new {@code ImmutableVector} that drops longest prefix of elements of this {@code ImmutableVector} that satisfy a predicate.
     * <p>
     * Does not make copies of any underlying collections.
     *
     * @param predicate a predicate; not null.
     *                  This function should be referentially transparent and not perform side-effects.
     *                  It may be called zero or more times for each element.
     * @return an {@code ImmutableVector<A>}
     */
    @Override
    default ImmutableVector<A> dropWhile(Fn1<? super A, ? extends Boolean> predicate) {
        return ImmutableVectors.dropWhile(predicate, this);
    }

    /**
     * Maps a function over this {@code ImmutableVector}.
     * <p>
     * Returns a new {@link ImmutableVector} of the same size (but possibly a different type).
     * <p>
     * Does not make any copies of underlying collections.
     * <p>
     * This method is stack-safe, so a {@code ImmutableVector} can be mapped as many times as the heap permits.
     *
     * @param f   a function from {@code A} to {@code B}.
     *            Not null.
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
     * Returns a {@code NonEmptyIterable} containing the inits of this {@code ImmutableVector}.
     * <p>
     * The first value will be this {@code ImmutableVector} and the final one will be an empty {@code Vector},
     * with the intervening values the results of successive applications of {@code init}.
     *
     * @return a {@code NonEmptyIterable} over all the inits of this {@code ImmutableVector}
     */
    @Override
    default NonEmptyIterable<? extends ImmutableVector<A>> inits() {
        return ImmutableVectors.inits(this);
    }

    /**
     * Creates an {@code ImmutableVector} with this {@code ImmutableVector}'s elements in reversed order.
     * <p>
     * Does not make copies of any underlying collections.
     *
     * @return an {@code ImmutableVector<A>}
     */
    @Override
    default ImmutableVector<A> reverse() {
        return ImmutableVectors.reverse(this);
    }

    /**
     * Creates a slice of this {@code ImmutableVector}.
     * <p>
     * Does not make copies of any underlying collections.
     * <p>
     * Use caution when taking a small slice of a huge {@code ImmutableVector} that you no longer need.
     * The smaller slice will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     * To avoid this situation, use {@link Vector#copySliceFrom} instead.
     *
     * @param startIndex        the index of the element to begin the slice.
     *                          Must be &gt;= 0.
     *                          May exceed the size of this {@code ImmutableVector}, in which case an empty {@code ImmutableVector} will be returned.
     * @param endIndexExclusive the end index (exclusive) of the slice.  Must be &gt;= {@code startIndex}.
     *                          May exceed the size of this {@code ImmutableVector}, in which case the slice will
     *                          contain as many elements as available.
     * @return an {@code ImmutableVector<A>}
     */
    @Override
    default ImmutableVector<A> slice(int startIndex, int endIndexExclusive) {
        return ImmutableVectors.slice(startIndex, endIndexExclusive, this);
    }

    /**
     * Splits this {@code ImmutableVector} into a prefix/suffix pair according to a predicate.
     * <p>
     * Does not make copies of any underlying collections.
     * <p>
     * Note that <code>vector.span(p)</code> is equivalent to, but possibly more efficient than
     * <code>tuple(vector.takeWhile(p), vector.dropWhile(p))</code>
     *
     * @param predicate a predicate; not null.
     *                  This function should be referentially transparent and not perform side-effects.
     *                  It may be called zero or more times for each element.
     * @return a {@code Tuple2} contains of {@code ImmutableVector}s, one of which containing the first {@code index} elements
     * that satisfied the predicate, the second containing the other elements.
     */
    @Override
    default Tuple2<ImmutableVector<A>, ImmutableVector<A>> span(Fn1<? super A, ? extends Boolean> predicate) {
        return ImmutableVectors.span(predicate, this);
    }

    /**
     * Splits this {@code ImmutableVector} into two at a given position.
     * <p>
     * Does not make copies of any underlying collections.
     * <p>
     * Note that <code>vector.splitAt(n)</code> is equivalent to, but possibly more efficient than
     * <code>tuple(vector.take(n), vector.drop(n))</code>
     *
     * @param index the position at which to split.
     *              Must be &gt;= 0;
     * @return a {@code Tuple2} contains of {@code ImmutableVector}s, one of which containing the first {@code index} elements,
     * the second containing the other elements.
     */
    @Override
    default Tuple2<ImmutableVector<A>, ImmutableVector<A>> splitAt(int index) {
        return ImmutableVectors.splitAt(index, this);
    }

    /**
     * Returns a {@code NonEmptyIterable} containing the tails of this {@code ImmutableVector}.
     * <p>
     * The first value will be this {@code ImmutableVector} and the final one will be an empty {@code ImmutableVector},
     * with the intervening values the results of successive applications of {@code tail}.
     *
     * @return a {@code NonEmptyIterable} over all the tails of this {@code ImmutableVector}
     */
    @Override
    default NonEmptyFiniteIterable<ImmutableVector<A>> tails() {
        return ImmutableVectors.tails(this);
    }

    /**
     * Returns a new {@code ImmutableVector} containing at most the first {@code count} elements of this {@code ImmutableVector}.
     * <p>
     * Does not make copies of any underlying collections.
     * <p>
     * Use caution when taking a small slice of a huge {@link ImmutableVector} that you no longer need.
     * The smaller slice will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     * To avoid this situation, use {@link Vector#copyFrom(int, Iterable)} instead.
     *
     * @param count the maximum number of elements to take from this {@code ImmutableVector}.
     *              Must be &gt;= 0.
     *              May exceed size of this {@code ImmutableVector}.
     * @return an {@code ImmutableVector<A>}
     */
    @Override
    default ImmutableVector<A> take(int count) {
        return ImmutableVectors.take(count, this);
    }

    /**
     * Returns a new {@code ImmutableVector} containing at most the last {@code count} elements of this {@code ImmutableVector}.
     * <p>
     * Does not make copies of any underlying collections.
     * <p>
     * Use caution when taking a small slice of a huge {@link ImmutableVector} that you no longer need.
     * The smaller slice will hold onto a reference of the larger one, and will prevent it from being GC'ed.
     * To avoid this situation, use {@link Vector#copyFrom(int, Iterable)} instead.
     *
     * @param count the maximum number of elements to take from this {@code ImmutableVector}.
     *              Must be &gt;= 0.
     *              May exceed size of this {@code Vector}.
     * @return an {@code ImmutableVector<A>}
     */
    @Override
    default ImmutableVector<A> takeRight(int count) {
        return ImmutableVectors.takeRight(count, this);
    }

    /**
     * Returns a new {@code ImmutableVector} containing the longest prefix of elements this {@code ImmutableVector} that satisfy a predicate.
     * <p>
     * Does not make copies of any underlying collections.
     *
     * @param predicate a predicate; not null.
     *                  This function should be referentially transparent and not perform side-effects.
     *                  It may be called zero or more times for each element.
     * @return an {@code ImmutableVector<A>}
     */
    @Override
    default ImmutableVector<A> takeWhile(Fn1<? super A, ? extends Boolean> predicate) {
        return ImmutableVectors.takeWhile(predicate, this);
    }

    /**
     * Returns an {@code ImmutableVector} containing the same elements as this one.
     * <p>
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
     * Attempts to convert this {@code ImmutableVector} to an {@code ImmutableNonEmptyVector}.
     * <p>
     * If successful, returns a {@link ImmutableNonEmptyVector} containing the same elements as this one, wrapped in a {@link Maybe#just}.
     * <p>
     * If this {@code ImmutableVector} is empty, returns {@link Maybe#nothing}.
     * <p>
     * Does not make copies of any underlying collections.
     *
     * @return a {@code Maybe<ImmutableNonEmptyVector<A>>}
     */
    @Override
    default Maybe<? extends ImmutableNonEmptyVector<A>> toNonEmpty() {
        return ImmutableVectors.maybeNonEmptyConvert(this);
    }

    /**
     * Attempts to convert this {@code ImmutableVector} to an {@code ImmutableNonEmptyVector}.
     * <p>
     * If successful, returns a {@link ImmutableNonEmptyVector} containing the same elements as this one.
     * Use this if you are confident that this {@link ImmutableVector} is not empty.
     * <p>
     * If this {@code ImmutableVector} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make copies of any underlying collections.
     *
     * @return an {@code ImmutableNonEmptyVector<A>}
     * @throws IllegalArgumentException if this {@code ImmutableVector} is empty
     */
    @Override
    default ImmutableNonEmptyVector<A> toNonEmptyOrThrow() {
        return ImmutableVectors.nonEmptyConvertOrThrow(this);
    }

    /**
     * Zips together this {@code ImmutableVector} with another {@code ImmutableVector} by applying a zipping function.
     * <p>
     * Applies the function to the successive elements of of each {@code ImmutableVector} until one of them runs out of elements.
     * <p>
     * Does not make copies of any underlying collections.
     *
     * @param fn    The zipping function.
     *              Not null.
     *              This function should be referentially transparent and not perform side-effects.
     *              It may be called zero or more times for each element.
     * @param other The other {@code ImmutableVector}
     * @param <B>   The element type of the other {@code ImmutableVector}
     * @param <C>   The element type of the result
     * @return An {@code ImmutableVector<C>}
     */
    default <B, C> ImmutableVector<C> zipWith(Fn2<A, B, C> fn, ImmutableVector<B> other) {
        return ImmutableVectors.zipWith(fn, this, other);
    }

    /**
     * Zips this {@code ImmutableVector} with its indices.
     * <p>
     * Does not make copies of any underlying collections.
     *
     * @return a new {@code ImmutableVector} containing pairs consisting of all elements of this {@code ImmutableVector} paired with their index.
     * Indices start at 0.
     */
    @Override
    default ImmutableVector<Tuple2<A, Integer>> zipWithIndex() {
        return ImmutableVectors.zipWithIndex(this);
    }

}
