package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;

final class CrossJoinVector<A, B> extends ConcreteVector<Tuple2<A, B>>
        implements NonEmptyVector<Tuple2<A, B>> {

    private final NonEmptyVector<A> first;
    private final NonEmptyVector<B> second;
    private final int size;
    private final int stride;

    private CrossJoinVector(NonEmptyVector<A> first, NonEmptyVector<B> second) {
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
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return tuple(first.unsafeGet(index / stride),
                second.unsafeGet(index % stride));
    }

    static <A, B> CrossJoinVector<A, B> crossJoinVector(NonEmptyVector<A> first, NonEmptyVector<B> second) {
        return new CrossJoinVector<>(first, second);
    }

}
