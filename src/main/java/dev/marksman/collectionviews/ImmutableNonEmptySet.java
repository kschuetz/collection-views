package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import dev.marksman.enhancediterables.ImmutableNonEmptyFiniteIterable;

import static com.jnape.palatable.lambda.adt.Maybe.just;

/**
 * A {@code Set} that is guaranteed at compile-time to be non-empty and safe from mutation anywhere.
 * In other words, it owns the sole reference to the underlying collection.
 * <p>
 * Provides all the guarantees of {@link Set}, {@link NonEmptySet}, and {@link ImmutableSet}.
 *
 * @param <A> the element type
 */
public interface ImmutableNonEmptySet<A> extends NonEmptySet<A>, ImmutableSet<A>, ImmutableNonEmptyFiniteIterable<A> {

    /**
     * Returns an {@code ImmutableNonEmptySet} containing the same elements as this one.
     * <p>
     * Since this is an {@link ImmutableNonEmptySet} already, this method simply returns
     * itself.
     *
     * @return itself
     */
    @Override
    default ImmutableNonEmptySet<A> toImmutable() {
        return this;
    }

    /**
     * Attempts to convert this {@code ImmutableSet} to an {@code ImmutableNonEmptySet}.
     * <p>
     * Since this will always be successful for {@link ImmutableNonEmptySet}s,
     * this method always returns itself wrapped in a {@link Maybe#just}.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return this {@code ImmutableNonEmptySet} wrapped in a {@link Maybe#just}
     */
    @Override
    default Maybe<? extends ImmutableNonEmptySet<A>> toNonEmpty() {
        return just(this);
    }

    /**
     * Attempts to convert this {@code ImmutableSet} to a {@code ImmutableNonEmptySet}.
     * <p>
     * Since this will always be successful for {@link ImmutableNonEmptySet}s,
     * this method always returns itself.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return this {@code ImmutableNonEmptySet}
     */
    @Override
    default ImmutableNonEmptySet<A> toNonEmptyOrThrow() {
        return this;
    }

    /**
     * Creates a {@code ImmutableNonEmptySet} with the given elements.
     *
     * @param first the first element
     * @param more  the remaining elements
     * @param <A>   the element type
     * @return an {@code ImmutableNonEmptySet<A>}
     */
    @SafeVarargs
    static <A> ImmutableNonEmptySet<A> of(A first, A... more) {
        return Sets.nonEmptySetOf(first, more);
    }

}
