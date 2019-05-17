package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Init;
import com.jnape.palatable.lambda.functions.builtin.fn1.Reverse;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;

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
    default <B> ImmutableNonEmptyFiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return immutableNonEmptyFiniteIterableOrThrow(Map.map(f, this));
    }

    @Override
    default ImmutableFiniteIterable<A> init() {
        return immutableFiniteIterable(Init.init(this));
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
