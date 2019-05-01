package dev.marksman.collectionviews;

abstract class ConcreteVector<A> implements Vector<A> {

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Vector<?>))
            return false;
        return VectorHelpers.vectorEquals(this, (Vector<?>) o);
    }

    @Override
    public int hashCode() {
        return VectorHelpers.vectorHashCode(this);
    }

    @Override
    public String toString() {
        return VectorHelpers.vectorToString(this);
    }

}
