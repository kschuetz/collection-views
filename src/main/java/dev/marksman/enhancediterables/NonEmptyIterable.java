package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn2.Cons;
import com.jnape.palatable.lambda.functions.builtin.fn2.Intersperse;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import com.jnape.palatable.lambda.functions.builtin.fn2.PrependAll;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;
import com.jnape.palatable.lambda.monoid.builtin.Concat;

import java.util.Iterator;

import static dev.marksman.enhancediterables.EnhancedIterable.enhancedIterable;
import static dev.marksman.enhancediterables.EnhancedIterables.nonEmptyIterableOrThrow;

/**
 * An {@code EnhancedIterable} that is guaranteed to contain at least one element.
 * <p>
 * May be infinite or finite.
 *
 * @param <A> the element type
 */
public interface NonEmptyIterable<A> extends EnhancedIterable<A> {

    /**
     * Returns the first element.
     *
     * @return an element of type {@code A}
     */
    A head();

    /**
     * Returns an {@link EnhancedIterable} containing all subsequent elements beyond the first.
     *
     * @return an {@code EnhancedIterable<A>}.  May be empty.
     */
    EnhancedIterable<A> tail();

    @Override
    default NonEmptyIterable<A> concat(Iterable<A> other) {
        return nonEmptyIterableOrThrow(Concat.concat(this, other));
    }

    @Override
    default <B> NonEmptyIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return nonEmptyIterable(f.apply(head()), Map.map(f, tail()));
    }

    @Override
    default NonEmptyIterable<A> intersperse(A a) {
        return nonEmptyIterableOrThrow(Intersperse.intersperse(a, this));
    }

    @Override
    default Iterator<A> iterator() {
        return Cons.cons(head(), tail()).iterator();
    }

    @Override
    default NonEmptyIterable<A> prependAll(A a) {
        return nonEmptyIterableOrThrow(PrependAll.prependAll(a, this));
    }

    default <B, C> NonEmptyIterable<C> zipWith(Fn2<A, B, C> fn, NonEmptyIterable<B> other) {
        return nonEmptyIterable(fn.apply(head(), other.head()),
                ZipWith.zipWith(fn.toBiFunction(), tail(), other.tail()));
    }

    /**
     * Creates a {@link NonEmptyIterable}.
     *
     * @param head the first element
     * @param tail the remaining elements.  May be empty.
     * @param <A>  the element type
     * @return a {@code NonEmptyIterable<A>}
     */
    static <A> NonEmptyIterable<A> nonEmptyIterable(A head, Iterable<A> tail) {
        EnhancedIterable<A> enhancedTail = enhancedIterable(tail);
        return new NonEmptyIterable<A>() {
            @Override
            public A head() {
                return head;
            }

            @Override
            public EnhancedIterable<A> tail() {
                return enhancedTail;
            }
        };
    }

}
