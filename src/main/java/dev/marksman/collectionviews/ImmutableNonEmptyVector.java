package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;

import static com.jnape.palatable.lambda.adt.Maybe.just;

/**
 * A {@code Vector} that is guaranteed at compile-time to be non-empty and safe from mutation anywhere.
 * In other words, it owns the sole reference to the underlying collection.
 * <p>
 * In addition to the guarantees of {@link Vector}, {@link NonEmptyVector}, and {@link ImmutableVector},
 * provides the following benefits:
 * <ul>
 * <li>{@code ImmutableNonEmptyVector#fmap} always returns a {@code ImmutableNonEmptyVector}.</li>
 * <li>{@code ImmutableNonEmptyVector#reverse} always returns a {@code ImmutableNonEmptyVector}.</li>
 * <li>{@code ImmutableNonEmptyVector#zipWithIndex} always returns a {@code ImmutableNonEmptyVector}.</li>
 * </ul>
 *
 * @param <A> the element type
 */
public interface ImmutableNonEmptyVector<A> extends NonEmptyVector<A>, ImmutableVector<A> {

    default <B> ImmutableNonEmptyVector<Tuple2<A, B>> cross(ImmutableNonEmptyVector<B> other) {
        return ImmutableVectors.nonEmptyCross(this, other);
    }

    /**
     * Maps a function over this {@code ImmutableNonEmptyVector}.
     * <p>
     * Returns a new {@link ImmutableNonEmptyVector} of the same size (but possibly a different type).
     * <p>
     * Does not make any copies of underlying data structures.
     * <p>
     * This method is stack-safe, so a {@code ImmutableNonEmptyVector} can be mapped as many times as the heap permits.
     *
     * @param f   a function from {@code A} to {@code B}.
     *            Not null.
     *            This function should be referentially transparent and not perform side-effects.
     *            It may be called zero or more times for each element.
     * @param <B> The type of the elements contained in the output Vector.
     * @return an {@code ImmutableNonEmptyVector<B>} of the same size
     */
    @Override
    default <B> ImmutableNonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return ImmutableVectors.nonEmptyMap(f, this);
    }

    /**
     * Creates an {@code ImmutableNonEmptyVector} with this {@code ImmutableNonEmptyVector}'s elements in reversed order.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return an {@code ImmutableNonEmptyVector<A>}
     */
    @Override
    default ImmutableNonEmptyVector<A> reverse() {
        return ImmutableVectors.nonEmptyReverse(this);
    }

    /**
     * Returns the tail of this {@code ImmutableNonEmptyVector}.
     * <p>
     * The tail of an {@link ImmutableNonEmptyVector} is the same {@code ImmutableNonEmptyVector} with the first element dropped.
     * May be empty.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return an {@code ImmutableVector<A>}
     */
    @Override
    default ImmutableVector<A> tail() {
        return drop(1);
    }

    /**
     * Returns an {@code ImmutableNonEmptyVector} containing the same elements as this one.
     * <p>
     * Since this is an {@link ImmutableNonEmptyVector} already, this method simply returns
     * itself.
     *
     * @return itself
     */
    @Override
    default ImmutableNonEmptyVector<A> toImmutable() {
        return this;
    }

    /**
     * Attempts to convert this {@code ImmutableVector} to an {@code ImmutableNonEmptyVector}.
     * <p>
     * Since this will always be successful for {@link ImmutableNonEmptyVector}s,
     * this method always returns itself wrapped in a {@link Maybe#just}.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return this {@code ImmutableNonEmptyVector} wrapped in a {@link Maybe#just}
     */
    @Override
    default Maybe<? extends ImmutableNonEmptyVector<A>> toNonEmpty() {
        return just(this);
    }

    /**
     * Attempts to convert this {@code ImmutableVector} to a {@code ImmutableNonEmptyVector}.
     * <p>
     * Since this will always be successful for {@link ImmutableNonEmptyVector}s,
     * this method always returns itself.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return this {@code ImmutableNonEmptyVector}
     */
    @Override
    default ImmutableNonEmptyVector<A> toNonEmptyOrThrow() {
        return this;
    }

    default <B, R> ImmutableNonEmptyVector<R> zipWith(Fn2<A, B, R> fn, ImmutableNonEmptyVector<B> other) {
        return ImmutableVectors.nonEmptyZipWith(fn, this, other);
    }

    /**
     * Zips this {@code ImmutableNonEmptyVector} with its indices.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a new {@code ImmutableNonEmptyVector} containing pairs consisting of all elements of this {@code ImmutableNonEmptyVector} paired with their index.
     * Indices start at 0.
     */
    @Override
    default ImmutableNonEmptyVector<Tuple2<A, Integer>> zipWithIndex() {
        return ImmutableVectors.nonEmptyZipWithIndex(this);
    }

}
