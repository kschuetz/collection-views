package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;

public interface NonEmptyFiniteIterable<A> extends FiniteIterable<A>, NonEmptyIterable<A> {

    @Override
    default <B> NonEmptyFiniteIterable<Tuple2<A, B>> cross(FiniteIterable<B> other) {
        return null;
    }

    @Override
    default <B> NonEmptyFiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return null;
    }

    @Override
    default NonEmptyFiniteIterable<A> reverse() {
        return null;
    }

    @Override
    default <B, R> NonEmptyFiniteIterable<R> zipWith(Fn2<A, B, R> fn, FiniteIterable<B> other) {
        return null;
    }

}
