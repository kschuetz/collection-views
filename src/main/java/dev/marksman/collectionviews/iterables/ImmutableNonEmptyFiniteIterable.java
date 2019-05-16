package dev.marksman.collectionviews.iterables;

import com.jnape.palatable.lambda.functions.Fn1;

public interface ImmutableNonEmptyFiniteIterable<A> extends ImmutableFiniteIterable<A>, NonEmptyFiniteIterable<A> {

    @Override
    ImmutableFiniteIterable<A> tail();

    @Override
    default <B> ImmutableNonEmptyFiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return null;
    }

    @Override
    default ImmutableNonEmptyFiniteIterable<A> reverse() {
        return null;
    }

}
