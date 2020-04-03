package dev.marksman.collectionviews;

import dev.marksman.enhancediterables.FiniteIterable;

import java.util.Collection;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static dev.marksman.collectionviews.ConcreteSetBuilder.concreteSetBuilder;

/**
 * A builder for {@code ImmutableNonEmptySet}s.
 * <p>
 * A {@code NonEmptySetBuilder} is immutable and all add operations return a new {@code SetBuilder}.
 * To construct the {@link ImmutableNonEmptySet}, call {@link NonEmptySetBuilder#build}.
 * <p>
 * It is safe to continue adding to a {@code NonEmptySetBuilder} even after {@code build} is called.
 * It is also safe to "fork" a {@code NonEmptySetBuilder}, but be aware that internal copies may be
 * made when doing so.
 *
 * @param <A> the element type
 */
public interface NonEmptySetBuilder<A> extends SetBuilder<A> {

    /**
     * Adds all elements in a {@code java.util.Collection} to this {@code NonEmptySetBuilder}.
     *
     * @param elements the collection to add
     * @return a new {@code NonEmptySetBuilder} with the elements added
     */
    @Override
    NonEmptySetBuilder<A> addAll(Collection<A> elements);

    /**
     * Adds all elements in a {@code FiniteIterable} to this {@code NonEmptySetBuilder}.
     * <p>
     * Since {@link Set}s are {@code FiniteIterable}s, this method accepts any {@code Set}.
     *
     * @param elements the {@code FiniteIterable} to add
     * @return a new {@code NonEmptySetBuilder} with the elements added
     */
    @Override
    NonEmptySetBuilder<A> addAll(FiniteIterable<A> elements);

    /**
     * Builds a new {@code ImmutableNonEmptySet}.
     * <p>
     * It is safe to call this at any point, and it safe to continue adding elements after calling this.
     *
     * @return an {@code ImmutableNonEmptySet}.
     */
    @Override
    ImmutableNonEmptySet<A> build();

    /**
     * Creates a new {@code NonEmptySetBuilder}.
     *
     * @param first the first element
     * @param <A>   the element type
     * @return an empty {@link SetBuilder}
     */
    static <A> NonEmptySetBuilder<A> builder(A first) {
        return concreteSetBuilder(nothing(), first);
    }

    /**
     * Creates a new {@code NonEmptySetBuilder} with an initial capacity hint.
     *
     * @param initialCapacity an initial capacity hint.
     *                        Must be &gt;= 0.
     * @param first           the first element
     * @param <A>             the element type
     * @return an empty {@link SetBuilder}
     */
    static <A> NonEmptySetBuilder<A> builder(int initialCapacity, A first) {
        return concreteSetBuilder(just(initialCapacity), first);
    }

}
