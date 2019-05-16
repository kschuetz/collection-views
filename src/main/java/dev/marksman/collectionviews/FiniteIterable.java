package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.builtin.fn2.Find;

public interface FiniteIterable<A> extends View<A> {

    default <B> FiniteIterable<Tuple2<A, B>> cross(FiniteIterable<B> other) {
        return null;
    }

    default FiniteIterable<A> drop(int count) {
        return null;
    }

    default Maybe<A> find(Fn1<? super A, ? extends Boolean> predicate) {
        return Find.find(predicate, this);
    }

    /**
     * Creates a {@code FiniteIterable} with this {@code FiniteIterable}'s elements in reversed order.
     *
     * @return a {@code FiniteIterable<A>}
     */
    default FiniteIterable<A> reverse() {
        return null;
    }

}
