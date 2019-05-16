package dev.marksman.collectionviews;

import dev.marksman.collectionviews.iterables.ImmutableFiniteIterable;

import java.util.Iterator;
import java.util.Set;

import static dev.marksman.collectionviews.internal.ProtectedIterator.protectedIterator;

final class ImmutableWrappedSet<A> extends ConcreteSet<A>
        implements ImmutableNonEmptySet<A>, Primitive {

    /**
     * underlying must contain at least one element
     */
    private final Set<A> underlying;

    ImmutableWrappedSet(Set<A> underlying) {
        this.underlying = underlying;
    }

    @Override
    public A head() {
        return underlying.iterator().next();
    }

    @Override
    public ImmutableFiniteIterable<A> tail() {
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
