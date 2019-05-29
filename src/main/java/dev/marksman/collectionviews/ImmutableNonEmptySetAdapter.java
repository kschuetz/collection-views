package dev.marksman.collectionviews;

import dev.marksman.enhancediterables.ImmutableFiniteIterable;

import java.util.Iterator;

import static dev.marksman.collectionviews.ProtectedIterator.protectedIterator;

final class ImmutableNonEmptySetAdapter<A> extends ConcreteSet<A>
        implements ImmutableNonEmptySet<A>, Primitive {
    private final ImmutableSet<A> underlying;

    ImmutableNonEmptySetAdapter(ImmutableSet<A> underlying) {
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
    public Iterator<A> iterator() {
        return protectedIterator(underlying.iterator());
    }

}
