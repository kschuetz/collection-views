package software.kes.collectionviews;

import com.jnape.palatable.lambda.functions.Fn3;

import java.util.function.Supplier;

import static software.kes.collectionviews.Validation.validateDrop;

final class VectorSlicing {

    private VectorSlicing() {

    }

    @SuppressWarnings("unchecked")
    static <A, V extends Vector<A>> V sliceImpl(Fn3<Integer, Integer, V, V> factory, int sourceSize,
                                                Supplier<V> underlyingSupplier, int startIndex, int requestedSize) {
        if (startIndex == 0 && requestedSize >= sourceSize) {
            return underlyingSupplier.get();
        } else if (startIndex >= sourceSize) {
            return (V) Vectors.empty();
        } else {
            int available = Math.max(sourceSize - startIndex, 0);
            int sliceSize = Math.min(available, requestedSize);
            return factory.apply(startIndex, sliceSize, underlyingSupplier.get());
        }
    }

    @SuppressWarnings("unchecked")
    static <A, V extends Vector<A>> V dropImpl(Fn3<Integer, Integer, V, V> factory, int count, V source) {
        validateDrop(count, source);
        if (count == 0) {
            return source;
        }
        int sourceSize = source.size();
        if (count >= sourceSize) {
            return (V) Vectors.empty();
        }
        return factory.apply(count, sourceSize - count, source);
    }

}
