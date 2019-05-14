package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn2;

final class ImmutableVectorZip<A, B, R> extends ConcreteVector<R>
        implements ImmutableNonEmptyVector<R> {

    private final Fn2<A, B, R> fn;
    private final ImmutableNonEmptyVector<A> first;
    private final ImmutableNonEmptyVector<B> second;
    private final int size;

    private ImmutableVectorZip(Fn2<A, B, R> fn, ImmutableNonEmptyVector<A> first, ImmutableNonEmptyVector<B> second) {
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

    static <A, B, R> ImmutableVectorZip<A, B, R> immutableVectorZip(Fn2<A, B, R> fn,
                                                                    ImmutableNonEmptyVector<A> first, ImmutableNonEmptyVector<B> second) {
        return new ImmutableVectorZip<>(fn, first, second);
    }

}
