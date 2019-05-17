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

import static dev.marksman.enhancediterables.EnhancedIterables.immutableFiniteIterable;
import static dev.marksman.enhancediterables.EnhancedIterables.nonEmptyIterableOrThrow;

/**
 * An {@code EnhancedIterable} that is both finite and safe from mutation.
 *
 * @param <A> the element type
 */
public interface ImmutableFiniteIterable<A> extends ImmutableIterable<A>, FiniteIterable<A> {

    default <B> ImmutableFiniteIterable<Tuple2<A, B>> cross(ImmutableFiniteIterable<B> other) {
        return immutableFiniteIterable(CartesianProduct.cartesianProduct(this, other));
    }

    @Override
    default ImmutableFiniteIterable<A> drop(int count) {
        return immutableFiniteIterable(Drop.drop(count, this));
    }

    @Override
    default <B> ImmutableFiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return immutableFiniteIterable(Map.map(f, this));
    }

    default NonEmptyIterable<? extends ImmutableFiniteIterable<A>> inits() {
        return nonEmptyIterableOrThrow(Map.map(EnhancedIterables::immutableFiniteIterable, Inits.inits(this)));
    }

    @Override
    default ImmutableFiniteIterable<A> reverse() {
        return immutableFiniteIterable(Reverse.reverse(this));
    }

    @Override
    default NonEmptyIterable<? extends ImmutableFiniteIterable<A>> tails() {
        return nonEmptyIterableOrThrow(Map.map(EnhancedIterables::immutableFiniteIterable, Tails.tails(this)));
    }

    @Override
    default ImmutableFiniteIterable<A> take(int count) {
        return immutableFiniteIterable(Take.take(count, this));
    }

    default <B, C> ImmutableFiniteIterable<C> zipWith(Fn2<A, B, C> fn, ImmutableIterable<B> other) {
        return immutableFiniteIterable(ZipWith.zipWith(fn.toBiFunction(), this, other));
    }

    @SafeVarargs
    static <A> ImmutableNonEmptyFiniteIterable<A> of(A first, A... more) {
        return EnhancedIterables.of(first, more);
    }

}
