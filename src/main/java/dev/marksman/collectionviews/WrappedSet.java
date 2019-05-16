package dev.marksman.collectionviews;

import java.util.Iterator;
import java.util.Set;

import static dev.marksman.collectionviews.internal.ProtectedIterator.protectedIterator;

final class WrappedSet<A> extends ConcreteSet<A>
        implements NonEmptySet<A>, Primitive {

    /**
     * underlying must contain at least one element
     */
    private final java.util.Set<A> underlying;

    WrappedSet(Set<A> underlying) {
        this.underlying = underlying;
    }

    @Override
    public A head() {
        return underlying.iterator().next();
    }

    @Override
    public Iterable<A> tail() {
        Iterator<A> iterator = iterator();
        iterator.next();
        return () -> iterator;
    }

    @Override
    public int size() {
        return underlying.size();
    }

    @Override
    public boolean contains(A element) {
        return underlying.contains(element);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Iterator<A> iterator() {
        return protectedIterator(underlying.iterator());
    }

}
