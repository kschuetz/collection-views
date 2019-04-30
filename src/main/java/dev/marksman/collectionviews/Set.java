package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;

public interface Set<A> extends Iterable<A> {

    /**
     * Returns {@code true} if {@code element} is a member of this {@link Set}.
     *
     * @param element the element to test
     * @return {@code true} if {@code element} is a member of this {@link Set}, {@code false} otherwise
     */
    boolean contains(A element);

    /**
     * The size of the {@link Set}.  Executes in O(1).
     *
     * @return the number of elements in the {@link Set}.
     */
    int size();

    /**
     * Tests whether the {@link Set} is empty.  Executes in O(1).
     *
     * @return {@code true} if the {@link Set} is empty, {@code false} otherwise.
     */
    default boolean isEmpty() {
        return size() == 0;
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
        return ImmutableSets.ensureImmutable(this);
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
     * Returns an empty {@link ImmutableSet}.
     *
     * @param <A> the element type
     * @return an empty {@code ImmutableSet<A>}
     */
    static <A> ImmutableSet<A> empty() {
        return Sets.empty();
    }

    /**
     * Constructs a new {@link ImmutableNonEmptySet} with the given elements.
     *
     * @param first the first element
     * @param more  the remaining elements
     * @param <A>   the element type
     * @return an {@code ImmutableNonEmptySet<A>}, which is guaranteed to be non-empty and to be safe from mutation.
     */
    @SafeVarargs
    static <A> ImmutableNonEmptySet<A> of(A first, A... more) {
        return Sets.nonEmptySetOf(first, more);
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
     * @param underlying Set to wrap
     * @param <A>        the element type
     * @return a {@code Set<A>}
     */
    static <A> Set<A> wrap(java.util.Set<A> underlying) {
        return Sets.wrap(underlying);
    }

    /**
     * Constructs a new {@link ImmutableSet} from any {@link Iterable}.
     * <p>
     * The entire {@link Iterable} will be eagerly iterated, so be careful not
     * to pass in an infinite {@link Iterable} or this method will not terminate.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If {@code source} already is an {@link ImmutableSet}, it will be returned directly.
     *
     * @param source an {@code Iterable<A>} that will be iterated eagerly in its entirety
     * @param <A>    the element type
     * @return an {@code ImmutableSet<A>}
     */
    static <A> ImmutableSet<A> copyFrom(Iterable<A> source) {
        return ImmutableSets.copyFrom(source);
    }

    /**
     * Returns a new {@link ImmutableSet} that is copied from an array.
     *
     * @param source the array to copy from.
     *               This method will not alter or hold on to a reference of this array.
     * @param <A>    the element type
     * @return an {@code ImmutableSet<A>}
     */
    static <A> ImmutableSet<A> copyFrom(A[] source) {
        return ImmutableSets.copyFrom(source);
    }

    /**
     * Constructs a new {@link ImmutableSet} from any {@link Iterable}, but consuming a maximum number of elements.
     * <p>
     * The {@link Iterable} will be eagerly iterated, but only up to a maximum of {@code maxCount}
     * elements.  If {@code maxCount} elements are not available, then the all of the elements
     * available will be returned.
     * <p>
     * This method will make a copy of the data provided, unless {@code source} is
     * an {@link ImmutableSet} and its size is less than or equal to {@code maxCount},
     * in which case it will be returned directly.
     * <p>
     * If {@code source} is an {@link ImmutableSet} that is greater than {@code maxCount} in size,
     * a copy will always be made, therefore making it memory-safe to take a small slice of
     * a huge {@link Vector} that you no longer need.
     *
     * @param maxCount the maximum number of elements to consume from the source.  Must be &gt;= 0.
     * @param source   an {@code Iterable<A>} that will be iterated eagerly for up to {@code maxCount} elements.
     *                 It is safe for {@code source} to be infinite.
     * @param <A>      the element type
     * @return an {@link ImmutableSet} that contains at most {@code maxCount} elements.
     */
    static <A> ImmutableSet<A> copyFrom(int maxCount, Iterable<A> source) {
        return ImmutableSets.copyFrom(maxCount, source);
    }

    /**
     * Returns a new {@link ImmutableSet} that is copied from an array.
     *
     * @param maxCount the maximum number of elements to copy from the array. Must be &gt;= 0.
     * @param source   the array to copy from.
     *                 This method will not alter or hold on to a reference of this array.
     * @param <A>      the element type
     * @return an {@code ImmutableVector<A>}
     */
    static <A> ImmutableSet<A> copyFrom(int maxCount, A[] source) {
        return ImmutableSets.copyFrom(maxCount, source);
    }

}
