# collection-views

[![Build Status](https://travis-ci.org/kschuetz/collection-views.svg?branch=master)](https://travis-ci.org/kschuetz/collection-views)

# What is it?

**collection-views** is a small Java library that facilitates creating protected views over collections and arrays with as little overhead as possible.  It provides the interfaces `Vector<A>`, `Set<A>`, `NonEmptyVector<A>`, and `NonEmptySet<A>`, and default implementations for each of these.

It is intended to be used in conjunction with [lambda](https://palatable.github.io/lambda/).

# Why? 

To provide controlled access to the essential operations of a collection (and no more) without the need for defensive copying.

# What is it not?

**collection-views** is not a persistent data structures library.  Collections are wrapped as is;  no methods are provided for updating or adding to collections.


# Types of collection views

## `Vector<A>`

### Using a `Vector`

The bearer of a `Vector` has the following capabilities:

- Random access to any element in the `Vector` using the `get` method.
- Get the size of the `Vector` in O(1) using the `size` method.
- Safely iterate the `Vector` or use it anywhere an `Iterable` is called for.  A `Vector` is always finite.
- Share the `Vector` with others safely.
- Make slices of the `Vector` using the `take`, `drop` or `slice` methods.  These slices are `Vector`s themselves, and can also be shared with others safely.

The bearer of a `Vector` cannot:

- Mutate the contents of the underlying collection or array. 
- Gain access to the reference of the underlying collection or array.

### Creating a Vector

#### Wrapping an existing collection

A `Vector` can be created by wrapping an existing collection or array using one of the `Vector.wrap` static methods. 

`Vector.wrap`:

- Does *not* make a copy of the underlying collection.
- Will not alter the underlying collection in any way.
- Will maintain a reference to the collection you wrap.

The underlying collection is protected against mutation from anyone you share the `Vector` with.  However, note that anyone who has a reference to the underlying collection is still able to mutate it.  Therefore, it is highly recommended that you do not mutate the collection or share the underlying collection with anyone else who might mutate it. 

If you prefer to make a copy that is 100% safe from mutation, just clone the original collection and wrap the clone. 

#### Taking from an `Iterable<A>`

A `Vector` can be constructed from an `Iterable` using the `sliceFromIterable` or `takeFromIterable` methods.

`sliceFromIterable` or `takeFromIterable` may make a copy of the data, but will only do so if necessary.  If passed a `java.util.List` or another `Vector`, it will create the appropriate sized view of that collection without making a copy.  For other `Iterable`s that can't guarantee random access, these methods may make a copy internally before wrapping it.

#### Building using `Vector.of`

Calling `Vector.of` with one or more elements will return a new `NonEmptyVector`.  Since the underlying collection in a `Vector` created this way is not exposed anywhere, it is 100% safe from mutation. 

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
