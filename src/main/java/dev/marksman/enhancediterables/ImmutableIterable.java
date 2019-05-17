package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Tails;
import com.jnape.palatable.lambda.functions.builtin.fn2.Drop;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import com.jnape.palatable.lambda.functions.builtin.fn2.Snoc;
import com.jnape.palatable.lambda.functions.builtin.fn2.Take;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;
import com.jnape.palatable.lambda.monoid.builtin.Concat;

import static dev.marksman.enhancediterables.EnhancedIterables.*;

/**
 * An {@code EnhancedIterable} that is safe from mutation.
 * <p>
 * May be infinite, finite, or empty.
 *
 * @param <A> the element type
 */
public interface ImmutableIterable<A> extends EnhancedIterable<A> {

    @Override
    default ImmutableNonEmptyIterable<A> append(A element) {
        return immutableNonEmptyIterableOrThrow(Snoc.snoc(element, this));
    }

    default ImmutableIterable<A> concat(ImmutableIterable<A> other) {
        return EnhancedIterables.immutableIterable(Concat.concat(this, other));
    }

    /**
     * Returns a new {@code ImmutableIterable} that drops the first {@code count} elements of this {@code ImmutableIterable}.
     *
     * @param count the number of elements to drop from this {@code ImmutableIterable}.
     *              Must be &gt;= 0.
     *              May exceed size of this {@code ImmutableIterable}, in which case, the result will be an
     *              empty {@code ImmutableIterable}.
     * @return an {@code ImmutableIterable<A>}
     */
    @Override
    default ImmutableIterable<A> drop(int count) {
        return immutableIterable(Drop.drop(count, this));
    }

    @Override
    default <B> ImmutableIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return immutableIterable(Map.map(f, this));
    }

    @Override
    default ImmutableNonEmptyIterable<A> prepend(A element) {
        return ImmutableNonEmptyIterable.immutableNonEmptyIterable(element, this);
    }

    @Override
    default NonEmptyIterable<? extends ImmutableIterable<A>> tails() {
        return nonEmptyIterableOrThrow(Map.map(EnhancedIterables::immutableIterable, Tails.tails(this)));
    }

    @Override
    default ImmutableIterable<A> take(int count) {
        return immutableIterable(Take.take(count, this));
    }

    default <B, C> ImmutableIterable<C> zipWith(Fn2<A, B, C> fn, ImmutableIterable<B> other) {
        return immutableIterable(ZipWith.zipWith(fn.toBiFunction(), this, other));
    }

    @SafeVarargs
    static <A> ImmutableNonEmptyFiniteIterable<A> of(A first, A... more) {
        return EnhancedIterables.of(first, more);
    }

}
