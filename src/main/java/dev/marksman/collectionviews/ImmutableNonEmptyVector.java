package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;

import static com.jnape.palatable.lambda.adt.Maybe.just;

/**
 * A {@link Vector} that is guaranteed at compile-time to be non-empty, and safe from mutation anywhere, and non-empty.
 * In other words, it owns the sole reference to the underlying collection.
 * <p>
 * In addition to the guarantees of {@link Vector}, {@link NonEmptyVector}, and {@link ImmutableVector},
 * provides the following benefits:
 * <ul>
 * <li>{@link ImmutableNonEmptyVector#fmap} always returns a {@code ImmutableNonEmptyVector}.</li>
 * </ul>
 *
 * @param <A> the element type
 */
public interface ImmutableNonEmptyVector<A> extends NonEmptyVector<A>, ImmutableVector<A> {

    @Override
    default ImmutableVector<A> tail() {
        return drop(1);
    }

    @Override
    default <B> ImmutableNonEmptyVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return ImmutableVectors.mapNonEmpty(f, this);
    }

    @Override
    default ImmutableNonEmptyVector<A> toImmutable() {
        return this;
    }

    @Override
    default Maybe<? extends ImmutableNonEmptyVector<A>> toNonEmpty() {
        return just(this);
    }

    @Override
    default ImmutableNonEmptyVector<A> toNonEmptyOrThrow() {
        return this;
    }
}
