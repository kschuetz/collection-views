package software.kes.collectionviews;

@SuppressWarnings("unused")
final class ImmutableArrayVector<A> extends ConcreteVector<A>
        implements ImmutableNonEmptyVector<A>, Primitive {
    /**
     * underlying must contain at least one element.
     */
    private final A[] underlying;

    ImmutableArrayVector(A[] underlying) {
        this.underlying = underlying;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return underlying.length;
    }

    @Override
    public A unsafeGet(int index) {
        if (index < 0 || index >= underlying.length) {
            throw new IndexOutOfBoundsException();
        }
        return underlying[index];
    }

}
