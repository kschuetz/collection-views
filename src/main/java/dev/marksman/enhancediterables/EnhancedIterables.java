package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Uncons;
import dev.marksman.enhancediterables.internal.ProtectedIterator;

class EnhancedIterables {

    static <A> EnhancedIterable<A> enhancedIterable(Iterable<A> underlying) {
        return () -> ProtectedIterator.protectedIterator(underlying.iterator());
    }

    static <A> FiniteIterable<A> finiteIterable(Iterable<A> underlying) {
        return () -> ProtectedIterator.protectedIterator(underlying.iterator());
    }

    static <A> ImmutableIterable<A> immutableIterable(Iterable<A> underlying) {
        return () -> ProtectedIterator.protectedIterator(underlying.iterator());
    }

    static <A> ImmutableFiniteIterable<A> immutableFiniteIterable(Iterable<A> underlying) {
        return () -> ProtectedIterator.protectedIterator(underlying.iterator());
    }

    static <A> NonEmptyIterable<A> nonEmptyIterableOrThrow(Iterable<A> underlying) {
        Tuple2<A, Iterable<A>> headTail = Uncons.uncons(underlying)
                .orElseThrow(() -> new IllegalArgumentException("underlying is empty"));
        return NonEmptyIterable.nonEmptyIterable(headTail._1(), headTail._2());
    }

    static <A> ImmutableNonEmptyFiniteIterable<A> immutableNonEmptyFiniteIterableOrThrow(Iterable<A> underlying) {
        Tuple2<A, Iterable<A>> headTail = Uncons.uncons(underlying)
                .orElseThrow(() -> new IllegalArgumentException("underlying is empty"));
        return null; //NonEmptyIterable.nonEmptyIterable(headTail._1(), headTail._2());
    }

}
