package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.builtin.fn2.Cons;

import java.util.Iterator;

public interface NonEmptyIterable<A> extends Iterable<A> {
    A head();

    Iterable<A> tail();

    @Override
    default Iterator<A> iterator() {
        return Cons.cons(head(), tail()).iterator();
    }

    static <A> NonEmptyIterable<A> cons(A head, Iterable<A> tail) {
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
