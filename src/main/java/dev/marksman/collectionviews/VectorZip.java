package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn2;

final class VectorZip<A, B, R> extends ConcreteVector<R>
        implements NonEmptyVector<R> {

    private final Fn2<A, B, R> fn;
    private final NonEmptyVector<A> first;
    private final NonEmptyVector<B> second;
    private final int size;

    private VectorZip(Fn2<A, B, R> fn, NonEmptyVector<A> first, NonEmptyVector<B> second) {
        this.fn = fn;
        this.first = first;
        this.second = second;
        this.size = Math.min(first.size(), second.size());
    }

    @Override
    public R head() {
        return unsafeGet(0);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public R unsafeGet(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return fn.apply(first.unsafeGet(index), second.unsafeGet(index));
    }

    static <A, B, R> VectorZip<A, B, R> vectorZip(Fn2<A, B, R> fn, NonEmptyVector<A> first, NonEmptyVector<B> second) {
        return new VectorZip<>(fn, first, second);
    }

}
