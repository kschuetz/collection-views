package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;

import static com.jnape.palatable.lambda.adt.Maybe.just;

public interface ImmutableNonEmptySet<A> extends NonEmptySet<A>, ImmutableSet<A> {

    @Override
    default ImmutableNonEmptySet<A> toImmutable() {
        return this;
    }

    @Override
    default Maybe<? extends ImmutableNonEmptySet<A>> toNonEmpty() {
        return just(this);
    }

    @Override
    default ImmutableNonEmptySet<A> toNonEmptyOrThrow() {
        return this;
    }

}
