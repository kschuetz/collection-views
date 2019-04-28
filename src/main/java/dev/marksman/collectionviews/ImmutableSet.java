package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;

public interface ImmutableSet<A> extends Set<A>, Immutable {

    @Override
    default ImmutableSet<A> toImmutable() {
        return this;
    }

    @Override
    default Maybe<? extends ImmutableNonEmptySet<A>> toNonEmpty() {
        return null;
    }

    @Override
    default NonEmptySet<A> toNonEmptyOrThrow() {
        return null;
    }
}
