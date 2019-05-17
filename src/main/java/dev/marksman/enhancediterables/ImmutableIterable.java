package dev.marksman.enhancediterables;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.builtin.fn1.Tails;
import com.jnape.palatable.lambda.functions.builtin.fn2.Drop;
import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import com.jnape.palatable.lambda.functions.builtin.fn2.Take;
import com.jnape.palatable.lambda.functions.builtin.fn3.ZipWith;

import static dev.marksman.enhancediterables.EnhancedIterables.immutableIterable;
import static dev.marksman.enhancediterables.EnhancedIterables.nonEmptyIterableOrThrow;

public interface ImmutableIterable<A> extends EnhancedIterable<A> {

    @Override
    default ImmutableIterable<A> drop(int count) {
        return immutableIterable(Drop.drop(count, this));
    }

    @Override
    default <B> ImmutableIterable<B> fmap(Fn1<? super A, ? extends B> f) {
        return immutableIterable(Map.map(f, this));
    }

    @Override
    default NonEmptyIterable<? extends ImmutableIterable<A>> tails() {
        return nonEmptyIterableOrThrow(Map.map(EnhancedIterables::immutableIterable, Tails.tails(this)));
    }

    @Override
    default ImmutableIterable<A> take(int count) {
        return immutableIterable(Take.take(count, this));
    }

    default <B, R> ImmutableIterable<R> zipWith(Fn2<A, B, R> fn, ImmutableIterable<B> other) {
        return immutableIterable(ZipWith.zipWith(fn.toBiFunction(), this, other));
    }

}
