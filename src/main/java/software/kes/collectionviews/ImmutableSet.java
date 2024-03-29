package software.kes.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import software.kes.enhancediterables.ImmutableFiniteIterable;

/**
 * A {@code Set} that is guaranteed at compile-time to be safe from mutation anywhere.
 * In other words, it owns the sole reference to the underlying collection.
 * <p>
 * In addition to guarantees of {@link Set}, an {@code ImmutableSet} provides the following benefits:
 * <ul>
 * <li>{@link ImmutableSet#toImmutable} always returns itself.</li>
 * </ul>
 *
 * @param <A> the element type
 */
public interface ImmutableSet<A> extends Set<A>, ImmutableFiniteIterable<A>, Immutable {

    /**
     * Since an {@code ImmutableSet} already contains only distinct values, this method always
     * returns itself.
     *
     * @return itself
     */
    @Override
    default ImmutableSet<A> distinct() {
        return this;
    }

    /**
     * Returns an {@code ImmutableSet} containing the same elements as this one.
     * <p>
     * Since this is an {@link ImmutableSet} already, this method simply returns
     * itself.
     *
     * @return itself
     */
    @Override
    default ImmutableSet<A> toImmutable() {
        return this;
    }

    /**
     * Attempts to convert this {@code ImmutableSet} to an {@code ImmutableNonEmptySet}.
     * <p>
     * If successful, returns a {@link ImmutableNonEmptySet} containing the same elements as this one, wrapped in a {@link Maybe#just}.
     * <p>
     * If this {@code ImmutableSet} is empty, returns {@link Maybe#nothing}.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a {@code Maybe<ImmutableNonEmptySet<A>>}
     */
    @Override
    default Maybe<? extends ImmutableNonEmptySet<A>> toNonEmpty() {
        return ImmutableSets.maybeNonEmptyConvert(this);
    }

    /**
     * Attempts to convert this {@code ImmutableSet} to an {@code ImmutableNonEmptySet}.
     * <p>
     * If successful, returns a {@link ImmutableNonEmptySet} containing the same elements as this one.
     * Use this if you are confident that this {@link ImmutableSet} is not empty.
     * <p>
     * If this {@code ImmutableSet} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return an {@code ImmutableNonEmptySet<A>}
     * @throws IllegalArgumentException if this {@code ImmutableSet} is empty
     */
    @Override
    default ImmutableNonEmptySet<A> toNonEmptyOrThrow() {
        return ImmutableSets.nonEmptyConvertOrThrow(this);
    }

    /**
     * Creates a {@code ImmutableNonEmptySet} with the given elements.
     *
     * @param first the first element
     * @param more  the remaining elements
     * @param <A>   the element type
     * @return an {@code ImmutableNonEmptySet<A>}
     */
    @SuppressWarnings("varargs")
    @SafeVarargs
    static <A> ImmutableNonEmptySet<A> of(A first, A... more) {
        return Sets.nonEmptySetOf(first, more);
    }

}
