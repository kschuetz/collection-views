package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;

import static com.jnape.palatable.lambda.adt.Maybe.just;

public interface ImmutableNonEmptyVector<A> extends NonEmptyVector<A>, ImmutableVector<A> {

    @Override
    default ImmutableVector<A> tail() {
        return drop(1);
    }

    @Override
    default <B> ImmutableNonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return Vectors.immutableMapNonEmpty(f, this);
    }

    @Override
    default ImmutableNonEmptyVector<A> toImmutable() {
        return this;
    }

    @Override
    default Maybe<? extends ImmutableNonEmptyVector<A>> toNonEmpty() {
        return just(this);
    }

    @Override
    default ImmutableNonEmptyVector<A> toNonEmptyOrThrow() {
        return this;
    }
}
