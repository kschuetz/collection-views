package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.builtin.fn2.Cons;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;

import java.util.Iterator;

/**
 * An {@link Iterable} that is guaranteed to contain at least one element.
 * <p>
 * May be infinite.
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
     * Returns an {@link Iterable} containing all subsequent elements beyond the first.
     *
     * @return an {@code Iterable<A>}.  May be empty.
     */
    Iterable<A> tail();

    @Override
    default <B> NonEmptyIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return nonEmptyIterable(f.apply(head()), Map.map(f, tail()));
    }

    @Override
    default Iterator<A> iterator() {
        return Cons.cons(head(), tail()).iterator();
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
        return new NonEmptyIterable<A>() {
            @Override
            public A head() {
                return head;
            }

            @Override
            public Iterable<A> tail() {
                return tail;
            }
        };
    }
}
