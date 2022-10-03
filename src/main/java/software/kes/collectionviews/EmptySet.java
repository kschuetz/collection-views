package software.kes.collectionviews;

import java.util.Iterator;

import static java.util.Collections.emptyIterator;

final class EmptySet<A> extends ConcreteSet<A> implements ImmutableSet<A>, Primitive {
    private static final EmptySet<?> INSTANCE = new EmptySet<>();

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean contains(A element) {
        return false;
    }

    @Override
    public Iterator<A> iterator() {
        return emptyIterator();
    }

    @SuppressWarnings("unchecked")
    static <A> EmptySet<A> emptySet() {
        return (EmptySet<A>) INSTANCE;
    }
}
