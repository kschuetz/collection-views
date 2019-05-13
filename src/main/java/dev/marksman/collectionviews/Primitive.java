package dev.marksman.collectionviews;

/**
 * A marker interface for indicating that a view is "primitive", i.e., it is not a transformation of another view.
 * <p>
 * {@code Primitive} is only of concern to implementers of custom views.
 * <p>
 * It is optional for custom views to mix in this trait.
 * If present, internal copying routines in the collection-views library may use this as a hint for optimization.
 * <p>
 * While <i>presence</i> of {@code Primitive} indicates that the view is not a transformation,
 * you should not rely on the absence of {@code Primitive} to indicate that it <i>is</i> a transformation.
 */
public interface Primitive {
}
