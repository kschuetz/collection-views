package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

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
    default ImmutableNonEmptyVector<A> ensureImmutable() {
        return this;
    }
}
