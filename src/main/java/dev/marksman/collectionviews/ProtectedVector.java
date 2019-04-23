package dev.marksman.collectionviews;

public interface ProtectedVector<A> extends Vector<A> {
    @Override
    default boolean ownsAllReferencesToUnderlying() {
        return true;
    }

    @Override
    default ProtectedVector<A> ensureProtected() {
        return this;
    }
}
