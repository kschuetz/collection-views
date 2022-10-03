package software.kes.collectionviews;

import com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection;

import java.util.ArrayList;
import java.util.List;

final class WrappedListVector<A> extends ConcreteVector<A>
        implements NonEmptyVector<A>, Primitive {
    /**
     * underlying must contain at least one element
     */
    private final List<A> underlying;

    WrappedListVector(List<A> underlying) {
        this.underlying = underlying;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return underlying.size();
    }

    @Override
    public A unsafeGet(int index) {
        return underlying.get(index);
    }

    @Override
    public ImmutableNonEmptyVector<A> toImmutable() {
        ArrayList<A> copied = ToCollection.toCollection(ArrayList::new, underlying);
        return new ImmutableListVector<>(copied);
    }

}
