package dev.marksman.collectionviews;

final class ImmutableVectorCons<A> extends ConcreteVector<A> implements ImmutableNonEmptyVector<A> {
    private final A head;
    private final ImmutableVector<A> tail;

    ImmutableVectorCons(A head, ImmutableVector<A> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public A head() {
        return head;
    }

    @Override
    public int size() {
        return 1 + tail.size();
    }

    @Override
    public A unsafeGet(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            return head;
        } else {
            return tail.unsafeGet(index - 1);
        }
    }

}
