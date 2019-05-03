package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Reverse.reverse;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Cons.cons;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;

interface MapperChain {
    MapperChain add(Fn1<Object, Object> f);

    Fn1<Object, Object> getFn();

    Object apply(Object input);

    boolean isEmpty();

    static MapperChain empty() {
        return EmptyMapperChain.INSTANCE;
    }

    static MapperChain mapperChain(Fn1<Object, Object> f) {
        return new MapperChainImpl(Collections.singletonList(lambdaToJava(f)));
    }

    class EmptyMapperChain implements MapperChain {
        static EmptyMapperChain INSTANCE = new EmptyMapperChain();

        private EmptyMapperChain() {

        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public MapperChain add(Fn1<Object, Object> f) {
            return mapperChain(f);
        }

        @Override
        public Fn1<Object, Object> getFn() {
            return id();
        }

        @Override
        public Object apply(Object input) {
            return input;
        }

    }

    class MapperChainImpl implements MapperChain {
        private Iterable<Function> mappers;
        private volatile Fn1<Object, Object> fnComposedOnTheHeap;

        private MapperChainImpl(Iterable<Function> mappers) {
            this.mappers = mappers;
            fnComposedOnTheHeap = null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        public MapperChainImpl add(Fn1<Object, Object> f) {
            return new MapperChainImpl(cons(lambdaToJava(f), mappers));
        }

        public Object apply(Object input) {
            return getFn().apply(input);
        }

        public Fn1<Object, Object> getFn() {
            if (fnComposedOnTheHeap == null) {
                synchronized (this) {
                    if (fnComposedOnTheHeap == null) {
                        fnComposedOnTheHeap = build();
                    }

                }
            }
            return fnComposedOnTheHeap;
        }

        @SuppressWarnings("unchecked")
        private Fn1<Object, Object> build() {
            ArrayList<Function> fnChain = toCollection(ArrayList::new, reverse(mappers));
            return o -> foldLeft((x, fn) -> fn.apply(x), o, fnChain);
        }
    }

    @SuppressWarnings("FunctionalExpressionCanBeFolded")
    static Function lambdaToJava(Fn1<Object, Object> f) {
        return f::apply;
    }

}
