package dev.marksman.collectionviews;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class VectorIterator<A> implements Iterator<A> {
    private final Vector<A> underlying;
    private int index;

    public VectorIterator(Vector<A> underlying) {
        this.underlying = underlying;
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < underlying.size();
    }

    @Override
    public A next() {
        if (index >= underlying.size()) throw new NoSuchElementException();
        A result = underlying.unsafeGet(index);
        index += 1;
        return result;
    }
}
