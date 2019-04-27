# collection-views

[![Build Status](https://travis-ci.org/kschuetz/collection-views.svg?branch=master)](https://travis-ci.org/kschuetz/collection-views)

This library has not yet been released.

# What is it?

**collection-views** is a small Java library that facilitates creating protected views over collections and arrays with as little overhead as possible.  It provides the interfaces `Vector<A>` and `Set<A>`, and some variations of these that provide additional guarantees. 

It is intended to be used in conjunction with [lambda](https://palatable.github.io/lambda/).

# Why? 

Sometimes you might want all of the following:

- To provide (or require) read-access to the essential operations of a collection (e.g., 'get by index' for arrays or lists, or 'contains' for sets), _and nothing more_.
- Protection from mutation to the collection without making defensive copies
- To retain the performance and locality of reference characteristics of the collection you are employing

The goal of **collection-views** is to provide this functionality with as little overhead as possible.

# What is it not?

**collection-views** is not a persistent data structures library.  Collections are wrapped as is;  no methods are provided for updating or adding to collections.  

It differs from [Guava Immutable Collections](https://github.com/google/guava/wiki/ImmutableCollectionsExplained) in that it acts as a shield over existing collections rather than being collections themselves.

# Types of collection views

| Interface | Immutable to bearer | Guaranteed safe from mutation anywhere | Guaranteed non-empty |
|---|---|---|---|
| `Vector<A>` | yes | no | no |
| `NonEmptyVector<A>` | yes | no | yes |
| `ImmutableVector<A>` | yes | yes | no |
| `ImmutableNonEmptyVector<A>` | yes | yes | yes |
| `Set<A>` | yes | no | no |
| `NonEmptySet<A>` | yes | no | yes |
| `ImmutableSet<A>` | yes | yes | no |
| `ImmutableNonEmptySet<A>` | yes | yes | yes |


## `Vector<A>`

The bearer of a `Vector` has the following capabilities:

- Random access to any element in the `Vector` using the `get` method.
- Get the size of the `Vector` in O(1) using the `size` method.
- Safely iterate the `Vector` or use it anywhere an `Iterable` is called for.  A `Vector` is always finite.
- Share the `Vector` with others safely.
- Make slices of the `Vector` using the `take`, `drop` or `slice` methods.  These slices are `Vector`s themselves, and can also be shared with others safely.
- Map to a new `Vector` of the same size but a different type using `fmap`.

The bearer of a `Vector` cannot:

- Mutate the contents of the underlying collection or array. 
- Gain access to the reference of the underlying collection or array.

### `Vector` examples

#### Basics

`Vector`s can wrap arrays or `java.util.List`s. 
They hold on to a reference of the data structure they wrap and never mutate it themselves, and are guaranteed to never make copies of the underlying data structure unless explicitly asked.

While a Vector can be mutated elsewhere (i.e. from someone else who holds a reference to the underlying collection), it can not be mutated by someone who only holds the `Vector` itself, so it is safe to share freely.

(Note: if you want complete protection from mutation _anywhere_, you can use an `ImmutabeVector` instead, which will be explained later).

The following wraps an `Integer` array in a `Vector`.  No copy of the array is made:
```Java
Integer[] arr = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

Vector<Integer> vector1 = Vector.wrap(arr);
```

You can also wrap an instance of `java.util.List<A>`, but for this
example we will wrap an array.

```Java
System.out.println("vector1 = " + vector1);
    // *** vector1 = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```

Get the size of the `Vector` in O(1) using the `size` method:

```Java
System.out.println("vector1.size() = " + vector1.size());
    // *** vector1.size() = 10
```

You can safely get an element at any index in O(1) using the `get` method:

```Java
System.out.println("vector1.get(0) = " + vector1.get(0));
    // *** vector1.get(0) = Just 1

System.out.println("vector1.get(9) = " + vector1.get(9));
    // *** vector1.get(9) = Just 10

System.out.println("vector1.get(100) = " + vector1.get(100));
    // *** vector1.get(100) = Nothing
```

Note that `get` returns a `Maybe<A>`.  If you pass get an invalid index, it will return `Maybe.nothing`:

```Java
System.out.println("vector1.get(100) = " + vector1.get(100));
    // *** vector1.get(100) = Nothing
```

`get` is also guaranteed to never return `null`.  If the underlying collection contains a `null` at the index requested, `get` will return `Maybe.nothing`.

You can also use the `unsafeGet` method if you want avoid the overhead of wrapping the result in a `Maybe`...

```Java
System.out.println("vector1.unsafeGet(5) = " + vector1.unsafeGet(5));
// *** vector1.unsafeGet(5) = 6

```

...but be aware, this method will throw an `IndexOutOfBoundsException` if you provide it an invalid index:
```Java
System.out.println("vector1.unsafeGet(1000) = "  + vector1.unsafeGet(1000));
// *** throws IndexOutOfBoundsException
```

Also, `unsafeGet` may return `null` if that is what the underlying collection contains.

#### Slices

You can create slices of another `Vector` using `take`, `drop`, or `slice`.  The results of these methods are also `Vectors`,
and none of them make copies of the original underlying data structure.

```Java
Vector<Integer> vector2 = vector1.take(5);
System.out.println("vector2 = " + vector2);
    // *** vector2 = Vector(1, 2, 3, 4, 5)

Vector<Integer> vector3 = vector1.drop(2);
System.out.println("vector3 = " + vector3);
    // *** vector3 = Vector(3, 4, 5, 6, 7, 8, 9, 10)

Vector<Integer> vector4 = vector1.slice(3, 7);
System.out.println("vector4 = " + vector4);
    // *** vector4 = Vector(4, 5, 6, 7)
```

#### Mapping

You can map to a new `Vector` using `fmap`.  This returns a new `Vector` and leaves the original `Vector` unaffected.
`fmap` never makes copies, and is stack-safe.

```Java
Vector<Integer> vector5 = vector1.fmap(n -> n * 100);

System.out.println("vector5 = " + vector5);
    // *** vector5 = Vector(100, 200, 300, 400, 500, 600, 700, 800, 900, 1000)

Vector<String> vector6 = vector5.fmap(n -> "a" + n + "z");

System.out.println("vector6 = " + vector6);
    // *** vector6 = Vector(a100z, a200z, a300z, a400z, a500z, a600z, a700z, a800z, a900z, a1000z)
```

TODO - more examples

### Creating a Vector

#### Wrapping an existing collection

A `Vector` can be created by wrapping an existing collection or array using one of the `Vector.wrap` static methods. 

`Vector.wrap`:

- Does *not* make a copy of the underlying collection.
- Will not alter the underlying collection in any way.
- Will maintain a reference to the collection you wrap.

The underlying collection is protected against mutation from anyone you share the `Vector` with.  However, note that anyone who has a reference to the underlying collection is still able to mutate it.  Therefore, it is highly recommended that you do not mutate the collection or share the underlying collection with anyone else who might mutate it. 

#### Taking from an `Iterable<A>`

A `Vector` can be constructed from an `Iterable` using the `sliceFromIterable` or `takeFromIterable` methods.

`sliceFromIterable` or `takeFromIterable` may make a copy of the data, but will only do so if necessary.  If passed a `java.util.List` or another `Vector`, it will create the appropriate sized view of that collection without making a copy.  For other `Iterable`s that can't guarantee random access, these methods may make a copy internally before wrapping it.

#### Building using `Vector.of`

Calling `Vector.of` with one or more elements will return a new `ImmutableNonEmptyVector`.  Since the underlying collection in a `Vector` created this way is not exposed anywhere, it is 100% safe from mutation. 

#### Creating an empty `Vector`

The `Vector.empty` static method will return an empty ``Vector<A>``.

## `NonEmptyVector<A>`

A `NonEmptyVector<A>` is a ``Vector<A>`` that is guaranteed to contain at least one element.  It is not possible to construct a `NonEmptyVector` that is empty.  Since it is also a ``Vector<A>``, it can be used anywhere a ``Vector<A>`` is called for.

### Using a `NonEmptyVector`

A `NonEmpty`Vector<A>`` has all of the properties of a ``Vector<A>``, with the additional property that it subtypes `NonEmptyIterable<A>`, providing a `head` method that is guaranteed to yield an element.

### Creating a `NonEmptyVector`

#### Wrapping an existing collection

`NonEmptyVector.tryWrap` takes an array, a `java.util.List`, or a `Vector` as an argument, and returns a `Maybe<NonEmptyVector<A>>`.  If the provided collection is not empty, a `NonEmptyVector` will be created and returned in a `Maybe.just`, otherwise `Maybe.nothing` will be returned.

Alternatively, if you know for sure that the collection you are passing is not empty, then you can call `NonEmptyVector.wrapOrThrow`.  This will either return the `NonEmptyVector` directly, or throw an `IllegalArgumentException` if the provided collection is empty.

`NonEmptyVector.tryWrap` and `NonEmptyVector.wrapOrThrow` behave similarly to `Vector.wrap` in that a copy of the underlying collection is never made.

#### Building using `NonEmptyVector.of`

`Vector.of` always returns a `NonEmptyVector`.  `NonEmptyVector.of` is effectively an alias of `Vector.of`.
