package dev.marksman.collectionviews;

abstract class AbstractVector<A> implements Vector<A> {

    @Override
    public String toString() {
        return Vectors.renderToString(this);
    }
}
