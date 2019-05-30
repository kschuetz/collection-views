package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import dev.marksman.enhancediterables.FiniteIterable;
import dev.marksman.enhancediterables.NonEmptyFiniteIterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

final class ConcreteVectorBuilder<A> implements NonEmptyVectorBuilder<A> {
    private final Maybe<Integer> initialCapacity;
    private final ArrayList<A> underlying;
    private final int size;

    private ConcreteVectorBuilder(int size, Maybe<Integer> initialCapacity, ArrayList<A> underlying) {
        this.initialCapacity = initialCapacity;
        this.underlying = underlying;
        this.size = size;
    }

    @Override
    public NonEmptyVectorBuilder<A> add(A element) {
        ArrayList<A> nextUnderlying = getNextUnderlying();
        nextUnderlying.add(element);
        return concreteVectorBuilder(nextUnderlying.size(), initialCapacity, nextUnderlying);
    }

    @Override
    public NonEmptyVectorBuilder<A> addAll(Collection<A> elements) {
        Objects.requireNonNull(elements);
        if (elements.isEmpty()) {
            return this;
        } else {
            ArrayList<A> nextUnderlying = getNextUnderlying();
            nextUnderlying.addAll(elements);

            return concreteVectorBuilder(nextUnderlying.size(), initialCapacity, nextUnderlying);
        }
    }

    @Override
    public NonEmptyVectorBuilder<A> addAll(FiniteIterable<A> elements) {
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
    public ImmutableNonEmptyVector<A> build() {
        return ImmutableVectors.wrapAndVouchFor(underlying).take(size).toNonEmptyOrThrow();
    }

    private NonEmptyVectorBuilder<A> addAllImpl(Iterable<A> elements) {
        ArrayList<A> nextUnderlying = getNextUnderlying();
        for (A element : elements) {
            nextUnderlying.add(element);
        }

        return concreteVectorBuilder(nextUnderlying.size(), initialCapacity, nextUnderlying);
    }

    private ArrayList<A> getNextUnderlying() {
        // If the size has changed, we need to make a copy
        if (underlying.size() == size) {
            return underlying;
        } else {
            ArrayList<A> newUnderlying = initialCapacity.match(__ -> new ArrayList<>(), ArrayList::new);
            for (int i = 0; i < size; i++) {
                newUnderlying.add(underlying.get(i));
            }
            return newUnderlying;
        }
    }

    static <A> ConcreteVectorBuilder<A> concreteVectorBuilder(int size, Maybe<Integer> initialCapacity, ArrayList<A> underlying) {
        assert (size >= 1);
        return new ConcreteVectorBuilder<>(size, initialCapacity, underlying);
    }

    static <A> ConcreteVectorBuilder<A> concreteVectorBuilder(Maybe<Integer> initialCapacity, A firstElement) {
        ArrayList<A> underlying = initialCapacity.match(__ -> new ArrayList<>(), ArrayList::new);
        underlying.add(firstElement);
        return new ConcreteVectorBuilder<>(1, initialCapacity, underlying);
    }

}
