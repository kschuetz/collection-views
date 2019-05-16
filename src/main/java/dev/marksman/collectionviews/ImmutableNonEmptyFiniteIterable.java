package dev.marksman.collectionviews;

public interface ImmutableNonEmptyFiniteIterable<A> extends ImmutableFiniteIterable<A>, NonEmptyFiniteIterable<A> {

    @Override
    ImmutableFiniteIterable<A> tail();

}
