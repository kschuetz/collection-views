package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.builtin.fn2.Take;

import java.util.HashSet;
import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;
import static dev.marksman.collectionviews.Validation.validateCopyFrom;

final class ImmutableSets {

    private ImmutableSets() {

    }

    static <A> ImmutableSet<A> copyFrom(A[] source) {
        Objects.requireNonNull(source);
        return copyFrom(source.length, source);
    }

    static <A> ImmutableSet<A> copyFrom(Iterable<A> source) {
        Objects.requireNonNull(source);
        if (source instanceof ImmutableSet<?>) {
            return (ImmutableSet<A>) source;
        } else if (!source.iterator().hasNext()) {
            return Sets.empty();
        } else {
            return new ImmutableWrappedSet<>(toCollection(HashSet::new, source));
        }
    }

    static <A> ImmutableSet<A> copyFrom(int maxCount, A[] source) {
        validateCopyFrom(maxCount, source);
        return copyFrom(maxCount, Vector.wrap(source));
    }

    static <A> ImmutableSet<A> copyFrom(int maxCount, Iterable<A> source) {
        validateCopyFrom(maxCount, source);
        if (maxCount == 0) {
            return Sets.empty();
        } else if (source instanceof ImmutableSet<?>) {
            return copyFrom(maxCount, (ImmutableSet<A>) source);
        } else {
            return copyFrom(Take.take(maxCount, source));
        }
    }

    private static <A> ImmutableSet<A> copyFrom(int maxCount, ImmutableSet<A> source) {
        if (source.size() <= maxCount) {
            return source;
        } else {
            return copyFrom(Take.take(maxCount, source));
        }
    }

    static <A> ImmutableSet<A> ensureImmutable(Set<A> set) {
        if (set instanceof ImmutableSet<?>) {
            return (ImmutableSet<A>) set;
        } else if (set.isEmpty()) {
            return Sets.empty();
        } else {
            HashSet<A> copied = toCollection(HashSet::new, set);
            return new ImmutableWrappedSet<>(copied);
        }
    }

    static <A> ImmutableNonEmptySet<A> ensureImmutable(NonEmptySet<A> set) {
        if (set instanceof ImmutableNonEmptySet<?>) {
            return (ImmutableNonEmptySet<A>) set;
        } else {
            HashSet<A> copied = toCollection(HashSet::new, set);
            return new ImmutableWrappedSet<>(copied);
        }
    }

    static <A> Maybe<ImmutableNonEmptySet<A>> maybeNonEmptyConvert(ImmutableSet<A> source) {
        Objects.requireNonNull(source);
        if (source instanceof ImmutableNonEmptySet<?>) {
            return just((ImmutableNonEmptySet<A>) source);
        } else if (!source.isEmpty()) {
            return just(new ImmutableNonEmptySetAdapter<>(source));
        } else {
            return nothing();
        }
    }

    static <A> Maybe<ImmutableNonEmptySet<A>> maybeNonEmptyCopyFrom(A[] source) {
        Objects.requireNonNull(source);
        return maybeNonEmptyCopyFrom(source.length, source);
    }

    @SuppressWarnings("unchecked")
    static <A> Maybe<ImmutableNonEmptySet<A>> maybeNonEmptyCopyFrom(Iterable<A> source) {
        Objects.requireNonNull(source);
        if (!source.iterator().hasNext()) {
            return nothing();
        } else {
            return (Maybe<ImmutableNonEmptySet<A>>) copyFrom(source).toNonEmpty();
        }
    }

    @SuppressWarnings("unchecked")
    static <A> Maybe<ImmutableNonEmptySet<A>> maybeNonEmptyCopyFrom(int maxCount, A[] source) {
        validateCopyFrom(maxCount, source);
        if (source.length == 0 || maxCount == 0) {
            return nothing();
        } else {
            return (Maybe<ImmutableNonEmptySet<A>>) copyFrom(maxCount, source).toNonEmpty();
        }
    }

    @SuppressWarnings("unchecked")
    static <A> Maybe<ImmutableNonEmptySet<A>> maybeNonEmptyCopyFrom(int maxCount, Iterable<A> source) {
        validateCopyFrom(maxCount, source);
        if (maxCount == 0 || !source.iterator().hasNext()) {
            return nothing();
        } else {
            return (Maybe<ImmutableNonEmptySet<A>>) copyFrom(maxCount, source).toNonEmpty();
        }
    }

    static <A> ImmutableNonEmptySet<A> nonEmptyConvertOrThrow(ImmutableSet<A> source) {
        return getNonEmptyOrThrow(maybeNonEmptyConvert(source));
    }

    static <A> ImmutableNonEmptySet<A> nonEmptyCopyFromOrThrow(A[] source) {
        return getNonEmptyOrThrow(maybeNonEmptyCopyFrom(source));
    }

    static <A> ImmutableNonEmptySet<A> nonEmptyCopyFromOrThrow(Iterable<A> source) {
        return getNonEmptyOrThrow(maybeNonEmptyCopyFrom(source));
    }

    static <A> ImmutableNonEmptySet<A> nonEmptyCopyFromOrThrow(int maxCount, A[] source) {
        return getNonEmptyOrThrow(maybeNonEmptyCopyFrom(maxCount, source));
    }

    static <A> ImmutableNonEmptySet<A> nonEmptyCopyFromOrThrow(int maxCount, Iterable<A> source) {
        return getNonEmptyOrThrow(maybeNonEmptyCopyFrom(maxCount, source));
    }

    private static <A> ImmutableNonEmptySet<A> getNonEmptyOrThrow(Maybe<ImmutableNonEmptySet<A>> maybeResult) {
        return maybeResult.orElseThrow(Sets.nonEmptyError());
    }

}
