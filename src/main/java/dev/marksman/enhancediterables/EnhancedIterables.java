package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Uncons;

import static dev.marksman.enhancediterables.internal.ProtectedIterator.protectedIterator;
import static java.util.Arrays.asList;

class EnhancedIterables {

    static <A> FiniteIterable<A> finiteIterable(Iterable<A> underlying) {
        if (underlying instanceof EnhancedIterable<?>) {
            return (FiniteIterable<A>) underlying;
        } else {
            return () -> protectedIterator(underlying.iterator());
        }
    }

    static <A> ImmutableIterable<A> immutableIterable(Iterable<A> underlying) {
        if (underlying instanceof ImmutableIterable<?>) {
            return (ImmutableIterable<A>) underlying;
        } else {
            return () -> protectedIterator(underlying.iterator());
        }
    }

    static <A> ImmutableFiniteIterable<A> immutableFiniteIterable(Iterable<A> underlying) {
        if (underlying instanceof ImmutableFiniteIterable<?>) {
            return (ImmutableFiniteIterable<A>) underlying;
        } else {
            return () -> protectedIterator(underlying.iterator());
        }
    }

    static <A> NonEmptyIterable<A> nonEmptyIterableOrThrow(Iterable<A> underlying) {
        if (underlying instanceof NonEmptyIterable<?>) {
            return (NonEmptyIterable<A>) underlying;
        } else {
            Tuple2<A, Iterable<A>> headTail = unconsOrThrow(underlying);
            return NonEmptyIterable.nonEmptyIterable(headTail._1(), headTail._2());
        }
    }

    static <A> ImmutableNonEmptyIterable<A> immutableNonEmptyIterableOrThrow(Iterable<A> underlying) {
        if (underlying instanceof ImmutableNonEmptyIterable<?>) {
            return (ImmutableNonEmptyIterable<A>) underlying;
        } else {
            Tuple2<A, Iterable<A>> headTail = unconsOrThrow(underlying);
            return ImmutableNonEmptyIterable.immutableNonEmptyIterable(headTail._1(), immutableIterable(headTail._2()));
        }
    }

    static <A> NonEmptyFiniteIterable<A> nonEmptyFiniteIterableOrThrow(Iterable<A> underlying) {
        if (underlying instanceof NonEmptyFiniteIterable<?>) {
            return (NonEmptyFiniteIterable<A>) underlying;
        } else {
            Tuple2<A, Iterable<A>> headTail = unconsOrThrow(underlying);
            return NonEmptyFiniteIterable.nonEmptyFiniteIterable(headTail._1(), finiteIterable(headTail._2()));
        }
    }

    static <A> ImmutableNonEmptyFiniteIterable<A> immutableNonEmptyFiniteIterableOrThrow(Iterable<A> underlying) {
        if (underlying instanceof ImmutableNonEmptyFiniteIterable<?>) {
            return (ImmutableNonEmptyFiniteIterable<A>) underlying;
        } else {
            Tuple2<A, Iterable<A>> headTail = unconsOrThrow(underlying);
            return ImmutableNonEmptyFiniteIterable.immutableNonEmptyFiniteIterable(headTail._1(),
                    immutableFiniteIterable(headTail._2()));
        }
    }

    @SafeVarargs
    static <A> ImmutableNonEmptyFiniteIterable<A> of(A first, A... more) {
        ImmutableFiniteIterable<A> tail = immutableFiniteIterable(asList(more));
        return new ImmutableNonEmptyFiniteIterable<A>() {
            @Override
            public A head() {
                return first;
            }

            @Override
            public ImmutableFiniteIterable<A> tail() {
                return tail;
            }

        };
    }

    private static <A> Tuple2<A, Iterable<A>> unconsOrThrow(Iterable<A> iterable) {
        return Uncons.uncons(iterable)
                .orElseThrow(() -> new IllegalArgumentException("iterable is empty"));
    }

}
