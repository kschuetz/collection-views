package software.kes.collectionviews;

import software.kes.enhancediterables.FiniteIterable;
import software.kes.enhancediterables.NonEmptyFiniteIterable;

import java.util.Collection;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static software.kes.collectionviews.EmptySetBuilder.emptySetBuilder;

/**
 * A builder for {@code ImmutableSet}s.
 * <p>
 * A {@code SetBuilder} is immutable and all add operations return a new {@code SetBuilder}.
 * To construct the {@link ImmutableSet}, call {@link SetBuilder#build}.
 * <p>
 * It is safe to continue adding to a {@code SetBuilder} even after {@code build} is called.
 * It is also safe to "fork" a {@code SetBuilder}, but be aware that internal copies may be
 * made when doing so.
 *
 * @param <A> the element type
 */
public interface SetBuilder<A> {

    /**
     * Adds an element to this {@code SetBuilder}.
     *
     * @param element the element to add
     * @return a new {@code NonEmptySetBuilder} with the element added
     */
    NonEmptySetBuilder<A> add(A element);

    /**
     * Adds all elements in a {@code java.util.Collection} to this {@code SetBuilder}.
     *
     * @param elements the collection to add
     * @return a new {@code SetBuilder} with the elements added
     */
    SetBuilder<A> addAll(Collection<A> elements);

    /**
     * Adds all elements in a {@code FiniteIterable} to this {@code SetBuilder}.
     * <p>
     * Since {@link Set}s are {@code FiniteIterable}s, this method accepts any {@code Set}.
     *
     * @param elements the {@code FiniteIterable} to add
     * @return a new {@code SetBuilder} with the elements added
     */
    SetBuilder<A> addAll(FiniteIterable<A> elements);

    /**
     * Adds all elements in a {@code NonEmptyFiniteIterable} to this {@code SetBuilder}.
     * <p>
     * Since {@link NonEmptyVector}s are {@code NonEmptyFiniteIterable}s, this method accepts any {@code NonEmptyVector}.
     *
     * @param elements the {@code NonEmptyFiniteIterable} to add
     * @return a new {@code NonEmptySetBuilder} with the elements added
     */
    NonEmptySetBuilder<A> addAll(NonEmptyFiniteIterable<A> elements);

    /**
     * Builds a new {@code ImmutableSet}.
     * <p>
     * It is safe to call this at any point, and it safe to continue adding elements after calling this.
     *
     * @return an {@code ImmutableSet}.
     */
    ImmutableSet<A> build();

    /**
     * Creates a new {@code SetBuilder}.
     *
     * @param <A> the element type
     * @return an empty {@link SetBuilder}
     */
    static <A> SetBuilder<A> builder() {
        return emptySetBuilder(nothing());
    }

    /**
     * Creates a new {@code SetBuilder} with an initial capacity hint.
     *
     * @param initialCapacity an initial capacity hint.
     *                        Must be &gt;= 0.
     * @param <A>             the element type
     * @return an empty {@link SetBuilder}
     */
    static <A> SetBuilder<A> builder(int initialCapacity) {
        return emptySetBuilder(just(initialCapacity));
    }

}
