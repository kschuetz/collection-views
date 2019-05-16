package dev.marksman.collectionviews.internal;

import java.util.Iterator;

public final class ProtectedIterator<A> implements Iterator<A> {
    private final Iterator<A> underlying;

    private ProtectedIterator(Iterator<A> underlying) {
        this.underlying = underlying;
    }

    @Override
    public boolean hasNext() {
        return underlying.hasNext();
    }

    @Override
    public A next() {
        return underlying.next();
    }

    public static <A> ProtectedIterator<A> protectedIterator(Iterator<A> underlying) {
        return new ProtectedIterator<>(underlying);
    }

}

