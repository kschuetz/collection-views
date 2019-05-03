package dev.marksman.collectionviews;

final class Util {

    private Util() {

    }

    static String iterableToString(String prefix, Iterable<?> iterable) {
        StringBuilder output = new StringBuilder();
        output.append(prefix);
        output.append('(');
        boolean inner = false;
        for (Object elem : iterable) {
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
