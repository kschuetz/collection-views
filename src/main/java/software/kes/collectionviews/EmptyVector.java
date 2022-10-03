package software.kes.collectionviews;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;

import java.util.Iterator;
import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.util.Collections.emptyIterator;
import static software.kes.collectionviews.Validation.validateDrop;
import static software.kes.collectionviews.Validation.validateSlice;
import static software.kes.collectionviews.Validation.validateTake;

final class EmptyVector<A> extends ConcreteVector<A> implements ImmutableVector<A>, Primitive {
    private static final EmptyVector<?> INSTANCE = new EmptyVector<>();

    @Override
    public int size() {
        return 0;
    }

    @Override
    public A unsafeGet(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public Maybe<A> find(Fn1<? super A, ? extends Boolean> predicate) {
        Objects.requireNonNull(predicate);
        return nothing();
    }

    @Override
    public Maybe<Integer> findIndex(Fn1<? super A, ? extends Boolean> predicate) {
        Objects.requireNonNull(predicate);
        return nothing();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ImmutableVector<A> drop(int count) {
        validateDrop(count);
        return this;
    }

    @Override
    public ImmutableVector<A> dropRight(int count) {
        validateDrop(count);
        return this;
    }

    @Override
    public ImmutableVector<A> dropWhile(Fn1<? super A, ? extends Boolean> predicate) {
        Objects.requireNonNull(predicate);
        return this;
    }

    @Override
    public ImmutableVector<A> reverse() {
        return this;
    }

    @Override
    public ImmutableVector<A> slice(int startIndex, int endIndexExclusive) {
        validateSlice(startIndex, endIndexExclusive);
        return this;
    }

    @Override
    public ImmutableVector<A> take(int count) {
        validateTake(count);
        return this;
    }

    @Override
    public ImmutableVector<A> takeRight(int count) {
        validateTake(count);
        return this;
    }

    @Override
    public ImmutableVector<A> takeWhile(Fn1<? super A, ? extends Boolean> predicate) {
        Objects.requireNonNull(predicate);
        return this;
    }

    @Override
    public ImmutableVector<A> toImmutable() {
        return this;
    }

    @Override
    public ImmutableVector<Tuple2<A, Integer>> zipWithIndex() {
        return emptyVector();
    }

    @Override
    public Maybe<A> get(int index) {
        return nothing();
    }

    @Override
    public <B> ImmutableVector<B> fmap(Fn1<? super A, ? extends B> f) {
        return emptyVector();
    }

    @Override
    public Iterator<A> iterator() {
        return emptyIterator();
    }

    @Override
    public Maybe<? extends ImmutableNonEmptyVector<A>> toNonEmpty() {
        return nothing();
    }

    @Override
    public ImmutableNonEmptyVector<A> toNonEmptyOrThrow() {
        throw Vectors.nonEmptyError().apply();
    }

    @SuppressWarnings("unchecked")
    static <A> EmptyVector<A> emptyVector() {
        return (EmptyVector<A>) INSTANCE;
    }

}
