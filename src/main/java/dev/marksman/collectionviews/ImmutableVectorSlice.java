package dev.marksman.collectionviews;

final class ImmutableVectorSlice<A> extends ConcreteVector<A>
        implements ImmutableVector<A> {
    private final int offset;
    private final int size;
    private final ImmutableVector<A> underlying;

    private ImmutableVectorSlice(int offset, int size, ImmutableVector<A> underlying) {
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
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return underlying.unsafeGet(offset + index);
    }

    static <A> ImmutableVector<A> immutableVectorSlice(int offset, int size, ImmutableVector<A> underlying) {
        if (underlying instanceof ImmutableVectorSlice<?>) {
            ImmutableVectorSlice<A> underlyingSlice = (ImmutableVectorSlice<A>) underlying;
            int endIndex = Math.min(offset + size, underlyingSlice.size);
            return new ImmutableVectorSlice<>(offset + underlyingSlice.offset, endIndex - offset, underlyingSlice.underlying);
        } else {
            return new ImmutableVectorSlice<>(offset, size, underlying);
        }
    }

}
