package dev.marksman.collectionviews;

abstract class ConcreteSet<A> implements Set<A> {

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Set<?>)) {
            return false;
        }
        return SetHelpers.setEquals(this, (Set<?>) o);
    }

    @Override
    public int hashCode() {
        return SetHelpers.setHashCode(this);
    }

    @Override
    public String toString() {
        return SetHelpers.setToString(this);
    }

}
