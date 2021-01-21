package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import dev.marksman.enhancediterables.FiniteIterable;
import dev.marksman.enhancediterables.NonEmptyFiniteIterable;

import java.util.Collection;
import java.util.Objects;

import static dev.marksman.collectionviews.ConcreteSetBuilder.concreteSetBuilder;

final class EmptySetBuilder<A> implements SetBuilder<A> {
    private final Maybe<Integer> initialCapacity;

    private EmptySetBuilder(Maybe<Integer> initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    @Override
    public NonEmptySetBuilder<A> add(A element) {
        return concreteSetBuilder(initialCapacity, element);
    }

    @Override
    public SetBuilder<A> addAll(Collection<A> elements) {
        Objects.requireNonNull(elements);
        if (elements.isEmpty()) {
            return this;
        } else {
            return concreteSetBuilder(initialCapacity, elements);
        }
    }

    @Override
    public SetBuilder<A> addAll(FiniteIterable<A> elements) {
        Objects.requireNonNull(elements);
        if (elements.isEmpty()) {
            return this;
        } else {
            return concreteSetBuilder(initialCapacity, elements);
        }
    }

    @Override
    public NonEmptySetBuilder<A> addAll(NonEmptyFiniteIterable<A> elements) {
        Objects.requireNonNull(elements);
        return concreteSetBuilder(initialCapacity, elements);
    }

    @Override
    public ImmutableSet<A> build() {
        return Set.empty();
    }

    static <A> EmptySetBuilder<A> emptySetBuilder(Maybe<Integer> initialCapacity) {
        return new EmptySetBuilder<>(initialCapacity.filter(n -> n >= 0));
    }

}
