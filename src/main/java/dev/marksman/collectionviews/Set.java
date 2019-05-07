package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.builtin.fn2.Find;

/**
 * A finite, unordered view of a collection that contains no duplicate elements.
 * <p>
 * A {@code Set} guarantees the following:
 * <ul>
 * <li>A {@link Set#size} method that executes in O(1).
 * <li>A {@link Set#contains} method that tests an value for membership in O(1).
 * <li>An {@link Set#isEmpty} method that executes in O(1).
 * <li>Iteration will always terminate.
 * <li>Protected from mutation by the bearer.
 * <li>The bearer cannot gain access to a reference to the underlying collection.
 * </ul>
 *
 * @param <A> the element type
 */
public interface Set<A> extends Iterable<A> {

    /**
     * Tests if an element is a member of this {@code Set}.
     *
     * @param element the element to test
     * @return true if {@code element} is a member of this {@link Set}, false otherwise
     */
    boolean contains(A element);

    /**
     * Returns the size of this {@code Set}.
     * <p>
     * Executes in O(1).
     *
     * @return the number of elements in this {@link Set}
     */
    int size();

    /**
     * Finds an element of this {@code Set} that satisfies a predicate, if any.
     *
     * @param predicate a predicate; not null
     * @return an element wrapped in a {@link Maybe#just} if a matching element is found;
     * {@link Maybe#nothing} otherwise.
     */
    default Maybe<A> find(Fn1<? super A, ? extends Boolean> predicate) {
        return Find.find(predicate, this);
    }

    /**
     * Tests whether this {@link Set} is empty.
     * <p>
     * Executes in O(1).
     *
     * @return true if this {@link Set} is empty, false otherwise.
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Converts this {@code Set} to an {@code ImmutableSet}.
     * <p>
     * This method will make a copy of the underlying data structure if necessary to guarantee immutability.
     * <p>
     * If this {@link Set} is already an {@link ImmutableSet}, no copies are made and this method is a no-op.
     *
     * @return an {@code ImmutableSet} of the same type and containing the same elements
     */
    default ImmutableSet<A> toImmutable() {
        return ImmutableSets.ensureImmutable(this);
    }

    /**
     * Attempts to convert this {@code Set} to a {@code NonEmptySet}.
     * <p>
     * If successful, returns a {@link NonEmptySet} containing the same elements as this one, wrapped in a {@link Maybe#just}.
     * <p>
     * If this {@code Set} is empty, returns {@link Maybe#nothing}.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a {@code Maybe<NonEmptySet<A>>}
     */
    default Maybe<? extends NonEmptySet<A>> toNonEmpty() {
        return Sets.maybeNonEmptyWrap(this);
    }

    /**
     * Attempts to convert this {@code Set} to a {@code NonEmptySet}.
     * <p>
     * If successful, returns a {@link NonEmptySet} containing the same elements as this one.
     * Use this if you are confident that this {@link Set} is not empty.
     * <p>
     * If this {@code Set} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return a {@code NonEmptySet<A>}
     * @throws IllegalArgumentException if this {@code Set} is empty
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

    /**
     * Creates a {@code Set} that wraps a {@code java.util.Set}.
     * <p>
     * Does not make any copies of the given {@link java.util.Set}.
     * The created {@link Set} will hold a reference to the given underlying {@code Set}, but will not alter it in any way.
     * <p>
     * Bearers of the created {@code Set} will be unable to gain access to the underlying {@code Set}, so it is safe to share.
     * <p>
     * Since no copy is made, be aware that anyone that holds a direct reference to the underlying {@code Set} can still mutate it.
     * Mutating the underlying {@code Set} is not advised.
     * Operations that change the size of the underlying {@code Set} will result in unpredictable behavior.
     * Use {@link Set#copyFrom} if you want to avoid this situation.
     *
     * @param underlying {@code Set} to wrap; not null
     * @param <A>        the element type
     * @return a {@code Set<A>}
     */
    static <A> Set<A> wrap(java.util.Set<A> underlying) {
        return Sets.wrap(underlying);
    }

    /**
     * Creates an {@code ImmutableSet} that is copied from any {@code Iterable}.
     * <p>
     * The entire {@link Iterable} will be eagerly iterated.
     * Be careful not to pass in an infinite {@code Iterable} or this method will not terminate.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If {@code source} already is an {@link ImmutableSet}, it will be returned directly.
     *
     * @param source an {@code Iterable<A>} that will be iterated eagerly in its entirety;  not null
     * @param <A>    the element type
     * @return an {@code ImmutableSet<A>}
     */
    static <A> ImmutableSet<A> copyFrom(Iterable<A> source) {
        return ImmutableSets.copyFrom(source);
    }

    /**
     * Creates an {@code ImmutableSet} that is copied from an array.
     *
     * @param source the array to copy from.
     *               Not null.
     *               This method will not alter or hold on to a reference of this array.
     * @param <A>    the element type
     * @return an {@code ImmutableSet<A>}
     */
    static <A> ImmutableSet<A> copyFrom(A[] source) {
        return ImmutableSets.copyFrom(source);
    }

    /**
     * Creates an {@code ImmutableVector} that is copied from any {@code Iterable}, but consuming a maximum number of elements.
     * <p>
     * The {@link Iterable} will be eagerly iterated, but only up to a maximum of {@code maxCount} elements.
     * If {@code maxCount} elements are not available, then the all of the elements available will be returned.
     * <p>
     * This method will make a copy of the data provided, unless {@code source} is
     * an {@link ImmutableSet} and its size equal to {@code maxCount},
     * in which case it will be returned directly.
     *
     * @param maxCount the maximum number of elements to consume from the source.
     *                 Must be &gt;= 0.
     * @param source   an {@code Iterable<A>} that will be iterated eagerly for up to {@code maxCount} elements.
     *                 Not null.
     *                 It is safe for {@code source} to be infinite.
     * @param <A>      the element type
     * @return an {@code ImmutableVector} that contains at most {@code maxCount} elements
     */
    static <A> ImmutableSet<A> copyFrom(int maxCount, Iterable<A> source) {
        return ImmutableSets.copyFrom(maxCount, source);
    }

    /**
     * Returns a new {@code ImmutableSet} that is copied from an array.
     *
     * @param maxCount the maximum number of elements to copy from the array.
     *                 Must be &gt;= 0.
     * @param source   the array to copy from.
     *                 Not null.
     *                 This method will not alter or hold on to a reference of this array.
     * @param <A>      the element type
     * @return an {@code ImmutableSet<A>}
     */
    static <A> ImmutableSet<A> copyFrom(int maxCount, A[] source) {
        return ImmutableSets.copyFrom(maxCount, source);
    }

}
