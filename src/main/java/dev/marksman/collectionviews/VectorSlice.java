package dev.marksman.collectionviews;

class VectorSlice<A> implements Vector<A> {
    private final int offset;
    private final int size;
    private final Vector<A> underlying;

    VectorSlice(int offset, int size, Vector<A> underlying) {
        this.offset = offset;
        this.size = size;
        this.underlying = underlying;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public A unsafeGet(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return underlying.unsafeGet(offset + index);
    }
}
