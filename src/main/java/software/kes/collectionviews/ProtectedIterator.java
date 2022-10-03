package software.kes.collectionviews;

import java.util.Iterator;

final class ProtectedIterator<A> implements Iterator<A> {
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

    static <A> ProtectedIterator<A> protectedIterator(Iterator<A> underlying) {
        return new ProtectedIterator<>(underlying);
    }

}
