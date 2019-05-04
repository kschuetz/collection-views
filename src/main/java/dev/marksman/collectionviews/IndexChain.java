package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;

import java.util.ArrayList;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Cons.cons;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static java.util.Collections.singletonList;

/**
 * Used for stack-safety when taking slices of LazyVectors.
 */
interface IndexChain extends Fn1<Integer, Integer> {

    IndexChain contraMap(Fn1<Integer, Integer> f);

    static IndexChain identity() {
        return IndexChainIdentity.INSTANCE;
    }

    class IndexChainIdentity implements IndexChain {
        static final IndexChainIdentity INSTANCE = new IndexChainIdentity();

        @Override
        public IndexChain contraMap(Fn1<Integer, Integer> f) {
            return new ContraMappedIndexChain(singletonList(f));
        }

        @Override
        public Integer apply(Integer input) {
            return input;
        }
    }

    class ContraMappedIndexChain implements IndexChain {
        private final Iterable<Fn1<Integer, Integer>> contraMappers;
        private volatile Fn1<Integer, Integer> fnComposedOnTheHeap;

        ContraMappedIndexChain(Iterable<Fn1<Integer, Integer>> contraMappers) {
            this.contraMappers = contraMappers;
            fnComposedOnTheHeap = null;
        }

        public IndexChain contraMap(Fn1<Integer, Integer> f) {
            return new ContraMappedIndexChain(cons(f, contraMappers));
        }

        public Fn1<Integer, Integer> getFn() {
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
        private Fn1<Integer, Integer> build() {
            ArrayList<Fn1<Integer, Integer>> fnChain = toCollection(ArrayList::new, contraMappers);
            return o -> foldLeft((x, fn) -> fn.apply(x), o, fnChain);
        }

        @Override
        public Integer apply(Integer input) {
            return getFn().apply(input);
        }

    }

}
