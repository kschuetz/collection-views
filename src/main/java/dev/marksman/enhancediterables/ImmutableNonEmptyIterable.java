package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn2.Intersperse;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import com.jnape.palatable.lambda.functions.builtin.fn2.PrependAll;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;
import com.jnape.palatable.lambda.monoid.builtin.Concat;

import static dev.marksman.enhancediterables.EnhancedIterables.immutableNonEmptyIterableOrThrow;

/**
 * An {@code EnhancedIterable} that is safe from mutation, and guaranteed to contain at least one element.
 * <p>
 * May be infinite or finite.
 *
 * @param <A> the element type
 */
public interface ImmutableNonEmptyIterable<A> extends ImmutableIterable<A>, NonEmptyIterable<A> {
    @Override
    ImmutableIterable<A> tail();

    @Override
    default ImmutableNonEmptyIterable<A> concat(ImmutableIterable<A> other) {
        return immutableNonEmptyIterableOrThrow(Concat.concat(this, other));
    }

    @Override
    default <B> ImmutableNonEmptyIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return immutableNonEmptyIterableOrThrow(Map.map(f, this));
    }

    @Override
    default ImmutableNonEmptyIterable<A> intersperse(A a) {
        return immutableNonEmptyIterableOrThrow(Intersperse.intersperse(a, this));
    }

    @Override
    default ImmutableNonEmptyIterable<A> prependAll(A a) {
        return immutableNonEmptyIterableOrThrow(PrependAll.prependAll(a, this));
    }

    default <B, C> ImmutableNonEmptyIterable<C> zipWith(Fn2<A, B, C> fn, ImmutableNonEmptyIterable<B> other) {
        return immutableNonEmptyIterableOrThrow(ZipWith.zipWith(fn.toBiFunction(), this, other));
    }

    static <A> ImmutableNonEmptyIterable<A> immutableNonEmptyIterable(A head, ImmutableIterable<A> tail) {
        return new ImmutableNonEmptyIterable<A>() {
            @Override
            public A head() {
                return head;
            }

            @Override
            public ImmutableIterable<A> tail() {
                return tail;
            }
        };
    }

    @SafeVarargs
    static <A> ImmutableNonEmptyFiniteIterable<A> of(A first, A... more) {
        return EnhancedIterables.of(first, more);
    }

}
