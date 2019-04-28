package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;

public interface Set<A> extends Iterable<A> {
    int size();

    boolean contains(A element);

    default boolean isEmpty() {
        return size() == 0;
    }

    static <A> Set<A> empty() {
        return Sets.empty();
    }

    /**
     * Returns an {@link ImmutableSet} containing the same elements as this one.
     * This method will make a copy of the underlying data structure if necessary to
     * guarantee immutability.
     * <p>
     * If this {@link Set} is an {@link ImmutableSet} already, no copies are made
     * and this method is a no-op.
     *
     * @return an {@link ImmutableSet} of the same type and containing the same elements
     */
    default ImmutableSet<A> toImmutable() {
        return Sets.ensureImmutable(this);
    }

    /**
     * Returns a {@link NonEmptySet} containing the same elements as this one,
     * wrapped in a {@link Maybe#just}, if this {@link Vector} is not empty.
     * <p>
     * If this {@link Set} is empty, returns {@link Maybe#nothing}.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    default Maybe<? extends NonEmptySet<A>> toNonEmpty() {
        return Sets.tryNonEmptyWrap(this);
    }

    /**
     * Returns a {@link NonEmptySet} containing the same elements as this one,
     * if this {@link Set} is not empty.  Use this if you are confident that this {@link Set}
     * is not empty.
     * <p>
     * If this {@link Set} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make copies of any underlying data structures.
     */
    default NonEmptySet<A> toNonEmptyOrThrow() {
        return Sets.nonEmptyWrapOrThrow(this);
    }

    /**
     * Wraps a {@link java.util.Set} in a {@link Set}.
     *
     * <p>
     * Does *not* make a copy of the given {@link java.util.Set}.
     * The {@link Set} will hold a reference to the underlying {@link java.util.Set}, but will not alter it in any way.
     * <p>
     * Since bearers of this {@link Set} will be unable to mutate or gain access to the underlying data,
     * it is safe to share.
     * <p>
     * Since this does not make a copy of the underlying data, be aware that anyone that holds a
     * direct reference to it the underlying {@link java.util.Set} can still mutate it.
     *
     * @param underlying
     * @param <A> the element type
     * @return
     */
    static <A> Set<A> wrap(java.util.Set<A> underlying) {
        return Sets.wrap(underlying);
    }

    @SafeVarargs
    static <A> ImmutableNonEmptySet<A> of(A first, A... more) {
        return Sets.nonEmptySetOf(first, more);
    }
}
