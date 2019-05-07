package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;

final class ImmutableVectorZipWithIndex<A> extends ConcreteVector<Tuple2<A, Integer>>
        implements ImmutableNonEmptyVector<Tuple2<A, Integer>> {

    private final ImmutableNonEmptyVector<A> underlying;

    ImmutableVectorZipWithIndex(ImmutableNonEmptyVector<A> underlying) {
        this.underlying = underlying;
    }

    @Override
    public Tuple2<A, Integer> head() {
        return unsafeGet(0);
    }

    @Override
    public int size() {
        return underlying.size();
    }

    @Override
    public Tuple2<A, Integer> unsafeGet(int index) {
        if (index < 0 || index >= size()) {
            throw new IllegalArgumentException();
        }
        return tuple(underlying.unsafeGet(index), index);
    }

}
