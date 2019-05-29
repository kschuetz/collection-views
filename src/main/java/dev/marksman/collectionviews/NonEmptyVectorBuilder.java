package dev.marksman.collectionviews;

import dev.marksman.enhancediterables.FiniteIterable;

import java.util.Collection;

/**
 * A builder for {@code ImmutableNonEmptyVector}s.
 * <p>
 * A {@code NonEmptyVectorBuilder} is immutable and all add operations return a new {@code VectorBuilder}.
 * To construct the {@link ImmutableNonEmptyVector}, call {@link NonEmptyVectorBuilder#build}.
 * <p>
 * It is safe to continue adding to a {@code NonEmptyVectorBuilder} even after {@code build} is called.
 * It is also safe to "fork" a {@code NonEmptyVectorBuilder}, but be aware that internal copies may be
 * made when doing so.
 *
 * @param <A> the element type
 */
public interface NonEmptyVectorBuilder<A> extends VectorBuilder<A> {

    /**
     * Adds all elements in a {@code java.util.Collection} to this {@code NonEmptyVectorBuilder}.
     *
     * @param elements the collection to add
     * @return a new {@code NonEmptyVectorBuilder} with the elements added
     */
    @Override
    NonEmptyVectorBuilder<A> addAll(Collection<A> elements);

    /**
     * Adds all elements in a {@code FiniteIterable} to this {@code NonEmptyVectorBuilder}.
     * <p>
     * Since {@link Vector}s are {@code FiniteIterable}s, this method accepts any {@code Vector}.
     *
     * @param elements the {@code FiniteIterable} to add
     * @return a new {@code NonEmptyVectorBuilder} with the elements added
     */
    @Override
    NonEmptyVectorBuilder<A> addAll(FiniteIterable<A> elements);

    /**
     * Builds a new {@code ImmutableNonEmptyVector}.
     * <p>
     * It is safe to call this at any point, and it safe to continue adding elements after calling this.
     *
     * @return an {@code ImmutableNonEmptyVector}.
     */
    @Override
    ImmutableNonEmptyVector<A> build();

}
