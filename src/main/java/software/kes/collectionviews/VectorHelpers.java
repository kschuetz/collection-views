package software.kes.collectionviews;

import java.util.Iterator;
import java.util.Objects;

/**
 * Helper methods for implementers of custom {@link Vector}s.
 * <p>
 * If you are implementing a custom {@link Vector}, your {@code equals} and {@code hashCode}
 * methods SHOULD delegate to {@code vectorEquals} and {@code vectorHashCode}, respectively.  This will
 * ensure that equality works correctly with the built-in {@link Vector} types.
 * <p>
 * Your {@code toString} method MAY delegate to {@code vectorToString}.
 */
@SuppressWarnings("WeakerAccess")
public final class VectorHelpers {

    private VectorHelpers() {

    }

    public static boolean vectorEquals(Vector<?> vector, Vector<?> other) {
        if (vector == null || other == null) {
            return false;
        }
        if (other == vector) {
            return true;
        } else {
            return (other.size() == vector.size())
                    && allElementsEqual(vector.iterator(), other.iterator());
        }
    }

    public static int vectorHashCode(Vector<?> vector) {
        int hashCode = 1;
        for (Object e : vector) {
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }

    public static <A> Iterator<A> vectorIterator(Vector<A> vector) {
        return new VectorIterator<>(vector);
    }

    public static String vectorToString(Vector<?> vector) {
        return Util.iterableToString("Vector", vector);
    }

    private static boolean allElementsEqual(Iterator<?> e1, Iterator<?> e2) {
        while (e1.hasNext() && e2.hasNext()) {
            Object o1 = e1.next();
            Object o2 = e2.next();
            if (!(Objects.equals(o1, o2))) {
                return false;
            }
        }
        return !(e1.hasNext() || e2.hasNext());
    }

}
