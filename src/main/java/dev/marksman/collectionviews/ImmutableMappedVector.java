package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

class ImmutableMappedVector<A> implements ImmutableNonEmptyVector<A> {
    private final MapperChain mapper;
    private final ImmutableNonEmptyVector<Object> underlying;

    ImmutableMappedVector(MapperChain mapper, ImmutableNonEmptyVector<Object> underlying) {
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

    @Override
    public <B> ImmutableNonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        // TODO: future proof this from upcoming lambda changes
        return new ImmutableMappedVector<>(mapper.add(f), underlying);
    }
}
