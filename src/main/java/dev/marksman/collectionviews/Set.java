package dev.marksman.collectionviews;

public interface Set<A> extends Iterable<A> {
    int size();

    boolean contains(A element);

    default boolean isEmpty() {
        return size() == 0;
    }

    static <A> Set<A> empty() {
        return Sets.empty();
    }

    static <A> Set<A> wrap(java.util.Set<A> underlying) {
        return Sets.wrap(underlying);
    }

    @SafeVarargs
    static <A> NonEmptySet<A> of(A first, A... more) {
        return Sets.nonEmptySetOf(first, more);
    }
}
