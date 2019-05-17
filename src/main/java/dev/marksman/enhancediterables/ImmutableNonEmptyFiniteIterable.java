package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Init;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;

import static dev.marksman.enhancediterables.EnhancedIterables.immutableFiniteIterable;
import static dev.marksman.enhancediterables.EnhancedIterables.immutableNonEmptyFiniteIterableOrThrow;

public interface ImmutableNonEmptyFiniteIterable<A> extends ImmutableFiniteIterable<A>, ImmutableNonEmptyIterable<A>,
        NonEmptyFiniteIterable<A> {

    @Override
    ImmutableFiniteIterable<A> tail();

    @Override
    default <B> ImmutableNonEmptyFiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return null;
    }

    @Override
    default ImmutableFiniteIterable<A> init() {
        return immutableFiniteIterable(Init.init(this));
    }

    @Override
    default ImmutableNonEmptyFiniteIterable<A> reverse() {
        return null;
    }

    default <B, R> ImmutableNonEmptyFiniteIterable<R> zipWith(Fn2<A, B, R> fn, ImmutableNonEmptyIterable<B> other) {
        return immutableNonEmptyFiniteIterableOrThrow(ZipWith.zipWith(fn.toBiFunction(), this, other));
    }
}
