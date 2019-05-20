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

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
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
    default FiniteIterable<A> dropWhile(Fn1<? super A, ? extends Boolean> predicate) {
        return EnhancedIterables.finiteIterable(DropWhile.dropWhile(predicate, this));
    }

    @Override
    default FiniteIterable<A> filter(Fn1<? super A, ? extends Boolean> predicate) {
        return EnhancedIterables.finiteIterable(Filter.<A>filter(predicate).apply(this));
    }

    @Override
    default <B> FiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return EnhancedIterables.finiteIterable(Map.map(f, this));
    }

    default NonEmptyIterable<? extends FiniteIterable<A>> inits() {
        return nonEmptyIterableOrThrow(Map.map(EnhancedIterables::finiteIterable, Inits.inits(this)));
    }

    @Override
    default FiniteIterable<A> intersperse(A a) {
        return EnhancedIterables.finiteIterable(Intersperse.intersperse(a, this));
    }

    @Override
    default NonEmptyFiniteIterable<A> prepend(A element) {
        return NonEmptyFiniteIterable.nonEmptyFiniteIterable(element, this);
    }

    @Override
    default FiniteIterable<A> prependAll(A a) {
        return EnhancedIterables.finiteIterable(PrependAll.prependAll(a, this));
    }

    default FiniteIterable<A> reverse() {
        return EnhancedIterables.finiteIterable(Reverse.reverse(this));
    }

    @Override
    default Tuple2<? extends FiniteIterable<A>, ? extends FiniteIterable<A>> span(Fn1<? super A, ? extends Boolean> predicate) {
        Tuple2<Iterable<A>, Iterable<A>> spanResult = Span.<A>span(predicate).apply(this);
        return tuple(EnhancedIterables.finiteIterable(spanResult._1()),
                EnhancedIterables.finiteIterable(spanResult._2()));
    }

    @Override
    default NonEmptyIterable<? extends FiniteIterable<A>> tails() {
        return nonEmptyIterableOrThrow(Map.map(EnhancedIterables::finiteIterable, Tails.tails(this)));
    }

    @Override
    default EnhancedIterable<A> takeWhile(Fn1<? super A, ? extends Boolean> predicate) {
        return EnhancedIterables.finiteIterable(TakeWhile.takeWhile(predicate, this));
    }

    default <B, C> FiniteIterable<C> zipWith(Fn2<A, B, C> fn, Iterable<B> other) {
        return EnhancedIterables.finiteIterable(ZipWith.zipWith(fn.toBiFunction(), this, other));
    }

    static <A> FiniteIterable<A> finiteIterable(Collection<A> collection) {
        return EnhancedIterables.finiteIterable(collection);
    }

    static <A> FiniteIterable<A> finiteIterable(int maxCount, Iterable<A> iterable) {
        return EnhancedIterable.enhancedIterable(iterable).take(maxCount);
    }

    @SafeVarargs
    static <A> ImmutableNonEmptyFiniteIterable<A> of(A first, A... more) {
        return EnhancedIterables.of(first, more);
    }

}
