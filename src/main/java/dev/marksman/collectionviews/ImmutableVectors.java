package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn2.Take;
import dev.marksman.enhancediterables.ImmutableNonEmptyFiniteIterable;
import dev.marksman.enhancediterables.ImmutableNonEmptyIterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into.into;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Tupler2.tupler;
import static dev.marksman.collectionviews.ImmutableCrossJoinVector.immutableCrossJoinVector;
import static dev.marksman.collectionviews.ImmutableReverseVector.immutableReverseVector;
import static dev.marksman.collectionviews.ImmutableVectorZip.immutableVectorZip;
import static dev.marksman.collectionviews.MapperChain.mapperChain;
import static dev.marksman.collectionviews.Validation.*;
import static dev.marksman.collectionviews.Vector.empty;
import static dev.marksman.collectionviews.VectorSlicing.sliceImpl;

final class ImmutableVectors {

    private ImmutableVectors() {

    }

    static <A> ImmutableVector<A> copyFrom(A[] source) {
        Objects.requireNonNull(source);
        return copyFrom(source.length, source);
    }

    static <A> ImmutableVector<A> copyFrom(int maxCount, A[] source) {
        validateCopyFrom(maxCount, source);
        int count = Math.min(maxCount, source.length);
        A[] copied = Arrays.copyOf(source, count);
        return wrapAndVouchFor(copied);
    }

    static <A> ImmutableVector<A> copyFrom(Iterable<A> source) {
        Objects.requireNonNull(source);
        if (source instanceof ImmutableVector<?> && Util.isPrimitive(source)) {
            return (ImmutableVector<A>) source;
        } else if (!source.iterator().hasNext()) {
            return Vectors.empty();
        } else {
            ArrayList<A> copied = toCollection(ArrayList::new, source);
            return wrapAndVouchFor(copied);
        }
    }

    static <A> ImmutableVector<A> copyFrom(int maxCount, Iterable<A> source) {
        validateCopyFrom(maxCount, source);
        if (maxCount == 0) {
            return Vectors.empty();
        }
        if (source instanceof ImmutableVector<?> && Util.isPrimitive(source)) {
            return ((ImmutableVector<A>) source).take(maxCount);
        } else {
            return copyFrom(Take.take(maxCount, source));
        }
    }

    static <A> ImmutableVector<A> copySliceFrom(int startIndex, int endIndexExclusive, Iterable<A> source) {
        validateSlice(startIndex, endIndexExclusive, source);
        if (source instanceof ImmutableVector<?> && Util.isPrimitive(source)) {
            return ((ImmutableVector<A>) source).slice(startIndex, endIndexExclusive);
        } else {
            return Vectors.sliceFromIterable(startIndex, endIndexExclusive, source).toImmutable();
        }
    }

    static <A, B> ImmutableVector<Tuple2<A, B>> cross(ImmutableVector<A> first, ImmutableVector<B> second) {
        return second.toNonEmpty().<Tuple2<ImmutableNonEmptyVector<A>, ImmutableNonEmptyVector<B>>>zip(first.toNonEmpty()
                .fmap(tupler()))
                .match(__ -> empty(),
                        into(ImmutableVectors::nonEmptyCross));
    }

    static <A> ImmutableVector<A> drop(int count, ImmutableVector<A> source) {
        return VectorSlicing.dropImpl(ImmutableVectorSlice::immutableVectorSlice, count, source);
    }

    static <A> ImmutableVector<A> dropRight(int count, ImmutableVector<A> source) {
        validateDrop(count, source);
        int size = source.size();
        if (count >= size) {
            return empty();
        } else {
            return take(size - count, source);
        }
    }

    static <A> ImmutableVector<A> dropWhile(Fn1<? super A, ? extends Boolean> predicate, ImmutableVector<A> source) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(source);
        return drop(Vectors.findPrefixLength(predicate, source), source);
    }

    static <A> ImmutableVector<A> ensureImmutable(Vector<A> vector) {
        if (vector instanceof ImmutableVector<?>) {
            return (ImmutableVector<A>) vector;
        } else if (vector.isEmpty()) {
            return Vectors.empty();
        } else {
            ArrayList<A> copied = toCollection(ArrayList::new, vector);
            return wrapAndVouchFor(copied);
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

    static <A> ImmutableVector<A> fill(int size, A value) {
        validateFill(size);
        if (size == 0) {
            return Vectors.empty();
        } else {
            return nonEmptyFill(size, value);
        }
    }

    static <A> ImmutableVector<Integer> indices(Vector<A> vec) {
        return range(vec.size());
    }

    static <A> ImmutableNonEmptyIterable<ImmutableVector<A>> inits(ImmutableVector<A> source) {
        return nonEmptyRange(source.size() + 1).fmap(source::dropRight);
    }

    static <A> ImmutableVector<A> lazyFill(int size, Fn1<Integer, A> valueSupplier) {
        validateFill(size);
        Objects.requireNonNull(valueSupplier);
        if (size == 0) {
            return Vectors.empty();
        } else {
            return nonEmptyLazyFill(size, valueSupplier);
        }
    }

    static <A> ImmutableVector<? extends ImmutableNonEmptyVector<A>> magnetizeBy(Fn2<A, A, Boolean> predicate, ImmutableVector<A> source) {
        Objects.requireNonNull(predicate);
        return source.toNonEmpty().match(__ -> Vectors.empty(),
                ne -> nonEmptyMagnetizeBy(predicate, ne));
    }

    static <A, B> ImmutableVector<B> map(Fn1<? super A, ? extends B> f, ImmutableVector<A> source) {
        return maybeNonEmptyConvert(source)
                .match(__ -> Vectors.empty(),
                        nonEmpty -> nonEmptyMap(f, nonEmpty));
    }

    static <A> Maybe<ImmutableNonEmptyVector<A>> maybeNonEmptyConvert(ImmutableVector<A> vec) {
        Objects.requireNonNull(vec);
        if (vec instanceof ImmutableNonEmptyVector<?>) {
            return just((ImmutableNonEmptyVector<A>) vec);
        } else if (!vec.isEmpty()) {
            return just(new ImmutableVectorCons<>(vec.unsafeGet(0), vec.drop(1)));
        } else {
            return nothing();
        }
    }

    @SuppressWarnings("unchecked")
    static <A> Maybe<ImmutableNonEmptyVector<A>> maybeNonEmptyCopyFrom(A[] arr) {
        Objects.requireNonNull(arr);
        if (arr.length == 0) {
            return nothing();
        } else {
            return (Maybe<ImmutableNonEmptyVector<A>>) copyFrom(arr).toNonEmpty();
        }
    }

    @SuppressWarnings("unchecked")
    static <A> Maybe<ImmutableNonEmptyVector<A>> maybeNonEmptyCopyFrom(int maxCount, A[] arr) {
        validateCopyFrom(maxCount, arr);
        if (arr.length == 0 || maxCount == 0) {
            return nothing();
        } else {
            return (Maybe<ImmutableNonEmptyVector<A>>) copyFrom(maxCount, arr).toNonEmpty();
        }
    }

    @SuppressWarnings("unchecked")
    static <A> Maybe<ImmutableNonEmptyVector<A>> maybeNonEmptyCopyFrom(Iterable<A> source) {
        Objects.requireNonNull(source);
        if (!source.iterator().hasNext()) {
            return nothing();
        }
        return (Maybe<ImmutableNonEmptyVector<A>>) copyFrom(source).toNonEmpty();
    }

    @SuppressWarnings("unchecked")
    static <A> Maybe<ImmutableNonEmptyVector<A>> maybeNonEmptyCopyFrom(int maxCount, Iterable<A> source) {
        validateCopyFrom(maxCount, source);
        if (maxCount == 0) {
            return nothing();
        }
        if (!source.iterator().hasNext()) {
            return nothing();
        }
        return (Maybe<ImmutableNonEmptyVector<A>>) copyFrom(maxCount, source).toNonEmpty();
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyConvertOrThrow(ImmutableVector<A> source) {
        return getNonEmptyOrThrow(maybeNonEmptyConvert(source));
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyCopyFromOrThrow(Iterable<A> source) {
        return getNonEmptyOrThrow(maybeNonEmptyCopyFrom(source));
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyCopyFromOrThrow(int maxCount, Iterable<A> source) {
        return getNonEmptyOrThrow(maybeNonEmptyCopyFrom(maxCount, source));
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyCopyFromOrThrow(A[] source) {
        return getNonEmptyOrThrow(maybeNonEmptyCopyFrom(source));
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyCopyFromOrThrow(int maxCount, A[] source) {
        return getNonEmptyOrThrow(maybeNonEmptyCopyFrom(maxCount, source));
    }

    static <A, B> ImmutableNonEmptyVector<Tuple2<A, B>> nonEmptyCross(ImmutableNonEmptyVector<A> first, ImmutableNonEmptyVector<B> second) {
        return immutableCrossJoinVector(first, second);
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyFill(int size, A value) {
        validateNonEmptyFill(size);
        return new RepeatingVector<>(size, value);
    }

    static <A> ImmutableNonEmptyVector<Integer> nonEmptyIndices(NonEmptyVector<A> vec) {
        return nonEmptyRange(vec.size());
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyLazyFill(int size, Fn1<Integer, A> valueSupplier) {
        validateNonEmptyFill(size);
        return new LazyVector<>(size, 0, valueSupplier);
    }

    static <A> ImmutableNonEmptyVector<? extends ImmutableNonEmptyVector<A>> nonEmptyMagnetizeBy(Fn2<A, A, Boolean> predicate, ImmutableNonEmptyVector<A> source) {
        Objects.requireNonNull(predicate);
        int size = source.size();
        VectorBuilder<ImmutableNonEmptyVector<A>> builder = Vector.builder();
        int index = 0;
        while (index < size) {
            Tuple2<ImmutableNonEmptyVector<A>, Integer> next = magnetizeAt(predicate, index, source);
            builder = builder.add(next._1());
            index = next._2();
        }
        return builder.build().toNonEmptyOrThrow();
    }

    @SuppressWarnings("unchecked")
    static <A, B> ImmutableNonEmptyVector<B> nonEmptyMap(Fn1<? super A, ? extends B> f, ImmutableNonEmptyVector<A> source) {
        return new ImmutableMappedVector<>(mapperChain((Fn1<Object, Object>) f),
                (ImmutableNonEmptyVector<Object>) source);
    }


    static ImmutableNonEmptyVector<Integer> nonEmptyRange(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be >= 1");
        }
        return nonEmptyLazyFill(size, id());
    }

    static <A> ImmutableNonEmptyVector<A> nonEmptyReverse(ImmutableNonEmptyVector<A> vec) {
        if (vec.size() < 2) {
            return vec;
        } else {
            return immutableReverseVector(vec);
        }
    }

    static <A, B, C> ImmutableNonEmptyVector<C> nonEmptyZipWith(Fn2<A, B, C> fn,
                                                                ImmutableNonEmptyVector<A> first, ImmutableNonEmptyVector<B> second) {
        Objects.requireNonNull(fn);
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        return immutableVectorZip(fn, first, second);
    }

    static <A> ImmutableNonEmptyVector<Tuple2<A, Integer>> nonEmptyZipWithIndex(ImmutableNonEmptyVector<A> vec) {
        Objects.requireNonNull(vec);
        return nonEmptyZipWith(tupler(), vec, vec.indices());
    }

    static <A> ImmutableVector<A> reverse(ImmutableVector<A> vec) {
        if (vec.size() < 2) {
            return vec;
        } else {
            return immutableReverseVector(vec.toNonEmptyOrThrow());
        }
    }

    static ImmutableVector<Integer> range(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be >= 0");
        }
        return lazyFill(size, id());
    }

    static <A> ImmutableVector<A> slice(int startIndex, int endIndexExclusive, ImmutableVector<A> source) {
        validateSlice(startIndex, endIndexExclusive, source);
        int requestedSize = endIndexExclusive - startIndex;
        if (requestedSize < 1) {
            return Vectors.empty();
        } else {
            return sliceImpl(ImmutableVectorSlice::immutableVectorSlice, source.size(), () -> source,
                    startIndex, requestedSize);
        }
    }

    static <A> Tuple2<ImmutableVector<A>, ImmutableVector<A>> span
            (Fn1<? super A, ? extends Boolean> predicate, ImmutableVector<A> source) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(source);
        return splitAt(Vectors.findPrefixLength(predicate, source), source);
    }

    static <A> Tuple2<ImmutableVector<A>, ImmutableVector<A>> splitAt(int index, ImmutableVector<A> source) {
        validateTake(index, source);
        return tuple(source.take(index), source.drop(index));
    }

    static <A> ImmutableNonEmptyFiniteIterable<ImmutableVector<A>> tails(ImmutableVector<A> source) {
        return nonEmptyRange(source.size() + 1).fmap(source::drop);
    }

    static <A> ImmutableVector<A> take(int count, ImmutableVector<A> source) {
        validateTake(count, source);
        return slice(0, count, source);
    }

    static <A> ImmutableVector<A> takeRight(int count, ImmutableVector<A> source) {
        validateTake(count, source);
        int size = source.size();
        if (count >= size) {
            return source;
        } else {
            return drop(size - count, source);
        }
    }

    static <A> ImmutableVector<A> takeWhile(Fn1<? super A, ? extends Boolean> predicate, ImmutableVector<A> source) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(source);
        return take(Vectors.findPrefixLength(predicate, source), source);
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

    static <A, B, C> ImmutableVector<C> zipWith(Fn2<A, B, C> fn, ImmutableVector<A> first, ImmutableVector<B> second) {
        return second.toNonEmpty().<Tuple2<ImmutableNonEmptyVector<A>, ImmutableNonEmptyVector<B>>>zip(first.toNonEmpty()
                .fmap(tupler()))
                .match(__ -> empty(),
                        into((ne1, ne2) -> nonEmptyZipWith(fn, ne1, ne2)));
    }

    static <A> ImmutableVector<Tuple2<A, Integer>> zipWithIndex(ImmutableVector<A> vec) {
        Objects.requireNonNull(vec);
        return zipWith(tupler(), vec, vec.indices());
    }

    private static <A> ImmutableNonEmptyVector<A> getNonEmptyOrThrow(Maybe<ImmutableNonEmptyVector<A>> maybeResult) {
        return maybeResult.orElseThrow(Vectors.nonEmptyError());
    }

    private static <A> Tuple2<ImmutableNonEmptyVector<A>, Integer> magnetizeAt(Fn2<A, A, Boolean> predicate, int index, ImmutableVector<A> source) {
        int maxIndex = source.size() - 1;
        int firstIndex = index;
        while (index < maxIndex && predicate.apply(source.unsafeGet(index), source.unsafeGet(index + 1))) {
            index += 1;
        }
        int nextIndex = index + 1;
        return tuple(slice(firstIndex, nextIndex, source).toNonEmptyOrThrow(), nextIndex);
    }

}
