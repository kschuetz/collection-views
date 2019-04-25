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
    MapperChain add(Function f);

    Fn1<Object, Object> getFn();

    Object apply(Object input);

    boolean isEmpty();

    static MapperChain empty() {
        return EmptyMapperChain.INSTANCE;
    }

    static MapperChain mapperChain(Function f) {
        return new MapperChainImpl(Collections.singletonList(f));
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
        public MapperChain add(Function f) {
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
        private Fn1<Object, Object> fnComposedOnTheHeap;

        private MapperChainImpl(Iterable<Function> mappers) {
            this.mappers = mappers;
            fnComposedOnTheHeap = null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        public MapperChainImpl add(Function f) {
            return new MapperChainImpl(cons(f, mappers));
        }

        @SuppressWarnings("unchecked")
        public Object apply(Object input) {
            return getFn().apply(input);
        }

        public Fn1<Object, Object> getFn() {
            // TODO: improve synchronization of getFn
            synchronized (this) {
                if (fnComposedOnTheHeap == null) {
                    fnComposedOnTheHeap = build();
                }
                return fnComposedOnTheHeap;
            }
        }

        @SuppressWarnings("unchecked")
        private Fn1<Object, Object> build() {
            ArrayList<Function> fnChain = toCollection(ArrayList::new, reverse(mappers));
            return o -> foldLeft((x, fn) -> fn.apply(x), o, fnChain);
        }
    }

}
