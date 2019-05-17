package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Tails;
import com.jnape.palatable.lambda.functions.builtin.fn2.Drop;
import com.jnape.palatable.lambda.functions.builtin.fn2.Find;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import com.jnape.palatable.lambda.functions.builtin.fn2.Take;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;

import static dev.marksman.enhancediterables.EnhancedIterables.enhancedIterable;
import static dev.marksman.enhancediterables.EnhancedIterables.nonEmptyIterableOrThrow;

public interface EnhancedIterable<A> extends Iterable<A> {

    default EnhancedIterable<A> drop(int count) {
        return enhancedIterable(Drop.drop(count, this));
    }

    default Maybe<A> find(Fn1<? super A, ? extends Boolean> predicate) {
        return Find.find(predicate, this);
    }

    default <B> EnhancedIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return enhancedIterable(Map.map(f, this));
    }

    default boolean isEmpty() {
        return !iterator().hasNext();
    }

    default NonEmptyIterable<? extends EnhancedIterable<A>> tails() {
        return nonEmptyIterableOrThrow(Map.map(EnhancedIterables::enhancedIterable, Tails.tails(this)));
    }

    default EnhancedIterable<A> take(int count) {
        return enhancedIterable(Take.take(count, this));
    }

    default <B, R> EnhancedIterable<R> zipWith(Fn2<A, B, R> fn, Iterable<B> other) {
        return enhancedIterable(ZipWith.zipWith(fn.toBiFunction(), this, other));
    }

}
