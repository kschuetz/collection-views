package dev.marksman.collectionviews;

abstract class ConcreteSet<A> implements Set<A> {

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Set<?>))
            return false;
        Set<Object> other = (Set<Object>) o;
        if (other.size() != size()) {
            return false;
        }
        for (A elem : this) {
            if (!other.contains(elem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (A obj : this) {
            if (obj != null)
                h += obj.hashCode();
        }
        return h;
    }

    @Override
    public String toString() {
        return Sets.renderToString(this);
    }

}
