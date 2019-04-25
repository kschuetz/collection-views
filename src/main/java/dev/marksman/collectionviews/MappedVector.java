package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

class MappedVector<A> implements NonEmptyVector<A> {
    private final MapperChain mapper;
    private final NonEmptyVector<Object> underlying;

    MappedVector(MapperChain mapper, NonEmptyVector<Object> underlying) {
        this.mapper = mapper;
        this.underlying = underlying;
    }

    @SuppressWarnings("unchecked")
    @Override
    public A head() {
        return (A) mapper.apply(underlying.head());
    }

    @Override
    public int size() {
        return underlying.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public A unsafeGet(int index) {
        return (A) mapper.apply(underlying.unsafeGet(index));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> NonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return new MappedVector<>(mapper.add((Fn1<Object, Object>) f),
                underlying);
    }
}
