package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.lambda.functions.builtin.fn2.Drop;
import com.jnape.palatable.lambda.functions.builtin.fn2.Take;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;
import static dev.marksman.collectionviews.MapperChain.mapperChain;

class Vectors {

    static <A> ImmutableVector<A> empty() {
        return EmptyVector.emptyVector();
    }

    static <A> Vector<A> wrap(A[] arr) {
        Objects.requireNonNull(arr);
        if (arr.length == 0) {
            return empty();
        } else {
            return new WrappedArrayVector<>(arr);
        }
    }

    static <A> ImmutableVector<A> immutableWrap(A[] arr) {
        Objects.requireNonNull(arr);
        if (arr.length == 0) {
            return empty();
        } else {
            return new ImmutableArrayVector<>(arr);
        }
    }

    static <A> Vector<A> wrap(List<A> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return empty();
        } else {
            return new WrappedListVector<>(list);
        }
    }

    static <A> ImmutableVector<A> immutableWrap(List<A> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return empty();
        } else {
            return new ImmutableListVector<>(list);
        }
    }

    static <A> Vector<A> take(int count, Vector<A> source) {
        return takeFromIterable(count, source);
    }

    static <A> ImmutableVector<A> immutableTake(int count, ImmutableVector<A> source) {
        if (count < 0) throw new IllegalArgumentException("count must be >= 0");
        return immutableSlice(0, count, source);
    }

    static <A> Vector<A> takeFromIterable(int count, Iterable<A> source) {
        if (count < 0) throw new IllegalArgumentException("count must be >= 0");
        return sliceFromIterable(0, count, source);
    }

    static <A> Vector<A> drop(int count, Vector<A> source) {
        return dropImpl(VectorSlice::new, count, source);
    }

    static <A> ImmutableVector<A> immutableDrop(int count, ImmutableVector<A> source) {
        return dropImpl(ImmutableVectorSlice::new, count, source);
    }

    private static <A, V extends Vector<A>> V dropImpl(Fn3<Integer, Integer, V, V> factory, int count, V source) {
        Objects.requireNonNull(source);
        if (count < 0) throw new IllegalArgumentException("count must be >= 0");
        if (count == 0) return source;
        int sourceSize = source.size();
        if (count >= sourceSize) //noinspection unchecked
            return (V) empty();
        return factory.apply(count, sourceSize - count, source);
    }

    static <A> Vector<A> slice(int startIndex, int endIndexExclusive, Vector<A> source) {
        return sliceFromIterable(startIndex, endIndexExclusive, source);
    }

    static <A> ImmutableVector<A> immutableSlice(int startIndex, int endIndexExclusive, ImmutableVector<A> source) {
        if (startIndex < 0) throw new IllegalArgumentException("startIndex must be >= 0");
        if (endIndexExclusive < 0) throw new IllegalArgumentException("endIndex must be >= 0");
        Objects.requireNonNull(source);
        int requestedSize = endIndexExclusive - startIndex;
        if (requestedSize < 1) {
            return empty();
        }
        int sourceSize = source.size();
        if (startIndex == 0 && requestedSize >= sourceSize) {
            return source;
        } else if (startIndex >= sourceSize) {
            return empty();
        } else {
            int available = Math.max(sourceSize - startIndex, 0);
            int sliceSize = Math.min(available, requestedSize);
            return new ImmutableVectorSlice<>(startIndex, sliceSize, source);
        }
    }

    static <A> Vector<A> sliceFromIterable(int startIndex, int endIndexExclusive, Iterable<A> source) {
        if (startIndex < 0) throw new IllegalArgumentException("startIndex must be >= 0");
        if (endIndexExclusive < 0) throw new IllegalArgumentException("endIndex must be >= 0");
        Objects.requireNonNull(source);
        int requestedSize = endIndexExclusive - startIndex;
        if (requestedSize < 1) {
            return empty();
        }
        if (source instanceof Vector<?>) {
            Vector<A> sourceVector = (Vector<A>) source;
            int sourceSize = sourceVector.size();
            if (startIndex == 0 && requestedSize >= sourceSize) {
                return sourceVector;
            } else if (startIndex >= sourceSize) {
                return empty();
            } else {
                int available = Math.max(sourceSize - startIndex, 0);
                int sliceSize = Math.min(available, requestedSize);
                return new VectorSlice<>(startIndex, sliceSize, sourceVector);
            }
        } else if (source instanceof List<?>) {
            List<A> sourceList = (List<A>) source;
            int sourceSize = sourceList.size();
            if (startIndex == 0 && requestedSize >= sourceSize) {
                return wrap(sourceList);
            } else if (startIndex >= sourceSize) {
                return empty();
            } else {
                int available = Math.max(sourceSize - startIndex, 0);
                int sliceSize = Math.min(available, requestedSize);
                return new VectorSlice<>(startIndex, sliceSize, wrap(sourceList));
            }
        } else {
            ArrayList<A> newList = toCollection(ArrayList::new, Take.take(requestedSize, Drop.drop(startIndex, source)));
            return immutableWrap(newList);
        }
    }

    @SafeVarargs
    static <A> ImmutableNonEmptyVector<A> of(A first, A... more) {
        return new ImmutableVectorCons<>(first, immutableWrap(more));
    }

    static <A> ImmutableVector<A> copyFromArray(A[] source) {
        Objects.requireNonNull(source);
        A[] copied = Arrays.copyOf(source, source.length);
        return immutableWrap(copied);
    }

    static <A> Maybe<NonEmptyVector<A>> tryNonEmptyWrap(A[] arr) {
        Objects.requireNonNull(arr);
        if (arr.length == 0) {
            return nothing();
        } else {
            return just(new WrappedArrayVector<>(arr));
        }
    }

    static <A> Maybe<NonEmptyVector<A>> tryNonEmptyWrap(List<A> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return nothing();
        } else {
            return just(new WrappedListVector<>(list));
        }
    }

    @SuppressWarnings("unchecked")
    static <A> Maybe<ImmutableNonEmptyVector<A>> tryNonEmptyCopyFromArray(A[] arr) {
        Objects.requireNonNull(arr);
        if (arr.length == 0) {
            return nothing();
        } else {
            return (Maybe<ImmutableNonEmptyVector<A>>) copyFromArray(arr).toNonEmpty();
        }
    }

    @SuppressWarnings("unchecked")
    static <A> Maybe<ImmutableNonEmptyVector<A>> tryNonEmptyCopyAllFromIterable(Iterable<A> source) {
        Objects.requireNonNull(source);
        if (source.iterator().hasNext()) return nothing();
        return (Maybe<ImmutableNonEmptyVector<A>>) copyAllFromIterable(source).toNonEmpty();
    }

    static <A> Maybe<NonEmptyVector<A>> tryNonEmptyWrap(Vector<A> vec) {
        Objects.requireNonNull(vec);
        if (vec instanceof NonEmptyVector<?>) {
            return just((NonEmptyVector<A>) vec);
        } else if (!vec.isEmpty()) {
            return just(new VectorCons<>(vec.unsafeGet(0), vec.tail()));
        } else {
            return nothing();
        }
    }

    static <A> Maybe<ImmutableNonEmptyVector<A>> immutableTryNonEmptyWrap(ImmutableVector<A> vec) {
        Objects.requireNonNull(vec);
        if (vec instanceof NonEmptyVector<?>) {
            return just((ImmutableNonEmptyVector<A>) vec);
        } else if (!vec.isEmpty()) {
            return just(new ImmutableVectorCons<>(vec.unsafeGet(0), vec.tail()));
        } else {
            return nothing();
        }
    }

    static <A> NonEmptyVector<A> nonEmptyWrapOrThrow(A[] arr) {
        return getNonEmptyOrThrow(tryNonEmptyWrap(arr));
    }

    static <A> NonEmptyVector<A> nonEmptyWrapOrThrow(List<A> list) {
        return getNonEmptyOrThrow(tryNonEmptyWrap(list));
    }

    static <A> NonEmptyVector<A> nonEmptyWrapOrThrow(Vector<A> vec) {
        return getNonEmptyOrThrow(tryNonEmptyWrap(vec));
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyCopyFromArrayOrThrow(A[] source) {
        return immutableGetNonEmptyOrThrow(tryNonEmptyCopyFromArray(source));
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyCopyAllFromIterableOrThrow(Iterable<A> source) {
        return immutableGetNonEmptyOrThrow(tryNonEmptyCopyAllFromIterable(source));
    }

    static <A> ImmutableVector<A> ensureImmutable(Vector<A> vector) {
        if (vector instanceof ImmutableVector<?>) {
            return (ImmutableVector<A>) vector;
        } else if (vector.isEmpty()) {
            return empty();
        } else {
            ArrayList<A> copied = toCollection(ArrayList::new, vector);
            return immutableWrap(copied);
        }
    }

    static <A> ImmutableNonEmptyVector<A> ensureImmutable(NonEmptyVector<A> vector) {
        if (vector instanceof ImmutableNonEmptyVector<?>) {
            return (ImmutableNonEmptyVector<A>) vector;
        } else {
            ArrayList<A> copied = toCollection(ArrayList::new, vector);
            return new ImmutableListVector<>(copied);
        }
    }

    static <A, B> Vector<B> map(Fn1<? super A, ? extends B> f, Vector<A> source) {
        return tryNonEmptyWrap(source)
                .match(__ -> empty(),
                        nonEmpty -> mapNonEmpty(f, nonEmpty));
    }

    @SuppressWarnings("unchecked")
    static <A, B> NonEmptyVector<B> mapNonEmpty(Fn1<? super A, ? extends B> f, NonEmptyVector<A> source) {
        return new MappedVector<>(mapperChain((Fn1<Object, Object>) f),
                (NonEmptyVector<Object>) source);
    }

    static <A, B> ImmutableVector<B> immutableMap(Fn1<? super A, ? extends B> f, ImmutableVector<A> source) {
        return immutableTryNonEmptyWrap(source)
                .match(__ -> empty(),
                        nonEmpty -> immutableMapNonEmpty(f, nonEmpty));
    }

    @SuppressWarnings("unchecked")
    static <A, B> ImmutableNonEmptyVector<B> immutableMapNonEmpty(Fn1<? super A, ? extends B> f, ImmutableNonEmptyVector<A> source) {
        return new ImmutableMappedVector<>(mapperChain((Fn1<Object, Object>) f),
                (ImmutableNonEmptyVector<Object>) source);
    }

    static <A> String renderToString(Vector<A> vector) {
        StringBuilder output = new StringBuilder();
        output.append("Vector(");
        boolean inner = false;
        for (A elem : vector) {
            if (inner) {
                output.append(", ");
            }
            output.append(elem.toString());
            inner = true;
        }
        output.append(')');
        return output.toString();
    }

    static <A> ImmutableVector<A> copyAllFromIterable(Iterable<A> source) {
        Objects.requireNonNull(source);
        if (source instanceof ImmutableVector<?>) {
            return (ImmutableVector<A>) source;
        } else if (!source.iterator().hasNext()) {
            return empty();
        } else {
            ArrayList<A> copied = toCollection(ArrayList::new, source);
            return immutableWrap(copied);
        }
    }

    static <A> ImmutableVector<A> copyTakeFromIterable(int maxCount, Iterable<A> source) {
        Objects.requireNonNull(source);
        if (maxCount < 0) throw new IllegalArgumentException("maxCount must be >= 0");
        if (maxCount == 0) return empty();
        if (source instanceof ImmutableVector<?>) {
            return ((ImmutableVector<A>) source).take(maxCount);
        } else {
            return copyAllFromIterable(Take.take(maxCount, source));
        }
    }

    static <A> ImmutableVector<A> copySliceFromIterable(int startIndex, int endIndexExclusive, Iterable<A> source) {
        if (startIndex < 0) throw new IllegalArgumentException("startIndex must be >= 0");
        if (endIndexExclusive < 0) throw new IllegalArgumentException("endIndex must be >= 0");
        Objects.requireNonNull(source);
        if (source instanceof ImmutableVector<?>) {
            return ((ImmutableVector<A>) source).slice(startIndex, endIndexExclusive);
        } else {
            return sliceFromIterable(startIndex, endIndexExclusive, source).toImmutable();
        }
    }

    private static <A> NonEmptyVector<A> getNonEmptyOrThrow(Maybe<NonEmptyVector<A>> maybeResult) {
        return maybeResult.orElseThrow(() -> {
            throw new IllegalArgumentException("Cannot construct NonEmptyVector from empty input");
        });
    }

    private static <A> ImmutableNonEmptyVector<A> immutableGetNonEmptyOrThrow(Maybe<ImmutableNonEmptyVector<A>> maybeResult) {
        return maybeResult.orElseThrow(() -> {
            throw new IllegalArgumentException("Cannot construct NonEmptyVector from empty input");
        });
    }

}
