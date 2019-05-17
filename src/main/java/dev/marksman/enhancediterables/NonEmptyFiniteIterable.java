package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Init;

import static dev.marksman.enhancediterables.EnhancedIterables.finiteIterable;

public interface NonEmptyFiniteIterable<A> extends FiniteIterable<A>, NonEmptyIterable<A> {

    @Override
    FiniteIterable<A> tail();

    @Override
    default <B> NonEmptyFiniteIterable<Tuple2<A, B>> cross(FiniteIterable<B> other) {
        return null;
    }

    @Override
    default <B> NonEmptyFiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return null;
    }

    default FiniteIterable<A> init() {
        return finiteIterable(Init.init(this));
    }

    @Override
    default NonEmptyFiniteIterable<A> reverse() {
        return null;
    }

    default <B, R> NonEmptyFiniteIterable<R> zipWith(Fn2<A, B, R> fn, NonEmptyFiniteIterable<B> other) {
        return null;
    }

    /**
     * Creates a {@code NonEmptyFiniteIterable}.
     *
     * @param head the first element
     * @param tail the remaining elements.  May be empty.
     * @param <A>  the element type
     * @return a {@code NonEmptyFiniteIterable<A>}
     */
    static <A> NonEmptyFiniteIterable<A> nonEmptyFiniteIterable(A head, FiniteIterable<A> tail) {
        return new NonEmptyFiniteIterable<A>() {
            @Override
            public A head() {
                return head;
            }

            @Override
            public FiniteIterable<A> tail() {
                return tail;
            }
        };
    }

}
