package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Init;
import com.jnape.palatable.lambda.functions.builtin.fn1.Reverse;
import com.jnape.palatable.lambda.functions.builtin.fn2.CartesianProduct;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import com.jnape.palatable.lambda.functions.builtin.fn2.Snoc;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;
import com.jnape.palatable.lambda.monoid.builtin.Concat;

import static dev.marksman.enhancediterables.EnhancedIterables.immutableFiniteIterable;
import static dev.marksman.enhancediterables.EnhancedIterables.immutableNonEmptyFiniteIterableOrThrow;

/**
 * An {@code EnhancedIterable} that is finite, safe from mutation, and guaranteed to contain at least one element.
 *
 * @param <A> the element type
 */
public interface ImmutableNonEmptyFiniteIterable<A> extends ImmutableFiniteIterable<A>, ImmutableNonEmptyIterable<A>,
        NonEmptyFiniteIterable<A> {

    @Override
    ImmutableFiniteIterable<A> tail();

    @Override
    default ImmutableNonEmptyFiniteIterable<A> append(A element) {
        return immutableNonEmptyFiniteIterableOrThrow(Snoc.snoc(element, this));
    }

    @Override
    default ImmutableNonEmptyFiniteIterable<A> concat(ImmutableFiniteIterable<A> other) {
        return immutableNonEmptyFiniteIterableOrThrow(Concat.concat(this, other));
    }

    /**
     * Returns the lazily computed cartesian product of this {@code ImmutableNonEmptyFiniteIterable} with another {@code ImmutableNonEmptyFiniteIterable}.
     *
     * @param other an {@code ImmutableNonEmptyFiniteIterable} of any type
     * @param <B>   the type of the other {@code ImmutableNonEmptyFiniteIterable}
     * @return a {@code ImmutableNonEmptyFiniteIterable<Tuple2<A, B>>}
     */
    default <B> ImmutableNonEmptyFiniteIterable<Tuple2<A, B>> cross(ImmutableNonEmptyFiniteIterable<B> other) {
        return immutableNonEmptyFiniteIterableOrThrow(CartesianProduct.cartesianProduct(this, other));
    }

    @Override
    default <B> ImmutableNonEmptyFiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return immutableNonEmptyFiniteIterableOrThrow(Map.map(f, this));
    }

    @Override
    default ImmutableFiniteIterable<A> init() {
        return immutableFiniteIterable(Init.init(this));
    }

    @Override
    default ImmutableNonEmptyFiniteIterable<A> prepend(A element) {
        return immutableNonEmptyFiniteIterable(element, this);
    }

    @Override
    default ImmutableNonEmptyFiniteIterable<A> reverse() {
        return immutableNonEmptyFiniteIterableOrThrow(Reverse.reverse(this));
    }

    default <B, C> ImmutableNonEmptyFiniteIterable<C> zipWith(Fn2<A, B, C> fn, ImmutableNonEmptyIterable<B> other) {
        return immutableNonEmptyFiniteIterableOrThrow(ZipWith.zipWith(fn.toBiFunction(), this, other));
    }

    static <A> ImmutableNonEmptyFiniteIterable<A> immutableNonEmptyFiniteIterable(A head, ImmutableFiniteIterable<A> tail) {
        return new ImmutableNonEmptyFiniteIterable<A>() {
            @Override
            public A head() {
                return head;
            }

            @Override
            public ImmutableFiniteIterable<A> tail() {
                return tail;
            }
        };
    }

    @SafeVarargs
    static <A> ImmutableNonEmptyFiniteIterable<A> of(A first, A... more) {
        return EnhancedIterables.of(first, more);
    }
}
