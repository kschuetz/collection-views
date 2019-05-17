package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Init;
import com.jnape.palatable.lambda.functions.builtin.fn1.Reverse;
import com.jnape.palatable.lambda.functions.builtin.fn2.CartesianProduct;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;

import static dev.marksman.enhancediterables.EnhancedIterables.nonEmptyFiniteIterableOrThrow;

/**
 * An {@code EnhancedIterable} that is finite and guaranteed to contain at least one element.
 *
 * @param <A> the element type
 */
public interface NonEmptyFiniteIterable<A> extends FiniteIterable<A>, NonEmptyIterable<A> {

    @Override
    FiniteIterable<A> tail();

    @Override
    default <B> NonEmptyFiniteIterable<Tuple2<A, B>> cross(FiniteIterable<B> other) {
        return nonEmptyFiniteIterableOrThrow(CartesianProduct.cartesianProduct(this, other));
    }

    @Override
    default <B> NonEmptyFiniteIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return nonEmptyFiniteIterableOrThrow(Map.map(f, this));
    }

    default FiniteIterable<A> init() {
        return EnhancedIterables.finiteIterable(Init.init(this));
    }

    @Override
    default NonEmptyFiniteIterable<A> reverse() {
        return nonEmptyFiniteIterableOrThrow(Reverse.reverse(this));
    }

    default <B, C> NonEmptyFiniteIterable<C> zipWith(Fn2<A, B, C> fn, NonEmptyFiniteIterable<B> other) {
        return nonEmptyFiniteIterableOrThrow(ZipWith.zipWith(fn.toBiFunction(), this, other));
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
