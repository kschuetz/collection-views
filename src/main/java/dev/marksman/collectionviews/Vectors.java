package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn2.Drop;
import com.jnape.palatable.lambda.functions.builtin.fn2.Take;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into.into;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Tupler2.tupler;
import static dev.marksman.collectionviews.CrossJoinVector.crossJoinVector;
import static dev.marksman.collectionviews.MapperChain.mapperChain;
import static dev.marksman.collectionviews.Validation.*;
import static dev.marksman.collectionviews.VectorZip.vectorZip;

final class Vectors {

    private Vectors() {

    }

    static <A, B> Vector<Tuple2<A, B>> cross(Vector<A> first, Vector<B> second) {
        return second.toNonEmpty().<Tuple2<NonEmptyVector<A>, NonEmptyVector<B>>>zip(first.toNonEmpty()
                .fmap(tupler()))
                .match(__ -> empty(),
                        into(Vectors::nonEmptyCross));
    }

    static <A> Vector<A> drop(int count, Vector<A> source) {
        return VectorSlicing.dropImpl(VectorSlice::vectorSlice, count, source);
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

    static <A> ImmutableVector<A> empty() {
        return EmptyVector.emptyVector();
    }

    static <A> ImmutableVector<A> fill(int size, A value) {
        validateFill(size);
        if (size == 0) {
            return empty();
        } else {
            return nonEmptyFill(size, value);
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

    static <A> ImmutableVector<A> lazyFill(int size, Fn1<Integer, A> valueSupplier) {
        validateFill(size);
        Objects.requireNonNull(valueSupplier);
        if (size == 0) {
            return empty();
        } else {
            return nonEmptyLazyFill(size, valueSupplier);
        }
    }

    static <A, B> Vector<B> map(Fn1<? super A, ? extends B> f, Vector<A> source) {
        return maybeNonEmptyWrap(source)
                .match(__ -> empty(),
                        nonEmpty -> nonEmptyMap(f, nonEmpty));
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

    static <A> Maybe<NonEmptyVector<A>> maybeNonEmptyWrap(Vector<A> vec) {
        Objects.requireNonNull(vec);
        if (vec instanceof NonEmptyVector<?>) {
            return just((NonEmptyVector<A>) vec);
        } else if (!vec.isEmpty()) {
            return just(new VectorCons<>(vec.unsafeGet(0), vec.tail()));
        } else {
            return nothing();
        }
    }

    static <A, B> NonEmptyVector<Tuple2<A, B>> nonEmptyCross(NonEmptyVector<A> first, NonEmptyVector<B> second) {
        return crossJoinVector(first, second);
    }

    static Supplier<IllegalArgumentException> nonEmptyError() {
        return () -> new IllegalArgumentException("Cannot construct NonEmptyVector from empty input");
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyFill(int size, A value) {
        validateNonEmptyFill(size);
        return new RepeatingVector<>(size, value);
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyLazyFill(int size, Fn1<Integer, A> valueSupplier) {
        validateNonEmptyFill(size);
        return new LazyVector<>(size, 0, valueSupplier);
    }

    @SuppressWarnings("unchecked")
    static <A, B> NonEmptyVector<B> nonEmptyMap(Fn1<? super A, ? extends B> f, NonEmptyVector<A> source) {
        return new MappedVector<>(mapperChain((Fn1<Object, Object>) f),
                (NonEmptyVector<Object>) source);
    }

    static <A> NonEmptyVector<A> nonEmptyReverse(NonEmptyVector<A> vec) {
        Objects.requireNonNull(vec);
        if (vec.size() < 2) {
            return vec;
        } else {
            return ReverseVector.reverseVector(vec);
        }
    }

    @SafeVarargs
    static <A> ImmutableNonEmptyVector<A> nonEmptyVectorOf(A first, A... more) {
        return new ImmutableVectorCons<>(first, ImmutableVectors.wrapAndVouchFor(more));
    }

    static <A> NonEmptyVector<A> nonEmptyWrapOrThrow(A[] arr) {
        return getNonEmptyOrThrow(maybeNonEmptyWrap(arr));
    }

    static <A> NonEmptyVector<A> nonEmptyWrapOrThrow(List<A> list) {
        return getNonEmptyOrThrow(maybeNonEmptyWrap(list));
    }

    static <A> NonEmptyVector<A> nonEmptyWrapOrThrow(Vector<A> vec) {
        return getNonEmptyOrThrow(maybeNonEmptyWrap(vec));
    }

    static <A, B, R> NonEmptyVector<R> nonEmptyZipWith(Fn2<A, B, R> fn, NonEmptyVector<A> first, NonEmptyVector<B> second) {
        Objects.requireNonNull(fn);
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        return vectorZip(fn, first, second);
    }

    static <A> NonEmptyVector<Tuple2<A, Integer>> nonEmptyZipWithIndex(NonEmptyVector<A> vec) {
        Objects.requireNonNull(vec);
        return new VectorZipWithIndex<>(vec);
    }

    static <A> Vector<A> reverse(Vector<A> vec) {
        Objects.requireNonNull(vec);
        if (vec.size() < 2) {
            return vec;
        } else {
            return ReverseVector.reverseVector(vec.toNonEmptyOrThrow());
        }
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
            return VectorSlicing.sliceImpl(VectorSlice::vectorSlice, sourceVector.size(), () -> sourceVector, startIndex, requestedSize);
        } else if (source instanceof List<?>) {
            List<A> sourceList = (List<A>) source;
            return VectorSlicing.sliceImpl(VectorSlice::vectorSlice, sourceList.size(), () -> Vector.wrap(sourceList), startIndex, requestedSize);
        } else {
            ArrayList<A> newList = toCollection(ArrayList::new, Take.take(requestedSize, Drop.drop(startIndex, source)));
            return ImmutableVectors.wrapAndVouchFor(newList);
        }
    }

    static <A> Tuple2<Vector<A>, Vector<A>> splitAt(int index, Vector<A> source) {
        validateTake(index, source);
        return tuple(source.take(index), source.drop(index));
    }

    static <A> Vector<A> take(int count, Vector<A> source) {
        validateTake(count, source);
        return sliceFromIterable(0, count, source);
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

    static <A, B, R> Vector<R> zipWith(Fn2<A, B, R> fn, Vector<A> first, Vector<B> second) {
        return second.toNonEmpty().<Tuple2<NonEmptyVector<A>, NonEmptyVector<B>>>zip(first.toNonEmpty()
                .fmap(tupler()))
                .match(__ -> empty(),
                        into((ne1, ne2) -> nonEmptyZipWith(fn, ne1, ne2)));
    }

    static <A> Vector<Tuple2<A, Integer>> zipWithIndex(Vector<A> vec) {
        Objects.requireNonNull(vec);
        if (vec.isEmpty()) {
            return empty();
        } else {
            return new VectorZipWithIndex<>(vec.toNonEmptyOrThrow());
        }
    }

    private static <A> NonEmptyVector<A> getNonEmptyOrThrow(Maybe<NonEmptyVector<A>> maybeResult) {
        return maybeResult.orElseThrow(Vectors.nonEmptyError());
    }

}
