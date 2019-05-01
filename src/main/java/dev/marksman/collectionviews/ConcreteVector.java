package dev.marksman.collectionviews;

import java.util.Iterator;

abstract class ConcreteVector<A> implements Vector<A> {

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Vector<?>))
            return false;
        Vector<?> other = (Vector<?>) o;
        if (other.size() != size()) {
            return false;
        }

        Iterator<A> e1 = iterator();
        Iterator<?> e2 = other.iterator();
        while (e1.hasNext() && e2.hasNext()) {
            A o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1 == null ? o2 == null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (A e : this)
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        return hashCode;
    }

    @Override
    public String toString() {
        return Vectors.renderToString(this);
    }

}
