package dev.marksman.collectionviews;

import java.util.Iterator;

/**
 * Helper methods for implementers of custom {@link Vector}s.
 * <p>
 * If you are implementing a custom {@link Vector}, your {@code equals} and {@code hashCode}
 * methods SHOULD delegate to {@code vectorEquals} and {@code vectorHashCode}, respectively.  This will
 * ensure that equality works correctly with the built-in {@link Vector} types.
 * <p>
 * Your {@code toString} method MAY delegate to {@code setToString}.
 */
public class VectorHelpers {

    public static boolean vectorEquals(Vector<?> vector, Vector<?> other) {
        if (vector == null || other == null) {
            return false;
        }
        if (other == vector)
            return true;

        if (other.size() != vector.size()) {
            return false;
        }

        Iterator<?> e1 = vector.iterator();
        Iterator<?> e2 = other.iterator();
        while (e1.hasNext() && e2.hasNext()) {
            Object o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1 == null ? o2 == null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    public static int vectorHashCode(Vector<?> vector) {
        int hashCode = 1;
        for (Object e : vector)
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        return hashCode;
    }

    public static <A> Iterator<A> vectorIterator(Vector<A> vector) {
        return new VectorIterator<>(vector);
    }

    public static String vectorToString(Vector<?> vector) {
        StringBuilder output = new StringBuilder();
        output.append("Vector(");
        boolean inner = false;
        for (Object elem : vector) {
            if (inner) {
                output.append(", ");
            }
            if (elem == null) {
                output.append("null");
            } else {
                output.append(elem.toString());
            }
            inner = true;
        }
        output.append(')');
        return output.toString();
    }

}
