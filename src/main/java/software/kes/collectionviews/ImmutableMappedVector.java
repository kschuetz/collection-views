package software.kes.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

final class ImmutableMappedVector<A> extends ConcreteVector<A>
        implements ImmutableNonEmptyVector<A> {

    private final MapperChain mapper;
    private final ImmutableNonEmptyVector<Object> underlying;

    ImmutableMappedVector(MapperChain mapper, ImmutableNonEmptyVector<Object> underlying) {
        this.mapper = mapper;
        this.underlying = underlying;
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
    public <B> ImmutableMappedVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return new ImmutableMappedVector<>(mapper.add((Fn1<Object, Object>) f),
                underlying);
    }

}
