package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Inits;
import com.jnape.palatable.lambda.functions.builtin.fn1.Reverse;
import com.jnape.palatable.lambda.functions.builtin.fn1.Tails;
import com.jnape.palatable.lambda.functions.builtin.fn2.CartesianProduct;
import com.jnape.palatable.lambda.functions.builtin.fn2.Drop;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import com.jnape.palatable.lambda.functions.builtin.fn2.Take;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;

import java.util.Collection;

import static dev.marksman.enhancediterables.EnhancedIterables.nonEmptyIterableOrThrow;

/**
 * An {@code EnhancedIterable} that is finite.
 *
 * @param <A> the element type
 */
public interface FiniteIterable<A> extends EnhancedIterable<A> {

    default <B> FiniteIterable<Tuple2<A, B>> cross(FiniteIterable<B> other) {
        return EnhancedIterables.finiteIterable(CartesianProduct.cartesianProduct(this, other));
    }

    @Override
    default FiniteIterable<A> drop(int count) {
        return EnhancedIterables.finiteIterable(Drop.drop(count, this));
    }

    @Override
    default <B> FiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return EnhancedIterables.finiteIterable(Map.map(f, this));
    }

    default NonEmptyIterable<? extends FiniteIterable<A>> inits() {
        return nonEmptyIterableOrThrow(Map.map(EnhancedIterables::finiteIterable, Inits.inits(this)));
    }

    default FiniteIterable<A> reverse() {
        return EnhancedIterables.finiteIterable(Reverse.reverse(this));
    }

    @Override
    default NonEmptyIterable<? extends FiniteIterable<A>> tails() {
        return nonEmptyIterableOrThrow(Map.map(EnhancedIterables::finiteIterable, Tails.tails(this)));
    }

    @Override
    default FiniteIterable<A> take(int count) {
        return EnhancedIterables.finiteIterable(Take.take(count, this));
    }

    default <B, C> FiniteIterable<C> zipWith(Fn2<A, B, C> fn, Iterable<B> other) {
        return EnhancedIterables.finiteIterable(ZipWith.zipWith(fn.toBiFunction(), this, other));
    }

    static <A> FiniteIterable<A> finiteIterable(Collection<A> collection) {
        return EnhancedIterables.finiteIterable(collection);
    }

    @SafeVarargs
    static <A> ImmutableNonEmptyFiniteIterable<A> of(A first, A... more) {
        return EnhancedIterables.of(first, more);
    }

}
