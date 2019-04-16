package dev.marksman.collectionviews.concrete;

import dev.marksman.collectionviews.NonEmptyVector;
import dev.marksman.collectionviews.Vector;

class ConcreteNonEmptyVector<A> implements NonEmptyVector<A> {
    private final A head;
    private final Vector<A> tail;

    ConcreteNonEmptyVector(A head, Vector<A> tail) {
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
        if (index < 0 || index >= size()) throw new IndexOutOfBoundsException();
        if (index == 0) {
            return head;
        } else {
            return tail.unsafeGet(index - 1);
        }
    }
}
