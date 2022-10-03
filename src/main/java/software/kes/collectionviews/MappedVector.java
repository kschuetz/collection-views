package software.kes.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

final class MappedVector<A> extends ConcreteVector<A> implements NonEmptyVector<A> {
    private final MapperChain mapper;
    private final NonEmptyVector<Object> underlying;

    MappedVector(MapperChain mapper, NonEmptyVector<Object> underlying) {
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
    public <B> NonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return new MappedVector<>(mapper.add((Fn1<Object, Object>) f),
                underlying);
    }
}
