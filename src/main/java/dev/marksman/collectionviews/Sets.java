package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;

import java.util.Arrays;
import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;

class Sets {

    static <A> Set<A> empty() {
        return EmptySet.emptySet();
    }

    static <A> Set<A> wrap(java.util.Set<A> underlying) {
        Objects.requireNonNull(underlying);
        if (underlying.isEmpty()) {
            return empty();
        } else {
            return new SetWrappedSet<>(underlying);
        }
    }

    @SafeVarargs
    static <A> NonEmptySet<A> nonEmptySetOf(A first, A... more) {
        java.util.Set<A> underlying = new java.util.HashSet<>();
        underlying.add(first);
        underlying.addAll(Arrays.asList(more));
        return new SetWrappedSet<>(underlying);
    }

    static <A> NonEmptySet<A> nonEmptyWrap(A first, java.util.Set<A> more) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(more);
        SetWrappedSet<A> tail = new SetWrappedSet<>(more);
        if (more.contains(first)) {
            return tail;
        } else {
            return new SetCons<>(first, tail);
        }
    }

    static <A> NonEmptySet<A> nonEmptyWrap(A first, Set<A> more) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(more);
        if (more.contains(first)) {
            // we know it's non-empty, so it this is safe:
            return nonEmptyWrapOrThrow(more);
        } else {
            return new SetCons<>(first, more);
        }
    }

    static <A> Maybe<NonEmptySet<A>> tryNonEmptyWrap(java.util.Set<A> underlying) {
        Objects.requireNonNull(underlying);
        if (underlying.isEmpty()) {
            return nothing();
        } else {
            return just(new SetWrappedSet<>(underlying));
        }
    }

    static <A> Maybe<NonEmptySet<A>> tryNonEmptyWrap(Set<A> underlying) {
        if (underlying instanceof NonEmptySet<?>) {
            return just((NonEmptySet<A>) underlying);
        } else if (!underlying.isEmpty()) {
            return just(new NonEmptySetAdapter<>(underlying));
        } else {
            return nothing();
        }
    }

    static <A> NonEmptySet<A> nonEmptyWrapOrThrow(java.util.Set<A> underlying) {
        return getNonEmptyOrThrow(tryNonEmptyWrap(underlying));
    }

    static <A> NonEmptySet<A> nonEmptyWrapOrThrow(Set<A> underlying) {
        return getNonEmptyOrThrow(tryNonEmptyWrap(underlying));
    }

    private static <A> NonEmptySet<A> getNonEmptyOrThrow(Maybe<NonEmptySet<A>> maybeResult) {
        return maybeResult.orElseThrow(() -> {
            throw new IllegalArgumentException("Cannot construct NonEmptySet from empty input");
        });
    }
}
