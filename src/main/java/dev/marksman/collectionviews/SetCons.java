package dev.marksman.collectionviews;


import java.util.Objects;

class SetCons<A> implements NonEmptySet<A> {
    /**
     * `tail` must not contain `head`.
     * Use `SetWrappedSet` in that case.
     */
    private final A head;
    private final Set<A> tail;

    SetCons(A head, Set<A> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public A head() {
        return head;
    }

    @Override
    public Iterable<A> tail() {
        return tail;
    }

    @Override
    public int size() {
        return 1 + tail.size();
    }

    @Override
    public boolean contains(A element) {
        Objects.requireNonNull(element);
        return element.equals(head) || tail.contains(element);
    }

}
