package dev.marksman.collectionviews.concrete;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.builtin.fn2.Drop;
import com.jnape.palatable.lambda.functions.builtin.fn2.Take;
import dev.marksman.collectionviews.NonEmptyVector;
import dev.marksman.collectionviews.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;

public class Vectors {

    public static <A> Vector<A> empty() {
        return EmptyVector.emptyVector();
    }

    public static <A> Vector<A> wrap(A[] arr) {
        Objects.requireNonNull(arr);
        if (arr.length == 0) {
            return empty();
        } else {
            for (A elem : arr) {
                if (elem == null) noNullsAllowedError();
            }
            return new VectorWrappedArray<>(arr);
        }
    }

    public static <A> Vector<A> wrap(List<A> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return empty();
        } else {
            for (A elem : list) {
                if (elem == null) noNullsAllowedError();
            }
            return new VectorWrappedList<>(list);
        }
    }

    public static <A> Vector<A> take(int count, Iterable<A> source) {
        if (count < 0) throw new IllegalArgumentException("count must be >= 0");
        return slice(0, count, source);
    }

    public static <A> Vector<A> drop(int count, Vector<A> source) {
        Objects.requireNonNull(source);
        if (count < 0) throw new IllegalArgumentException("count must be >= 0");
        if (count == 0) return source;
        int sourceSize = source.size();
        if (count >= sourceSize) return empty();
        return new VectorSlice<>(count, sourceSize - count, source);
    }

    public static <A> Vector<A> slice(int startIndex, int endIndexExclusive, Iterable<A> source) {
        if (startIndex < 0) throw new IllegalArgumentException("startIndex must be >= 0");
        if (endIndexExclusive < 0) throw new IllegalArgumentException("endIndex must be >= 0");
        Objects.requireNonNull(source);
        int targetSize = endIndexExclusive - startIndex;
        if (targetSize < 1) {
            return empty();
        }
        if (source instanceof Vector<?>) {
            Vector<A> sourceVector = (Vector<A>) source;
            int sourceSize = sourceVector.size();
            if (startIndex == 0 && targetSize >= sourceSize) {
                return sourceVector;
            } else if (startIndex >= sourceSize) {
                return empty();
            } else {
                int endIndex = Math.min(startIndex + targetSize, sourceSize);
                return new VectorSlice<>(startIndex, endIndex - startIndex, sourceVector);
            }
        } else if (source instanceof List<?>) {
            List<A> sourceList = (List<A>) source;
            if (startIndex == 0 && targetSize >= sourceList.size()) {
                return wrap(sourceList);
            } else
                return new VectorSlice<>(startIndex, targetSize, wrap(sourceList));
        } else {
            ArrayList<A> newList = toCollection(ArrayList::new, Take.take(targetSize, Drop.drop(startIndex, source)));
            return wrap(newList);
        }
    }

    public static <A> NonEmptyVector<A> nonEmptyWrap(A first, A[] more) {
        if (first == null) noNullsAllowedError();
        return new ConcreteNonEmptyVector<>(first, wrap(more));
    }

    public static <A> NonEmptyVector<A> nonEmptyWrap(A first, List<A> more) {
        if (first == null) noNullsAllowedError();
        return new ConcreteNonEmptyVector<>(first, wrap(more));
    }

    public static <A> NonEmptyVector<A> nonEmptyWrap(A first, Vector<A> more) {
        if (first == null) noNullsAllowedError();
        return new ConcreteNonEmptyVector<>(first, more);
    }

    public static <A> Maybe<NonEmptyVector<A>> tryNonEmptyWrap(A[] arr) {
        Objects.requireNonNull(arr);
        if (arr.length == 0) {
            return nothing();
        } else {
            return just(new VectorWrappedArray<>(arr));
        }
    }

    public static <A> Maybe<NonEmptyVector<A>> tryNonEmptyWrap(List<A> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return nothing();
        } else {
            return just(new VectorWrappedList<>(list));
        }
    }

    public static <A> Maybe<NonEmptyVector<A>> tryNonEmptyWrap(Vector<A> vec) {
        Objects.requireNonNull(vec);
        if (vec instanceof NonEmptyVector<?>) {
            return just((NonEmptyVector<A>) vec);
        } else if (!vec.isEmpty()) {
            return just(new ConcreteNonEmptyVector<>(vec.unsafeGet(0), vec.tail()));
        } else {
            return nothing();
        }
    }

    private static void noNullsAllowedError() {
        throw new IllegalStateException("Vector cannot contain any null elements");
    }
}
