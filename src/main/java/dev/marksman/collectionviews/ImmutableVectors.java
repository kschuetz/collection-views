package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;

import java.util.List;
import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static dev.marksman.collectionviews.MapperChain.mapperChain;

class ImmutableVectors {
    static <A> ImmutableVector<A> take(int count, ImmutableVector<A> source) {
        if (count < 0) throw new IllegalArgumentException("count must be >= 0");
        return slice(0, count, source);
    }

    static <A> ImmutableVector<A> slice(int startIndex, int endIndexExclusive, ImmutableVector<A> source) {
        if (startIndex < 0) throw new IllegalArgumentException("startIndex must be >= 0");
        if (endIndexExclusive < 0) throw new IllegalArgumentException("endIndex must be >= 0");
        Objects.requireNonNull(source);
        int requestedSize = endIndexExclusive - startIndex;
        if (requestedSize < 1) {
            return Vectors.empty();
        }
        int sourceSize = source.size();
        if (startIndex == 0 && requestedSize >= sourceSize) {
            return source;
        } else if (startIndex >= sourceSize) {
            return Vectors.empty();
        } else {
            int available = Math.max(sourceSize - startIndex, 0);
            int sliceSize = Math.min(available, requestedSize);
            return new ImmutableVectorSlice<>(startIndex, sliceSize, source);
        }
    }

    static <A> Maybe<ImmutableNonEmptyVector<A>> tryNonEmptyWrap(ImmutableVector<A> vec) {
        Objects.requireNonNull(vec);
        if (vec instanceof NonEmptyVector<?>) {
            return just((ImmutableNonEmptyVector<A>) vec);
        } else if (!vec.isEmpty()) {
            return just(new ImmutableVectorCons<>(vec.unsafeGet(0), vec.tail()));
        } else {
            return nothing();
        }
    }

    static <A, B> ImmutableVector<B> map(Fn1<? super A, ? extends B> f, ImmutableVector<A> source) {
        return tryNonEmptyWrap(source)
                .match(__ -> Vectors.empty(),
                        nonEmpty -> mapNonEmpty(f, nonEmpty));
    }

    @SuppressWarnings("unchecked")
    static <A, B> ImmutableNonEmptyVector<B> mapNonEmpty(Fn1<? super A, ? extends B> f, ImmutableNonEmptyVector<A> source) {
        return new ImmutableMappedVector<>(mapperChain((Fn1<Object, Object>) f),
                (ImmutableNonEmptyVector<Object>) source);
    }

    static <A> ImmutableVector<A> wrapAndVouchFor(A[] arr) {
        Objects.requireNonNull(arr);
        if (arr.length == 0) {
            return Vectors.empty();
        } else {
            return new ImmutableArrayVector<>(arr);
        }
    }

    static <A> ImmutableVector<A> wrapAndVouchFor(List<A> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return Vectors.empty();
        } else {
            return new ImmutableListVector<>(list);
        }
    }

    static <A> ImmutableNonEmptyVector<A> getNonEmptyOrThrow(Maybe<ImmutableNonEmptyVector<A>> maybeResult) {
        return maybeResult.orElseThrow(() -> {
            throw new IllegalArgumentException("Cannot construct NonEmptyVector from empty input");
        });
    }
}
