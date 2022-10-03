package software.kes.collectionviews.examples;

import software.kes.collectionviews.ImmutableNonEmptyVector;
import software.kes.collectionviews.ImmutableVector;
import software.kes.collectionviews.NonEmptyVector;
import software.kes.collectionviews.Vector;

import static java.util.Arrays.asList;

public class VectorExamples {

    private static void vectorExample1() {
        Integer[] arr = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        // Wrap an Integer array in a Vector.   No copy of the array is made.
        Vector<Integer> vector1 = Vector.wrap(arr);

        // You can also wrap an instance of java.util.List<A>, but for this
        // example we will wrap an array.

        System.out.println("vector1 = " + vector1);
        // *** vector1 = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        // Get the size in O(1) using `size`
        System.out.println("vector1.size() = " + vector1.size());
        // *** vector1.size() = 10

        // Safely get element at index using `get`
        System.out.println("vector1.get(0) = " + vector1.get(0));
        // *** vector1.get(0) = Just 1

        System.out.println("vector1.get(9) = " + vector1.get(9));
        // vector1.get(9) = Just 10

        System.out.println("vector1.get(100) = " + vector1.get(100));
        // *** vector1.get(100) = Nothing

        System.out.println("vector1.unsafeGet(5) = " + vector1.unsafeGet(5));
        // *** vector1.unsafeGet(5) = 6

        // Create slices of another Vector using take, drop, or slice:
        Vector<Integer> vector2 = vector1.take(5);
        System.out.println("vector2 = " + vector2);
        // *** vector2 = Vector(1, 2, 3, 4, 5)

        Vector<Integer> vector3 = vector1.drop(2);
        System.out.println("vector3 = " + vector3);
        // *** vector3 = Vector(3, 4, 5, 6, 7, 8, 9, 10)

        Vector<Integer> vector4 = vector1.slice(3, 7);
        System.out.println("vector4 = " + vector4);
        // *** vector4 = Vector(4, 5, 6, 7)

        // Map to a new Vector using fmap.
        // Doesn't make copies, and is stack-safe.
        Vector<Integer> vector5 = vector1.fmap(n -> n * 100);

        System.out.println("vector5 = " + vector5);
        // *** vector5 = Vector(100, 200, 300, 400, 500, 600, 700, 800, 900, 1000)

        Vector<String> vector6 = vector5.fmap(n -> "a" + n + "z");

        System.out.println("vector6 = " + vector6);
        // *** vector6 = Vector(a100z, a200z, a300z, a400z, a500z, a600z, a700z, a800z, a900z, a1000z)

        // The following is to prove that none of the above methods made a copy of the underlying array.
        // (Don't do this)
        arr[3] = 1000;
        System.out.println("vector1 = " + vector1);
        // *** vector1 = Vector(1, 2, 3, 1000, 5, 6, 7, 8, 9, 10)

        System.out.println("vector2 = " + vector2);
        // *** vector2 = Vector(1, 2, 3, 1000, 5)

        System.out.println("vector3 = " + vector3);
        // *** vector3 = Vector(3, 1000, 5, 6, 7, 8, 9, 10)

        System.out.println("vector4 = " + vector4);
        // *** vector4 = Vector(1000, 5, 6, 7)

        System.out.println("vector5 = " + vector5);
        // *** vector5 = Vector(100, 200, 300, 100000, 500, 600, 700, 800, 900, 1000)

        System.out.println("vector6 = " + vector6);
        // *** vector6 = Vector(a100z, a200z, a300z, a100000z, a500z, a600z, a700z, a800z, a900z, a1000z)

        // Switch it back.
        arr[3] = 4;

        // If you want to be sure you are complete protected from mutation, you can
        // upgrade to an `ImmutableVector`:
        ImmutableVector<Integer> vector7 = vector1.toImmutable();

        // Note that `toImmutable` may make a copy of the underlying structure,
        // but only if it is necessary.  Calling `toImmutable` on a Vector that
        // is already immutable is a no-op.

        System.out.println("vector7 = " + vector7);
        // *** vector7 = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        arr[3] = 1000;

        // vector1 was affected:
        System.out.println("vector1 = " + vector1);
        // *** vector1 = Vector(1, 2, 3, 1000, 5, 6, 7, 8, 9, 10)


        // vector7 was not:
        System.out.println("vector7 = " + vector7);
        // *** vector7 = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        // `ImmutableVector<A>` can be used anywhere an a `Vector<A>` is called for,
        // but it has the additional compile-time guarantee that it is protected
        // against mutation from anywhere.

        // `take`, `slice`, `drop`, and `fmap` on an `ImmutableVector` all yield `ImmutableVector`s for free:

        ImmutableVector<Integer> vector8 = vector7.take(5);
        ImmutableVector<Integer> vector9 = vector7.drop(2);
        ImmutableVector<Integer> vector10 = vector7.slice(3, 7);
        ImmutableVector<Integer> vector11 = vector7.fmap(n -> n * 100);

        // You can also create an `ImmutableVector` directly:
        ImmutableVector<String> vector12 = Vector.of("foo", "bar", "baz");

        System.out.println("vector12 = " + vector12);
        // *** vector12 = Vector(foo, bar, baz)

        // Note that `Vector.of` actually returns an instance of `ImmutableNonEmptyVector<String>`:
        ImmutableNonEmptyVector<String> vector13 = Vector.of("foo", "bar", "baz");

        // `NonEmpty` is another guarantee you can provide or require at compile-time.
        // `NonEmpty` provides a `head` method that is guaranteed to yield an element:

        System.out.println("vector13.head() = " + vector13.head());
        // *** vector13.head() = foo

        // All `NonEmptyVector`s can be used anywhere a `Vector` is called for, so
        // it is a concept you can opt-in to caring about.  All the following are legal:

        Vector<String> vector14 = vector13;
        ImmutableVector<String> vector15 = vector13;
        NonEmptyVector<String> vector16 = vector13;

        // Although, if you _require_ a `NonEmptyVector`, you need to provide one at compile-time.

        Vector<String> vector17 = Vector.empty();

        // The following line won't compile!
        //NonEmptyVector<String> vector18 = vector17;

        Vector<String> vector19 = Vector.wrap(asList("foo", "bar", "baz"));

        // Nor will this compile, since `Vector<String>` hasn't been proven non-empty at compile-time:
        // NonEmptyVector<String> vector20 = vector19;


    }

    public static void main(String[] args) {
        vectorExample1();
    }
}
