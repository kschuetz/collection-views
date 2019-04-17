package dev.marksman.collectionviews;

import java.util.Iterator;

import static dev.marksman.collectionviews.ProtectedIterator.protectedIterator;

class NonEmptySetAdapter<A> implements NonEmptySet<A> {
    private final Set<A> underlying;

    NonEmptySetAdapter(Set<A> underlying) {
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
    public Iterator<A> iterator() {
        return protectedIterator(underlying.iterator());
    }
}
