package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn0;

import java.util.Arrays;
import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;

class Sets {

    static <A> ImmutableSet<A> empty() {
        return EmptySet.emptySet();
    }

    static <A> Maybe<NonEmptySet<A>> maybeNonEmptyWrap(java.util.Set<A> underlying) {
        Objects.requireNonNull(underlying);
        if (underlying.isEmpty()) {
            return nothing();
        } else {
            return just(new WrappedSet<>(underlying));
        }
    }

    static <A> Maybe<NonEmptySet<A>> maybeNonEmptyWrap(Set<A> underlying) {
        if (underlying instanceof NonEmptySet<?>) {
            return just((NonEmptySet<A>) underlying);
        } else if (!underlying.isEmpty()) {
            return just(new NonEmptySetAdapter<>(underlying));
        } else {
            return nothing();
        }
    }

    static Fn0<IllegalArgumentException> nonEmptyError() {
        return () -> new IllegalArgumentException("Cannot construct NonEmptySet from empty input");
    }

    @SuppressWarnings("varargs")
    @SafeVarargs
    static <A> ImmutableNonEmptySet<A> nonEmptySetOf(A first, A... more) {
        java.util.Set<A> underlying = new java.util.HashSet<>();
        underlying.add(first);
        underlying.addAll(Arrays.asList(more));
        return new ImmutableWrappedSet<>(underlying);
    }

    static <A> NonEmptySet<A> nonEmptyWrapOrThrow(java.util.Set<A> underlying) {
        return getNonEmptyOrThrow(maybeNonEmptyWrap(underlying));
    }

    static <A> NonEmptySet<A> nonEmptyWrapOrThrow(Set<A> underlying) {
        return getNonEmptyOrThrow(maybeNonEmptyWrap(underlying));
    }

    static <A> Set<A> wrap(java.util.Set<A> underlying) {
        Objects.requireNonNull(underlying);
        if (underlying.isEmpty()) {
            return empty();
        } else {
            return new WrappedSet<>(underlying);
        }
    }

    private static <A> NonEmptySet<A> getNonEmptyOrThrow(Maybe<NonEmptySet<A>> maybeResult) {
        return maybeResult.orElseThrow(nonEmptyError());
    }

}
