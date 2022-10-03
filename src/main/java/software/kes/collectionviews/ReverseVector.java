package software.kes.collectionviews;

class ReverseVector<A> extends ConcreteVector<A> implements NonEmptyVector<A> {
    private final NonEmptyVector<A> underlying;

    private ReverseVector(NonEmptyVector<A> underlying) {
        this.underlying = underlying;
    }

    @Override
    public int size() {
        return underlying.size();
    }

    @Override
    public A unsafeGet(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return underlying.unsafeGet(size() - 1 - index);
    }

    static <A> NonEmptyVector<A> reverseVector(NonEmptyVector<A> underlying) {
        if (underlying.size() < 2) {
            return underlying;
        } else if (underlying instanceof ReverseVector<?>) {
            return ((ReverseVector<A>) underlying).underlying;
        } else {
            return new ReverseVector<>(underlying);
        }
    }

}
