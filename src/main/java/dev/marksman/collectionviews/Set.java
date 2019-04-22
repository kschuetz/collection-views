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

    /**
     * Wraps a {@link java.util.Set} in a {@link Set}.
     *
     * <p>
     * Does *not* make a copy of the given {@link java.util.Set}.
     * The Vector will hold a reference to the given Set, but will not alter it in any way.
     * <p>
     * Since bearers of this Set will be unable to mutate or gain access to the underlying data,
     * it is safe to share.
     * <p>
     * Since this does not make a copy of the underlying data, be aware that anyone that holds a
     * direct reference to it the java.util.Set can still mutate it.
     *
     * @param underlying
     * @param <A>
     * @return
     */
    static <A> Set<A> wrap(java.util.Set<A> underlying) {
        return Sets.wrap(underlying);
    }

    @SafeVarargs
    static <A> NonEmptySet<A> of(A first, A... more) {
        return Sets.nonEmptySetOf(first, more);
    }
}
