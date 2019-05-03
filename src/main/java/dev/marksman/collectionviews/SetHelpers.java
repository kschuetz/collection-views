package dev.marksman.collectionviews;

/**
 * Helper methods for implementers of custom {@link Set}s.
 * <p>
 * If you are implementing a custom {@link Set}, your {@code equals} and {@code hashCode}
 * methods SHOULD delegate to {@code setEquals} and {@code setHashCode}, respectively.  This will
 * ensure that equality works correctly with the built-in {@link Set} types.
 * <p>
 * Your {@code toString} method MAY delegate to {@code setToString}.
 */
public final class SetHelpers {

    private SetHelpers() {

    }

    @SuppressWarnings("unchecked")
    public static boolean setEquals(Set<?> set, Set<?> other) {
        if (set == null || other == null) {
            return false;
        }
        if (other == set)
            return true;

        if (other.size() != set.size()) {
            return false;
        }
        for (Object elem : set) {
            if (!((Set<Object>) other).contains(elem)) {
                return false;
            }
        }
        return true;
    }

    public static int setHashCode(Set<?> set) {
        int h = 0;
        for (Object obj : set) {
            if (obj != null)
                h += obj.hashCode();
        }
        return h;
    }

    public static String setToString(Set<?> set) {
        StringBuilder output = new StringBuilder();
        output.append("Set(");
        boolean inner = false;
        for (Object elem : set) {
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
