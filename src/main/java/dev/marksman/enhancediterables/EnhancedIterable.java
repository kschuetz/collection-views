package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn0;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Tails;
import com.jnape.palatable.lambda.functions.builtin.fn2.*;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;
import com.jnape.palatable.lambda.monoid.builtin.Concat;

import java.util.Collection;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static dev.marksman.enhancediterables.EnhancedIterables.finiteIterable;
import static dev.marksman.enhancediterables.EnhancedIterables.nonEmptyIterableOrThrow;
import static dev.marksman.enhancediterables.internal.ProtectedIterator.protectedIterator;

/**
 * An {@code Iterable} with some additional methods.
 * <p>
 * May be infinite, finite, or empty.
 * <p>
 * Any {@link Iterable} can be upgraded to an {@code EnhancedIterable} by calling {@link EnhancedIterable#enhancedIterable(Iterable)}}.
 *
 * @param <A> the element type
 */
public interface EnhancedIterable<A> extends Iterable<A> {

    default NonEmptyIterable<A> append(A element) {
        return nonEmptyIterableOrThrow(Snoc.snoc(element, this));
    }

    default EnhancedIterable<A> concat(Iterable<A> other) {
        return enhancedIterable(Concat.concat(this, other));
    }

    /**
     * Returns a new {@code EnhancedIterable} that drops the first {@code count} elements of this {@code EnhancedIterable}.
     *
     * @param count the number of elements to drop from this {@code EnhancedIterable}.
     *              Must be &gt;= 0.
     *              May exceed size of this {@code EnhancedIterable}, in which case, the result will be an
     *              empty {@code EnhancedIterable}.
     * @return an {@code EnhancedIterable<A>}
     */
    default EnhancedIterable<A> drop(int count) {
        return enhancedIterable(Drop.drop(count, this));
    }

    default EnhancedIterable<A> dropWhile(Fn1<? super A, ? extends Boolean> predicate) {
        return enhancedIterable(DropWhile.dropWhile(predicate, this));
    }

    default EnhancedIterable<A> filter(Fn1<? super A, ? extends Boolean> predicate) {
        return enhancedIterable(Filter.<A>filter(predicate).apply(this));
    }

    /**
     * Finds the first element of this {@code EnhancedIterable} that satisfies a predicate, if any.
     *
     * @param predicate a predicate; not null
     * @return an element wrapped in a {@link Maybe#just} if a matching element is found;
     * {@link Maybe#nothing} otherwise.
     */
    default Maybe<A> find(Fn1<? super A, ? extends Boolean> predicate) {
        return Find.find(predicate, this);
    }

    default <B> EnhancedIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return enhancedIterable(Map.map(f, this));
    }

    default EnhancedIterable<A> intersperse(A a) {
        return enhancedIterable(Intersperse.intersperse(a, this));
    }

    default boolean isEmpty() {
        return !iterator().hasNext();
    }

    default NonEmptyIterable<A> prepend(A element) {
        return NonEmptyIterable.nonEmptyIterable(element, this);
    }

    default EnhancedIterable<A> prependAll(A a) {
        return enhancedIterable(PrependAll.prependAll(a, this));
    }

    default Tuple2<? extends EnhancedIterable<A>, ? extends EnhancedIterable<A>> span(Fn1<? super A, ? extends Boolean> predicate) {
        Tuple2<Iterable<A>, Iterable<A>> spanResult = Span.<A>span(predicate).apply(this);
        return tuple(enhancedIterable(spanResult._1()), enhancedIterable(spanResult._2()));
    }

    default NonEmptyIterable<? extends EnhancedIterable<A>> tails() {
        return nonEmptyIterableOrThrow(Map.map(EnhancedIterable::enhancedIterable, Tails.tails(this)));
    }

    default FiniteIterable<A> take(int count) {
        return finiteIterable(Take.take(count, this));
    }

    default EnhancedIterable<A> takeWhile(Fn1<? super A, ? extends Boolean> predicate) {
        return enhancedIterable(TakeWhile.takeWhile(predicate, this));
    }

    default A[] toArray(Class<A[]> arrayType) {
        return ToArray.toArray(arrayType).apply(this);
    }

    default <C extends Collection<A>> C toCollection(Fn0<C> cSupplier) {
        return ToCollection.toCollection(cSupplier).apply(this);
    }

    default <B, C> EnhancedIterable<C> zipWith(Fn2<A, B, C> fn, Iterable<B> other) {
        return enhancedIterable(ZipWith.zipWith(fn.toBiFunction(), this, other));
    }

    static <A> EnhancedIterable<A> enhancedIterable(Iterable<A> underlying) {
        if (underlying instanceof EnhancedIterable<?>) {
            return (EnhancedIterable<A>) underlying;
        } else {
            return () -> protectedIterator(underlying.iterator());
        }
    }

    @SafeVarargs
    static <A> ImmutableNonEmptyFiniteIterable<A> of(A first, A... more) {
        return EnhancedIterables.of(first, more);
    }

}
