package dev.marksman.collectionviews.concrete;

import com.jnape.palatable.lambda.adt.Maybe;
import dev.marksman.collectionviews.Vector;

import java.util.Iterator;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.util.Collections.emptyIterator;

class EmptyVector<A> implements Vector<A> {
    private static final EmptyVector<?> INSTANCE = new EmptyVector<>();

    @Override
    public int size() {
        return 0;
    }

    @Override
    public A unsafeGet(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Vector<A> tail() {
        return this;
    }

    @Override
    public Maybe<A> get(int index) {
        return nothing();
    }

    @Override
    public Iterator<A> iterator() {
        return emptyIterator();
    }

    @SuppressWarnings("unchecked")
    static <A> EmptyVector<A> emptyVector() {
        return (EmptyVector<A>) INSTANCE;
    }
}
