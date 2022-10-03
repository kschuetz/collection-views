package software.kes.collectionviews;

/**
 * Helper methods for implementers of custom {@link Set}s.
 * <p>
 * If you are implementing a custom {@link Set}, your {@code equals} and {@code hashCode}
 * methods SHOULD delegate to {@code setEquals} and {@code setHashCode}, respectively.  This will
 * ensure that equality works correctly with the built-in {@link Set} types.
 * <p>
 * Your {@code toString} method MAY delegate to {@code setToString}.
 */
@SuppressWarnings("WeakerAccess")
public final class SetHelpers {

    private SetHelpers() {

    }

    @SuppressWarnings("unchecked")
    public static boolean setEquals(Set<?> set, Set<?> other) {
        if (set == null || other == null) {
            return false;
        }
        if (other == set) {
            return true;
        }
        if (other.size() != set.size()) {
            return false;
        }
        return containsAllElements((Set<Object>) other, (Iterable<Object>) set);
    }

    public static int setHashCode(Set<?> set) {
        int result = 0;
        for (Object obj : set) {
            if (obj != null) {
                result += obj.hashCode();
            }
        }
        return result;
    }

    public static String setToString(Set<?> set) {
        return Util.iterableToString("Set", set);
    }

    private static boolean containsAllElements(Set<Object> set, Iterable<Object> elements) {
        for (Object elem : elements) {
            if (!set.contains(elem)) {
                return false;
            }
        }
        return true;
    }

}
