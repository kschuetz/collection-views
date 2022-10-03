package software.kes.collectionviews;

import static software.kes.collectionviews.ImmutableVectorSlice.immutableVectorSlice;

final class VectorSlice<A> extends ConcreteVector<A> {
    private final int offset;
    private final int size;
    private final Vector<A> underlying;

    private VectorSlice(int offset, int size, Vector<A> underlying) {
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

    static <A> Vector<A> vectorSlice(int offset, int size, Vector<A> underlying) {
        if (underlying instanceof VectorSlice<?>) {
            VectorSlice<A> underlyingSlice = (VectorSlice<A>) underlying;
            int endIndex = Math.min(offset + size, underlyingSlice.size);
            return new VectorSlice<>(offset + underlyingSlice.offset, endIndex - offset, underlyingSlice.underlying);
        } else if (underlying instanceof ImmutableVectorSlice<?>) {
            return immutableVectorSlice(offset, size, (ImmutableVectorSlice<A>) underlying);
        } else {
            return new VectorSlice<>(offset, size, underlying);
        }
    }

}
