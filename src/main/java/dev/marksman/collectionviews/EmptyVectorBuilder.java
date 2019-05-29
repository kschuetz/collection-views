package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import dev.marksman.enhancediterables.FiniteIterable;
import dev.marksman.enhancediterables.NonEmptyFiniteIterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static dev.marksman.collectionviews.ConcreteVectorBuilder.concreteVectorBuilder;

final class EmptyVectorBuilder<A> implements VectorBuilder<A> {
    private final Maybe<Integer> initialCapacity;

    private EmptyVectorBuilder(Maybe<Integer> initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    @Override
    public NonEmptyVectorBuilder<A> add(A element) {
        ArrayList<A> underlying = createNewUnderlying();
        underlying.add(element);

        return concreteVectorBuilder(underlying.size(), initialCapacity, underlying);
    }

    @Override
    public VectorBuilder<A> addAll(Collection<A> elements) {
        Objects.requireNonNull(elements);
        if (elements.isEmpty()) {
            return this;
        } else {
            ArrayList<A> underlying = createNewUnderlying();
            underlying.addAll(elements);

            return concreteVectorBuilder(underlying.size(), initialCapacity, underlying);
        }
    }

    @Override
    public VectorBuilder<A> addAll(FiniteIterable<A> elements) {
        Objects.requireNonNull(elements);
        if (elements.isEmpty()) {
            return this;
        } else {
            return addAllImpl(elements);
        }
    }

    @Override
    public NonEmptyVectorBuilder<A> addAll(NonEmptyFiniteIterable<A> elements) {
        return addAllImpl(elements);
    }

    @Override
    public ImmutableVector<A> build() {
        return Vector.empty();
    }

    private NonEmptyVectorBuilder<A> addAllImpl(FiniteIterable<A> elements) {
        ArrayList<A> underlying = createNewUnderlying();
        for (A element : elements) {
            underlying.add(element);
        }

        return concreteVectorBuilder(underlying.size(), initialCapacity, underlying);
    }

    private ArrayList<A> createNewUnderlying() {
        return initialCapacity.match(__ -> new ArrayList<>(), ArrayList::new);
    }

    static <A> EmptyVectorBuilder<A> emptyVectorBuilder(Maybe<Integer> initialCapacity) {
        return new EmptyVectorBuilder<>(initialCapacity.filter(n -> n >= 0));
    }

}
