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

import static dev.marksman.enhancediterables.EnhancedIterables.*;

/**
 * An {@code EnhancedIterable} that is both finite and safe from mutation.
 *
 * @param <A> the element type
 */
public interface ImmutableFiniteIterable<A> extends ImmutableIterable<A>, FiniteIterable<A> {

    @Override
    default ImmutableNonEmptyFiniteIterable<A> append(A element) {
        return immutableNonEmptyFiniteIterableOrThrow(Snoc.snoc(element, this));
    }

    default ImmutableFiniteIterable<A> concat(ImmutableFiniteIterable<A> other) {
        return EnhancedIterables.immutableFiniteIterable(Concat.concat(this, other));
    }

    /**
     * Returns the lazily computed cartesian product of this {@code ImmutableFiniteIterable} with another {@code ImmutableFiniteIterable}.
     *
     * @param other an {@code ImmutableFiniteIterable} of any type
     * @param <B>   the type of the other {@code ImmutableFiniteIterable}
     * @return a {@code ImmutableFiniteIterable<Tuple2<A, B>>}
     */
    default <B> ImmutableFiniteIterable<Tuple2<A, B>> cross(ImmutableFiniteIterable<B> other) {
        return immutableFiniteIterable(CartesianProduct.cartesianProduct(this, other));
    }

    /**
     * Returns a new {@code ImmutableFiniteIterable} that drops the first {@code count} elements of this {@code ImmutableFiniteIterable}.
     *
     * @param count the number of elements to drop from this {@code ImmutableFiniteIterable}.
     *              Must be &gt;= 0.
     *              May exceed size of this {@code ImmutableFiniteIterable}, in which case, the result will be an
     *              empty {@code ImmutableFiniteIterable}.
     * @return an {@code ImmutableFiniteIterable<A>}
     */
    @Override
    default ImmutableFiniteIterable<A> drop(int count) {
        return immutableFiniteIterable(Drop.drop(count, this));
    }

    @Override
    default ImmutableFiniteIterable<A> dropWhile(Fn1<? super A, ? extends Boolean> predicate) {
        return immutableFiniteIterable(DropWhile.dropWhile(predicate, this));
    }

    @Override
    default ImmutableFiniteIterable<A> filter(Fn1<? super A, ? extends Boolean> predicate) {
        return immutableFiniteIterable(Filter.<A>filter(predicate).apply(this));
    }

    @Override
    default <B> ImmutableFiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return immutableFiniteIterable(Map.map(f, this));
    }

    default NonEmptyIterable<? extends ImmutableFiniteIterable<A>> inits() {
        return nonEmptyIterableOrThrow(Map.map(EnhancedIterables::immutableFiniteIterable, Inits.inits(this)));
    }

    @Override
    default ImmutableFiniteIterable<A> intersperse(A a) {
        return immutableFiniteIterable(Intersperse.intersperse(a, this));
    }

    @Override
    default ImmutableNonEmptyFiniteIterable<A> prepend(A element) {
        return ImmutableNonEmptyFiniteIterable.immutableNonEmptyFiniteIterable(element, this);
    }

    @Override
    default ImmutableFiniteIterable<A> prependAll(A a) {
        return immutableFiniteIterable(PrependAll.prependAll(a, this));
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
