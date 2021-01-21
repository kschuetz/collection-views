package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import dev.marksman.enhancediterables.FiniteIterable;
import dev.marksman.enhancediterables.NonEmptyFiniteIterable;

import java.util.Collection;
import java.util.Objects;

import static dev.marksman.collectionviews.ConcreteVectorBuilder.concreteVectorBuilder;

final class ConcreteSetBuilder<A> implements NonEmptySetBuilder<A> {
    private final NonEmptyVectorBuilder<A> underlying;

    private ConcreteSetBuilder(NonEmptyVectorBuilder<A> underlying) {
        this.underlying = underlying;
    }

    @Override
    public NonEmptySetBuilder<A> add(A element) {
        return concreteSetBuilder(underlying.add(element));
    }

    @Override
    public NonEmptySetBuilder<A> addAll(Collection<A> elements) {
        Objects.requireNonNull(elements);
        if (elements.isEmpty()) {
            return this;
        } else {
            return concreteSetBuilder(underlying.addAll(elements));
        }
    }

    @Override
    public NonEmptySetBuilder<A> addAll(FiniteIterable<A> elements) {
        Objects.requireNonNull(elements);
        if (elements.isEmpty()) {
            return this;
        } else {
            return concreteSetBuilder(underlying.addAll(elements));
        }
    }

    @Override
    public NonEmptySetBuilder<A> addAll(NonEmptyFiniteIterable<A> elements) {
        Objects.requireNonNull(elements);
        return concreteSetBuilder(underlying.addAll(elements));
    }

    @Override
    public ImmutableNonEmptySet<A> build() {
        return ImmutableSets.copyFrom(underlying.build()).toNonEmptyOrThrow();
    }

    static <A> ConcreteSetBuilder<A> concreteSetBuilder(NonEmptyVectorBuilder<A> underlying) {
        return new ConcreteSetBuilder<>(underlying);
    }

    static <A> ConcreteSetBuilder<A> concreteSetBuilder(Maybe<Integer> initialCapacity, A firstElement) {
        return concreteSetBuilder(concreteVectorBuilder(initialCapacity, firstElement));
    }

    static <A> ConcreteSetBuilder<A> concreteSetBuilder(Maybe<Integer> initialCapacity, Iterable<A> elements) {
        return concreteSetBuilder(concreteVectorBuilder(initialCapacity, elements));
    }

}
