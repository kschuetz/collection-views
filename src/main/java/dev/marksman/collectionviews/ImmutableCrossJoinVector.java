package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;

final class ImmutableCrossJoinVector<A, B> extends ConcreteVector<Tuple2<A, B>>
        implements ImmutableNonEmptyVector<Tuple2<A, B>> {

    private final ImmutableNonEmptyVector<A> first;
    private final ImmutableNonEmptyVector<B> second;
    private final int size;
    private final int stride;

    private ImmutableCrossJoinVector(ImmutableNonEmptyVector<A> first, ImmutableNonEmptyVector<B> second) {
        this.first = first;
        this.second = second;
        this.stride = second.size();
        this.size = first.size() * stride;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Tuple2<A, B> unsafeGet(int index) {
        if (index <= 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return tuple(first.unsafeGet(index / stride),
                second.unsafeGet(index % stride));
    }

    static <A, B> ImmutableCrossJoinVector<A, B> immutableCrossJoinVector(ImmutableNonEmptyVector<A> first, ImmutableNonEmptyVector<B> second) {
        return new ImmutableCrossJoinVector<>(first, second);
    }

}
