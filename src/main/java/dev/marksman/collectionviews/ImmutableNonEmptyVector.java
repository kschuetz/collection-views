package dev.marksman.collectionviews;

public interface ImmutableNonEmptyVector<A> extends NonEmptyVector<A>, ImmutableVector<A> {

    @Override
    default ImmutableVector<A> tail() {
        return drop(1);
    }

    @Override
    default ImmutableNonEmptyVector<A> ensureImmutable() {
        return this;
    }
}
