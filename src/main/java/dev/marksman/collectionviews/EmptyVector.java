package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;

import java.util.Iterator;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.util.Collections.emptyIterator;

final class EmptyVector<A> extends ConcreteVector<A> implements ImmutableVector<A> {
    private static final EmptyVector<?> INSTANCE = new EmptyVector<>();

    @Override
    public int size() {
        return 0;
    }

    @Override
    public A unsafeGet(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ImmutableVector<A> tail() {
        return this;
    }

    @Override
    public Maybe<A> get(int index) {
        return nothing();
    }

    @Override
    public <B> ImmutableVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return emptyVector();
    }

    @Override
    public Iterator<A> iterator() {
        return emptyIterator();
    }

    @Override
    public Maybe<? extends ImmutableNonEmptyVector<A>> toNonEmpty() {
        return nothing();
    }

    @Override
    public ImmutableNonEmptyVector<A> toNonEmptyOrThrow() {
        throw Vectors.nonEmptyError().get();
    }

    @SuppressWarnings("unchecked")
    static <A> EmptyVector<A> emptyVector() {
        return (EmptyVector<A>) INSTANCE;
    }

}
