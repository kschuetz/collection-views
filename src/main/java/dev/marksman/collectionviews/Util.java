package dev.marksman.collectionviews;

final class Util {

    private Util() {

    }

    static String iterableToString(String prefix, Iterable<?> iterable) {
        StringBuilder output = new StringBuilder();
        output.append(prefix);
        output.append('(');
        renderElements(output, iterable);
        output.append(')');
        return output.toString();
    }

    static boolean isPrimitive(Object obj) {
        return (obj instanceof Primitive);
    }

    private static void renderElements(StringBuilder output, Iterable<?> iterable) {
        boolean inner = false;
        for (Object elem : iterable) {
            if (inner) {
                output.append(", ");
            }
            renderElement(output, elem);
            inner = true;
        }
    }

    private static void renderElement(StringBuilder output, Object elem) {
        if (elem == null) {
            output.append("null");
        } else {
            output.append(elem.toString());
        }
    }

}
