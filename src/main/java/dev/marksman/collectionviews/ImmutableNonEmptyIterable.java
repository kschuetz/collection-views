package dev.marksman.collectionviews;

public interface ImmutableNonEmptyIterable<A> extends ImmutableIterable<A>, NonEmptyIterable<A> {
    @Override
    ImmutableIterable<A> tail();
}
