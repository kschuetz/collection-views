package software.kes.collectionviews;

import software.kes.enhancediterables.FiniteIterable;

import java.util.Collection;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;

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

    /**
     * Creates a new {@code NonEmptyVectorBuilder}.
     *
     * @param first the first element
     * @param <A>   the element type
     * @return an empty {@link VectorBuilder}
     */
    static <A> NonEmptyVectorBuilder<A> builder(A first) {
        return ConcreteVectorBuilder.concreteVectorBuilder(nothing(), first);
    }

    /**
     * Creates a new {@code NonEmptyVectorBuilder} with an initial capacity hint.
     *
     * @param initialCapacity an initial capacity hint.
     *                        Must be &gt;= 0.
     * @param first           the first element
     * @param <A>             the element type
     * @return an empty {@link VectorBuilder}
     */
    static <A> NonEmptyVectorBuilder<A> builder(int initialCapacity, A first) {
        return ConcreteVectorBuilder.concreteVectorBuilder(just(initialCapacity), first);
    }

}
