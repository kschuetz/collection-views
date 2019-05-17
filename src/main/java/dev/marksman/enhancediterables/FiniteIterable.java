package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Reverse;
import com.jnape.palatable.lambda.functions.builtin.fn2.CartesianProduct;
import com.jnape.palatable.lambda.functions.builtin.fn2.Drop;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import com.jnape.palatable.lambda.functions.builtin.fn2.Take;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;

import static dev.marksman.enhancediterables.EnhancedIterables.finiteIterable;

public interface FiniteIterable<A> extends EnhancedIterable<A> {

    default <B> FiniteIterable<Tuple2<A, B>> cross(FiniteIterable<B> other) {
        return finiteIterable(CartesianProduct.cartesianProduct(this, other));
    }

    @Override
    default FiniteIterable<A> drop(int count) {
        return finiteIterable(Drop.drop(count, this));
    }

    @Override
    default <B> FiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return finiteIterable(Map.map(f, this));
    }

    default FiniteIterable<A> reverse() {
        return finiteIterable(Reverse.reverse(this));
    }

    @Override
    default FiniteIterable<A> take(int count) {
        return finiteIterable(Take.take(count, this));
    }

    default <B, R> FiniteIterable<R> zipWith(Fn2<A, B, R> fn, FiniteIterable<B> other) {
        return finiteIterable(ZipWith.zipWith(fn.toBiFunction(), this, other));
    }

}
