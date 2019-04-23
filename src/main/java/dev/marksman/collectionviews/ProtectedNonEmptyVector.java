package dev.marksman.collectionviews;

public interface ProtectedNonEmptyVector<A> extends NonEmptyVector<A>, ProtectedVector<A> {
    @Override
    default boolean ownsAllReferencesToUnderlying() {
        return true;
    }

    @Override
    default ProtectedNonEmptyVector<A> ensureProtected() {
        return this;
    }
}
