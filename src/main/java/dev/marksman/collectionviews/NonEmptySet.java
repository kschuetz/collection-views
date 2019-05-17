package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import dev.marksman.enhancediterables.NonEmptyFiniteIterable;
import dev.marksman.enhancediterables.NonEmptyIterable;

import static com.jnape.palatable.lambda.adt.Maybe.just;

/**
 * A {@code Set} that is guaranteed at compile-time to contain at least one element.
 * <p>
 * In addition to guarantees of {@link Set}, provides the following benefits:
 * <ul>
 * <li>{@link NonEmptySet#head} method that returns an element.</li>
 * <li>Implements {@link NonEmptyIterable}.</li>
 * </ul>
 *
 * @param <A> the element type
 */
public interface NonEmptySet<A> extends NonEmptyFiniteIterable<A>, Set<A> {

    /**
     * Tests whether this {@code NonEmptySet} is empty.
     * <p>
     * Always returns false for {@link NonEmptySet}s.
     *
     * @return always false
     */
    @Override
    default boolean isEmpty() {
        return false;
    }

    /**
     * Converts this {@code NonEmptySet} to an {@code ImmutableNonEmptySet}.
     * <p>
     * This method will make a copy of the underlying data structure if necessary to guarantee immutability.
     * <p>
     * If this {@link NonEmptySet} is already an {@link ImmutableNonEmptySet}, no copies are made and this method is a no-op.
     *
     * @return an {@code ImmutableNonEmptySet} of the same type and containing the same elements
     */
    @Override
    default ImmutableNonEmptySet<A> toImmutable() {
        return ImmutableSets.ensureImmutable(this);
    }

    /**
     * Attempts to convert this {@code Set} to a {@code NonEmptySet}.
     * <p>
     * Since this will always be successful for {@link NonEmptySet}s,
     * this method always returns itself wrapped in a {@link Maybe#just}.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return this {@code NonEmptySet} wrapped in a {@link Maybe#just}
     */
    @Override
    default Maybe<? extends NonEmptySet<A>> toNonEmpty() {
        return just(this);
    }

    /**
     * Attempts to convert this {@code Set} to a {@code NonEmptySet}.
     * <p>
     * Since this will always be successful for {@link NonEmptySet}s,
     * this method always returns itself.
     * <p>
     * Does not make copies of any underlying data structures.
     *
     * @return this {@code NonEmptySet}
     */
    @Override
    default NonEmptySet<A> toNonEmptyOrThrow() {
        return this;
    }

    /**
     * Attempts to create a {@code NonEmptySet} that wraps a {@code java.util.Set}.
     * <p>
     * Does not make any copies of the given {@link java.util.Set}.
     * The created {@link NonEmptySet} will hold a reference to the given {@link java.util.Set}, but will not alter it in any way.
     * <p>
     * Bearers of the {@code NonEmptySet} will be unable to gain access to the underlying {@link java.util.Set}, it is safe to share.
     * <p>
     * Since no copy is made, be aware that anyone that holds a direct reference to the underlying {@code Set} can still mutate it.
     * Mutating the underlying {@code Set} is not advised.
     * Operations that change the size of the underlying {@code Set} will result in unpredictable behavior.
     * Use {@link Vector#copyFrom} if you want to avoid this situation.
     *
     * @param underlying {@code java.util.Set} to wrap; not null
     * @param <A>        the element type
     * @return a {@code NonEmptySet<A>} wrapped in a {@link Maybe#just} if {@code underlying} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<NonEmptySet<A>> maybeWrap(java.util.Set<A> underlying) {
        return Sets.maybeNonEmptyWrap(underlying);
    }

    /**
     * Attempts to create a {@code NonEmptySet} that wraps a {@code java.util.Set}.
     * If it is not possible, throws an {@link IllegalArgumentException}.
     * <p>
     * Does not make any copies of the given {@link java.util.Set}.
     * The created {@link NonEmptySet} will hold a reference to the given {@code java.util.Set}, but will not alter it in any way.
     * <p>
     * Bearers of the created {@link NonEmptySet} will be unable to gain access to the underlying {@code Set}, it is safe to share.
     * <p>
     * Since no copy is made, be aware that anyone that holds a direct reference to the underlying {@code Set} can still mutate it.
     * Mutating the underlying {@code Set} is not advised.
     * Operations that change the size of the underlying {@code Set} will result in unpredictable behavior.
     * Use {@link NonEmptySet#copyFromOrThrow(Iterable)} if you want to avoid this situation.
     *
     * @param underlying {@code java.util.Set} to wrap; not null
     * @param <A>        the element type
     * @return a {@code NonEmptySet<A>} if {@code underlying} is non-empty; throws an {@link IllegalArgumentException} otherwise
     */
    static <A> NonEmptySet<A> wrapOrThrow(java.util.Set<A> underlying) {
        return Sets.nonEmptyWrapOrThrow(underlying);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptySet} that is copied from any {@code Iterable}.
     * <p>
     * The entire {@link Iterable} will be eagerly iterated.
     * Be careful not to pass in an infinite {@code Iterable} or this method will not terminate.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If {@code source} already is an {@link ImmutableNonEmptySet}, it will be returned directly.
     *
     * @param source an {@code Iterable<A>} that may be iterated eagerly in its entirety; not null
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptySet<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptySet<A>> maybeCopyFrom(Iterable<A> source) {
        return ImmutableSets.maybeNonEmptyCopyFrom(source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptySet} that is copied from an array.
     *
     * @param source the array to copy from.
     *               Not null.
     *               This method will not alter or hold on to a reference of this array.
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptySet<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptySet<A>> maybeCopyFrom(A[] source) {
        return ImmutableSets.maybeNonEmptyCopyFrom(source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptySet} that is copied from any {@link Iterable}, but consuming a maximum number of elements.
     * <p>
     * The {@link Iterable} will be eagerly iterated, but only up to a maximum of {@code maxCount} elements.
     * If {@code maxCount} elements are not available, then the all of the elements available will be returned.
     * <p>
     * This method will make a copy of the data provided, unless {@code source} is
     * an {@link ImmutableNonEmptySet} and its size is equal to {@code maxCount},
     * in which case it will be returned directly.
     *
     * @param maxCount the maximum number of elements to consume from the source.  Must be &gt;= 0.
     *                 If 0, this method will always return {@link Maybe#nothing}.
     * @param source   an {@code Iterable<A>} that will be iterated eagerly for up to {@code maxCount} elements.
     *                 Not null.
     *                 It is safe for {@code source} to be infinite.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptySet<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptySet<A>> maybeCopyFrom(int maxCount, Iterable<A> source) {
        return ImmutableSets.maybeNonEmptyCopyFrom(maxCount, source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptySet} that is copied from an array, but with a maximum number of elements.
     *
     * @param maxCount the maximum number of elements to copy from the array.
     *                 Must be &gt;= 0.
     *                 If 0, this method will always return {@link Maybe#nothing}.
     * @param source   the array to copy from.
     *                 Not null.
     *                 This method will not alter or hold on to a reference of this array.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptySet<A>} wrapped in a {@link Maybe#just} if {@code source} is non-empty;
     * {@link Maybe#nothing} otherwise.
     */
    static <A> Maybe<ImmutableNonEmptySet<A>> maybeCopyFrom(int maxCount, A[] source) {
        return ImmutableSets.maybeNonEmptyCopyFrom(maxCount, source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptySet} from any {@code Iterable}.
     * If the {@link Iterable} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * The entire {@link Iterable} will be eagerly iterated.
     * Be careful not to pass in an infinite {@code Iterable} or this method will not terminate.
     * <p>
     * If necessary to guarantee immutability, this method will make a copy of the data provided.
     * If {@code source} already is an {@link ImmutableNonEmptySet}, it will be returned directly.
     *
     * @param source an {@code Iterable<A>} that will be iterated eagerly in its entirety; not null
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptySet<A>}
     */
    static <A> ImmutableNonEmptySet<A> copyFromOrThrow(Iterable<A> source) {
        return ImmutableSets.nonEmptyCopyFromOrThrow(source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptySet} that is copied from an array.
     * If the array is empty, throws an {@link IllegalArgumentException}.
     *
     * @param source the array to copy from.
     *               Not null.
     *               This method will not alter or hold on to a reference of this array.
     * @param <A>    the element type
     * @return an {@code ImmutableNonEmptySet<A>}
     */
    static <A> ImmutableNonEmptySet<A> copyFromOrThrow(A[] source) {
        return ImmutableSets.nonEmptyCopyFromOrThrow(source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptySet} from any {@code Iterable}, but consuming a maximum number of elements.
     * If the {@link Iterable} is empty, throws an {@link IllegalArgumentException}.
     * <p>
     * The {@code Iterable} will be eagerly iterated, but only up to a maximum of {@code maxCount} elements.
     * If {@code maxCount} elements are not available, then the all of the elements available will be returned.
     * <p>
     * This method will make a copy of the data provided, unless {@code source} is
     * an {@link ImmutableNonEmptySet} and its size is equal to {@code maxCount},
     * in which case it will be returned directly.
     * <p>
     *
     * @param maxCount the maximum number of elements to consume from the source.
     *                 Must be &gt;= 1.
     * @param source   an {@code Iterable<A>} that will be iterated eagerly for up to {@code maxCount} elements.
     *                 Not null.
     *                 It is safe for {@code source} to be infinite.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptySet<A>}
     */
    static <A> ImmutableNonEmptySet<A> copyFromOrThrow(int maxCount, Iterable<A> source) {
        return ImmutableSets.nonEmptyCopyFromOrThrow(maxCount, source);
    }

    /**
     * Attempts to create an {@code ImmutableNonEmptySet} that is copied from an array, but with a maximum number of elements.
     * If the array is empty, throws an {@link IllegalArgumentException}.
     *
     * @param maxCount the maximum number of elements to copy from the array.
     *                 Must be &gt;= 1.
     * @param source   the array to copy from.
     *                 Not null.
     *                 This method will not alter or hold on to a reference of this array.
     * @param <A>      the element type
     * @return an {@code ImmutableNonEmptySet<A>}
     */
    static <A> ImmutableNonEmptySet<A> copyFromOrThrow(int maxCount, A[] source) {
        return ImmutableSets.nonEmptyCopyFromOrThrow(maxCount, source);
    }

}
