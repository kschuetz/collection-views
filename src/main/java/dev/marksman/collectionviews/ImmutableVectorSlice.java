package dev.marksman.collectionviews;

class ImmutableVectorSlice<A> implements ImmutableVector<A> {
    private final int offset;
    private final int size;
    private final ImmutableVector<A> underlying;

    ImmutableVectorSlice(int offset, int size, ImmutableVector<A> underlying) {
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
