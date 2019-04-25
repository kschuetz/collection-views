package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

import java.util.List;

public interface ImmutableVector<A> extends Vector<A>, Immutable {

    @Override
    default ImmutableVector<A> ensureImmutable() {
        return this;
    }

    @Override
    default ImmutableVector<A> tail() {
        return drop(1);
    }

    @Override
    default <B> ImmutableVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return Vectors.immutableMap(f, this);
    }

    @Override
    default ImmutableVector<A> take(int count) {
        return Vectors.immutableTake(count, this);
    }

    @Override
    default ImmutableVector<A> drop(int count) {
        return Vectors.immutableDrop(count, this);
    }

    @Override
    default ImmutableVector<A> slice(int startIndex, int endIndexExclusive) {
        return Vectors.immutableSlice(startIndex, endIndexExclusive, this);
    }

    static <A> ImmutableVector<A> wrapAndVouchFor(A[] underlying) {
        return Vectors.immutableWrap(underlying);
    }

    static <A> ImmutableVector<A> wrapAndVouchFor(List<A> underlying) {
        return Vectors.immutableWrap(underlying);
    }
}
