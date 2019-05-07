package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.lambda.functions.builtin.fn2.Drop;
import com.jnape.palatable.lambda.functions.builtin.fn2.Take;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;
import static dev.marksman.collectionviews.MapperChain.mapperChain;
import static dev.marksman.collectionviews.Validation.*;

final class Vectors {

    private Vectors() {

    }

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

    static <A> Vector<A> wrap(List<A> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return empty();
        } else {
            return new WrappedListVector<>(list);
        }
    }

    static <A> Vector<A> take(int count, Vector<A> source) {
        return takeFromIterable(count, source);
    }

    static <A> Vector<A> takeRight(int count, Vector<A> source) {
        validateTake(count, source);
        int size = source.size();
        if (count >= size) {
            return source;
        } else {
            return drop(size - count, source);
        }
    }

    static <A> Vector<A> drop(int count, Vector<A> source) {
        return dropImpl(VectorSlice::new, count, source);
    }

    static <A> Vector<A> dropRight(int count, Vector<A> source) {
        validateDrop(count, source);
        int size = source.size();
        if (count >= size) {
            return empty();
        } else {
            return take(size - count, source);
        }
    }

    static <A, V extends Vector<A>> V dropImpl(Fn3<Integer, Integer, V, V> factory, int count, V source) {
        validateDrop(count, source);
        if (count == 0) {
            return source;
        }
        int sourceSize = source.size();
        if (count >= sourceSize) {
            //noinspection unchecked
            return (V) empty();
        }
        return factory.apply(count, sourceSize - count, source);
    }

    static <A> Vector<A> slice(int startIndex, int endIndexExclusive, Vector<A> source) {
        return sliceFromIterable(startIndex, endIndexExclusive, source);
    }

    static <A> Vector<A> sliceFromIterable(int startIndex, int endIndexExclusive, Iterable<A> source) {
        validateSlice(startIndex, endIndexExclusive, source);
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
            return ImmutableVectors.wrapAndVouchFor(newList);
        }
    }

    @SafeVarargs
    static <A> ImmutableNonEmptyVector<A> of(A first, A... more) {
        return new ImmutableVectorCons<>(first, ImmutableVectors.wrapAndVouchFor(more));
    }

    static <A> ImmutableVector<A> fill(int size, A value) {
        validateFill(size);
        if (size == 0) {
            return empty();
        } else {
            return nonEmptyFill(size, value);
        }
    }

    static <A> ImmutableVector<A> lazyFill(int size, Fn1<Integer, A> valueSupplier) {
        validateFill(size);
        if (size == 0) {
            return empty();
        } else {
            return nonEmptyLazyFill(size, valueSupplier);
        }
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyFill(int size, A value) {
        validateNonEmptyFill(size);
        return new RepeatingVector<>(size, value);
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyLazyFill(int size, Fn1<Integer, A> valueSupplier) {
        validateNonEmptyFill(size);
        return new LazyVector<>(size, 0, valueSupplier);
    }

    static <A> Maybe<NonEmptyVector<A>> maybeNonEmptyWrap(A[] arr) {
        Objects.requireNonNull(arr);
        if (arr.length == 0) {
            return nothing();
        } else {
            return just(new WrappedArrayVector<>(arr));
        }
    }

    static <A> Maybe<NonEmptyVector<A>> maybeNonEmptyWrap(List<A> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return nothing();
        } else {
            return just(new WrappedListVector<>(list));
        }
    }

    static <A> NonEmptyVector<A> nonEmptyWrapOrThrow(A[] arr) {
        return getNonEmptyOrThrow(maybeNonEmptyWrap(arr));
    }

    static <A> NonEmptyVector<A> nonEmptyWrapOrThrow(List<A> list) {
        return getNonEmptyOrThrow(maybeNonEmptyWrap(list));
    }

    static <A> NonEmptyVector<A> nonEmptyWrapOrThrow(Vector<A> vec) {
        return getNonEmptyOrThrow(ImmutableVectors.maybeNonEmptyWrap(vec));
    }

    static <A, B> Vector<B> map(Fn1<? super A, ? extends B> f, Vector<A> source) {
        return ImmutableVectors.maybeNonEmptyWrap(source)
                .match(__ -> empty(),
                        nonEmpty -> mapNonEmpty(f, nonEmpty));
    }

    @SuppressWarnings("unchecked")
    static <A, B> NonEmptyVector<B> mapNonEmpty(Fn1<? super A, ? extends B> f, NonEmptyVector<A> source) {
        return new MappedVector<>(mapperChain((Fn1<Object, Object>) f),
                (NonEmptyVector<Object>) source);
    }

    static <A> Vector<A> reverse(Vector<A> vec) {
        if (vec.size() < 2) {
            return vec;
        } else {
            return ReverseVector.reverseVector(vec.toNonEmptyOrThrow());
        }
    }

    static <A> NonEmptyVector<A> nonEmptyReverse(NonEmptyVector<A> vec) {
        if (vec.size() < 2) {
            return vec;
        } else {
            return ReverseVector.reverseVector(vec);
        }
    }

    static <A> Vector<Tuple2<A, Integer>> zipWithIndex(Vector<A> vec) {
        if (vec.isEmpty()) {
            return empty();
        } else {
            return new VectorZipWithIndex<>(vec.toNonEmptyOrThrow());
        }
    }

    static <A> Maybe<Integer> findIndex(Fn1<? super A, ? extends Boolean> predicate, Vector<A> vec) {
        int size = vec.size();
        for (int i = 0; i < size; i++) {
            if (predicate.apply(vec.unsafeGet(i))) {
                return just(i);
            }
        }
        return nothing();
    }

    static <A> NonEmptyVector<Tuple2<A, Integer>> nonEmptyZipWithIndex(NonEmptyVector<A> vec) {
        return new VectorZipWithIndex<>(vec);
    }

    private static <A> Vector<A> takeFromIterable(int count, Iterable<A> source) {
        validateTake(count, source);
        return sliceFromIterable(0, count, source);
    }

    private static <A> NonEmptyVector<A> getNonEmptyOrThrow(Maybe<NonEmptyVector<A>> maybeResult) {
        return maybeResult.orElseThrow(Vectors.nonEmptyError());
    }

    static Supplier<IllegalArgumentException> nonEmptyError() {
        return () -> new IllegalArgumentException("Cannot construct NonEmptyVector from empty input");
    }

    static <A> int findPrefixLength(Fn1<? super A, ? extends Boolean> predicate, Vector<A> vec) {
        int result = 0;
        for (A current : vec) {
            if (predicate.apply(current)) {
                result += 1;
            } else {
                break;
            }
        }
        return result;
    }

}
