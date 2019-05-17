package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.functions.Fn1;

public interface ImmutableNonEmptyIterable<A> extends ImmutableIterable<A>, NonEmptyIterable<A> {
    @Override
    ImmutableIterable<A> tail();

    @Override
    default <B> ImmutableNonEmptyIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return null;
    }
}
