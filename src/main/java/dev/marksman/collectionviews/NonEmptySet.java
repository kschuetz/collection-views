package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;

import static com.jnape.palatable.lambda.adt.Maybe.just;

public interface NonEmptySet<A> extends NonEmptyIterable<A>, Set<A> {

    @Override
    default boolean isEmpty() {
        return false;
    }

    @Override
    default ImmutableNonEmptySet<A> toImmutable() {
        return Sets.ensureImmutable(this);
    }

    @Override
    default Maybe<? extends NonEmptySet<A>> toNonEmpty() {
        return just(this);
    }

    @Override
    default NonEmptySet<A> toNonEmptyOrThrow() {
        return this;
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
