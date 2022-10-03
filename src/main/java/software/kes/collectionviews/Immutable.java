package software.kes.collectionviews;

/**
 * A marker interface for immutability.
 * If a class subtypes this interface, it is indicating that the resource that any instance of that class protects
 * is 100% safe from mutation anywhere in the system.
 */
@SuppressWarnings("WeakerAccess")
public interface Immutable {
}
