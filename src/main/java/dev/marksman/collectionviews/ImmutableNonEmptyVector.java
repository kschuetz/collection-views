package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;

import static com.jnape.palatable.lambda.adt.Maybe.just;

/**
 * A {@link Vector} that is guaranteed at compile-time to be non-empty, and safe from mutation anywhere.
 * In other words, it owns the sole reference to the underlying collection.
 * <p>
 * In addition to the guarantees of {@link Vector}, {@link NonEmptyVector}, and {@link ImmutableVector},
 * provides the following benefits:
 * <ul>
 * <li>{@link ImmutableNonEmptyVector#fmap} always returns a {@code ImmutableNonEmptyVector}.</li>
 * </ul>
 *
 * @param <A> the element type
 */
public interface ImmutableNonEmptyVector<A> extends NonEmptyVector<A>, ImmutableVector<A> {

    /**
     * Maps a function over the elements in an {@link ImmutableNonEmptyVector} and returns
     * a new {@link ImmutableNonEmptyVector} of the same size (but possibly a different type).
     * <p>
     * Does not make any copies of underlying data structures.
     * <p>
     * This method is stack-safe, so a Vector can be mapped as many times as the heap permits.
     *
     * @param f   a function from {@code A} to {@code B}.
     *            This function should be referentially transparent and not perform side-effects.
     *            It may be called zero or more times for each element.
     * @param <B> The type of the elements contained in the output Vector.
     * @return an {@code ImmutableNonEmptyVector<B>} of the same size
     */
    @Override
    default <B> ImmutableNonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return ImmutableVectors.mapNonEmpty(f, this);
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
     * Returns an {@link ImmutableVector} containing the same elements as this one.
     * Since this is an {@link ImmutableVector} already, this method simply returns
     * itself.
     *
     * @return itself
     */
    @Override
    default ImmutableNonEmptyVector<A> toImmutable() {
        return this;
    }

    /**
     * Attempts to convert this {@link Vector} to a {@link NonEmptyVector}.
     * Since this will always be successful for {@link ImmutableNonEmptyVector}s,
     * this method always returns itself wrapped in a {@link Maybe#just}.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    @Override
    default Maybe<? extends ImmutableNonEmptyVector<A>> toNonEmpty() {
        return just(this);
    }

    /**
     * Attempts to convert this {@link Vector} to a {@link NonEmptyVector}.
     * Since this will always be successful for {@link ImmutableNonEmptyVector}s,
     * this method always returns itself.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    @Override
    default ImmutableNonEmptyVector<A> toNonEmptyOrThrow() {
        return this;
    }

}
