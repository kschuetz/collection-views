package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;

public interface NonEmptySet<A> extends NonEmptyIterable<A>, Set<A> {

    @Override
    default boolean isEmpty() {
        return false;
    }

    static <A> NonEmptySet<A> wrap(A first, java.util.Set<A> more) {
        return Sets.nonEmptyWrap(first, more);
    }

    static <A> NonEmptySet<A> wrap(A first, Set<A> more) {
        return Sets.nonEmptyWrap(first, more);
    }

    static <A> Maybe<NonEmptySet<A>> tryWrap(java.util.Set<A> underlying) {
        return Sets.tryNonEmptyWrap(underlying);
    }

    static <A> Maybe<NonEmptySet<A>> tryWrap(Set<A> underlying) {
        return Sets.tryNonEmptyWrap(underlying);
    }

    static <A> NonEmptySet<A> wrapOrThrow(java.util.Set<A> underlying) {
        return Sets.nonEmptyWrapOrThrow(underlying);
    }

    static <A> NonEmptySet<A> wrapOrThrow(Set<A> underlying) {
        return Sets.nonEmptyWrapOrThrow(underlying);
    }

    @SafeVarargs
    static <A> NonEmptySet<A> of(A first, A... more) {
        return Sets.nonEmptySetOf(first, more);
    }
}
