package dev.marksman.collectionviews;

import dev.marksman.enhancediterables.FiniteIterable;
import dev.marksman.enhancediterables.NonEmptyFiniteIterable;

import java.util.Collection;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static dev.marksman.collectionviews.EmptyVectorBuilder.emptyVectorBuilder;

/**
 * A builder for {@code ImmutableVector}s.
 * <p>
 * A {@code VectorBuilder} is immutable and all add operations return a new {@code VectorBuilder}.
 * To construct the {@link ImmutableVector}, call {@link VectorBuilder#build}.
 * <p>
 * It is safe to continue adding to a {@code VectorBuilder} even after {@code build} is called.
 * It is also safe to "fork" a {@code VectorBuilder}, but be aware that internal copies may be
 * made when doing so.
 *
 * @param <A> the element type
 */
public interface VectorBuilder<A> {

    /**
     * Adds an element to this {@code VectorBuilder}.
     *
     * @param element the element to add
     * @return a new {@code NonEmptyVectorBuilder} with the element added
     */
    NonEmptyVectorBuilder<A> add(A element);

    /**
     * Adds all elements in a {@code java.util.Collection} to this {@code VectorBuilder}.
     *
     * @param elements the collection to add
     * @return a new {@code VectorBuilder} with the elements added
     */
    VectorBuilder<A> addAll(Collection<A> elements);

    /**
     * Adds all elements in a {@code FiniteIterable} to this {@code VectorBuilder}.
     * <p>
     * Since {@link Vector}s are {@code FiniteIterable}s, this method accepts any {@code Vector}.
     *
     * @param elements the {@code FiniteIterable} to add
     * @return a new {@code VectorBuilder} with the elements added
     */
    VectorBuilder<A> addAll(FiniteIterable<A> elements);

    /**
     * Adds all elements in a {@code NonEmptyFiniteIterable} to this {@code VectorBuilder}.
     * <p>
     * Since {@link NonEmptyVector}s are {@code NonEmptyFiniteIterable}s, this method accepts any {@code NonEmptyVector}.
     *
     * @param elements the {@code NonEmptyFiniteIterable} to add
     * @return a new {@code NonEmptyVectorBuilder} with the elements added
     */
    NonEmptyVectorBuilder<A> addAll(NonEmptyFiniteIterable<A> elements);

    /**
     * Builds a new {@code ImmutableVector}.
     * <p>
     * It is safe to call this at any point, and it safe to continue adding elements after calling this.
     *
     * @return an {@code ImmutableVector}.
     */
    ImmutableVector<A> build();

    /**
     * Creates a new {@code VectorBuilder}.
     *
     * @param <A> the element type
     * @return an empty {@link VectorBuilder}
     */
    static <A> VectorBuilder<A> builder() {
        return emptyVectorBuilder(nothing());
    }

    /**
     * Creates a new {@code VectorBuilder} with an initial capacity hint.
     *
     * @param initialCapacity an initial capacity hint.
     *                        Must be &gt;= 0.
     * @param <A>             the element type
     * @return an empty {@link VectorBuilder}
     */
    static <A> VectorBuilder<A> builder(int initialCapacity) {
        return emptyVectorBuilder(just(initialCapacity));
    }

}
