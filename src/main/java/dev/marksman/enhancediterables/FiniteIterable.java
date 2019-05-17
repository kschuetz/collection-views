package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Inits;
import com.jnape.palatable.lambda.functions.builtin.fn1.Reverse;
import com.jnape.palatable.lambda.functions.builtin.fn1.Tails;
import com.jnape.palatable.lambda.functions.builtin.fn2.*;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;
import com.jnape.palatable.lambda.monoid.builtin.Concat;

import java.util.Collection;

import static dev.marksman.enhancediterables.EnhancedIterables.nonEmptyFiniteIterableOrThrow;
import static dev.marksman.enhancediterables.EnhancedIterables.nonEmptyIterableOrThrow;

/**
 * An {@code EnhancedIterable} that is finite.
 *
 * @param <A> the element type
 */
public interface FiniteIterable<A> extends EnhancedIterable<A> {

    @Override
    default NonEmptyFiniteIterable<A> append(A element) {
        return nonEmptyFiniteIterableOrThrow(Snoc.snoc(element, this));
    }

    default FiniteIterable<A> concat(FiniteIterable<A> other) {
        return EnhancedIterables.finiteIterable(Concat.concat(this, other));
    }

    /**
     * Returns the lazily computed cartesian product of this {@code FiniteIterable} with another {@code FiniteIterable}.
     *
     * @param other a {@code FiniteIterable} of any type
     * @param <B>   the type of the other {@code FiniteIterable}
     * @return a {@code FiniteIterable<Tuple2<A, B>>}
     */
    default <B> FiniteIterable<Tuple2<A, B>> cross(FiniteIterable<B> other) {
        return EnhancedIterables.finiteIterable(CartesianProduct.cartesianProduct(this, other));
    }

    /**
     * Returns a new {@code FiniteIterable} that drops the first {@code count} elements of this {@code FiniteIterable}.
     *
     * @param count the number of elements to drop from this {@code FiniteIterable}.
     *              Must be &gt;= 0.
     *              May exceed size of this {@code FiniteIterable}, in which case, the result will be an
     *              empty {@code FiniteIterable}.
     * @return a {@code FiniteIterable<A>}
     */
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

    @Override
    default NonEmptyFiniteIterable<A> prepend(A element) {
        return NonEmptyFiniteIterable.nonEmptyFiniteIterable(element, this);
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
