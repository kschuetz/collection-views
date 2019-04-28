# collection-views

[![Build Status](https://travis-ci.org/kschuetz/collection-views.svg?branch=master)](https://travis-ci.org/kschuetz/collection-views)
[![collection-views](https://img.shields.io/maven-central/v/dev.marksman/collection-views.svg)](http://search.maven.org/#search%7Cga%7C1%7Cdev.marksman.collection-views)

This library has not yet been released.

#### Table of Contents

 - [What is it?](#what-is-it)
 - [Why](#why)
 - [What is it not?](#what-is-it-not)
 - [Design principles](#design-principles)
 - [Type of collection views](#types)
   - [`Vector<A>`](#vector)
   - [`NonEmptyVector<A>`](#non-empty-vectors)
   - [`ImmutableVector<A>`](#immutable-vector)
   - [`ImmutableNonEmptyVector<A>`](#immutable-non-empty-vector)
 - [Examples](#examples)
   - [`Vector`](#vector-examples)
 - [License](#license)   
       

# <a name="what-is-it">What is it?</a>

**collection-views** is a small Java library that facilitates creating protected views over collections and arrays with as little overhead as possible.  It provides the interfaces `Vector<A>` and `Set<A>`, and some variations of these that provide additional guarantees. 

It is intended to be used in conjunction with [lambda](https://palatable.github.io/lambda/).

Check out the [javadoc](https://kschuetz.github.io/collection-views/javadoc/).

# <a name="why">Why?</a> 

Sometimes you might want all of the following:

- To provide (or require) read-access to the essential operations of a collection (e.g., 'get by index' for arrays or lists, or 'contains' for sets), _and nothing more_.
- Protection from mutation to the collection without making defensive copies.
- To retain the performance and locality of reference characteristics of the collection you are employing.

The goal of **collection-views** is to provide this functionality with as little overhead as possible.

# <a name="what-is-it-not">What is it not?</a>

**collection-views** is not a persistent data structures library.  Collections are wrapped as is;  no methods are provided for updating or adding to collections.  

It differs from [Guava Immutable Collections](https://github.com/google/guava/wiki/ImmutableCollectionsExplained) in that it acts as a shield over existing collections rather than being collections themselves.

# <a name="design-principles">Design principles</a>

**collection-views** is guided by the following design principles and goals:

## Only the essentials

Only the essential read-only operations of underlying collections are exposed.  The consumer of a `Vector` will only be able to iterate its elements, check its size, or read an element by index.  The consumer of a `Set` will only be able to iterate its elements, check its size, or test for membership.

## Immutable to bearer

A collection view can be shared safely without making a defensive copy.  There is no way the bearer of a collection view can mutate the collection or gain access to a reference to the underlying collection.

## Correct by construction

It should be impossible to construct an instance of an interface that does not uphold its guarantees.  For example, there is no way to create `NonEmptyVector` that contains no elements.  Therefore, you can be assured at compile-time that if you require a `NonEmptyVector`, you will not need to check at run-time if it is non-empty.

## Don't make copies

Methods should never make a copy of an underlying collection unless explicitly asked to, or if it is necessary to uphold the guarantees of its interface.  For example, there is no reason `fmap`, `take`, or `slice` on a `Vector` should need to make a copy of the underlying collection to uphold the random access guarantee on a `Vector`.   

In contrast, if a `Vector` is to be constructed from an `Iterable` that is not known to have a random access guarantee, then in that case a copy will be made so `Vector` can fulfill this guarantee. 

## Opt-in to guarantees

If you simply prefer to work with `Vector`s or `Set`s and not think about non-emptiness or immutability, you can do so.  For example, since `NonEmptyVector<A>`, `ImmutableVector<A>`, and `ImmutableNonEmptyVector<A>` are all subtypes of `Vector<A>`, you can use them anywhere a `Vector<A>` is called for.

## Provide transformations, but only if they don't violate other principles

Although methods for updating and adding to collections are not available, some transformations that can be performed without violating other design principles (e.g. don't make copies), and can be done so with low overhead, are provided.

Examples include `fmap`, `take`, `drop`, and `slice` on `Vector`s.  Each of these transforms a view of a collection without making copies, or violating any guarantees.

The views yielded by these transformation are new, independent views, and do not affect the original in any way. 

# <a name="types">Types of collection views</a>

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

## <a name="vector">`Vector<A>`</a>

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

### <a name="creating-vectors">Creating `Vectors`</a>

Several static methods are available for creating `Vector`s:

| Method | Returns | Makes a copy | Caveats |
|---|---|---|---|
| `Vector.empty` | `ImmutableVector<A>` | N/A |  |  
| `Vector.of` | `ImmutableNonEmptyVector<A>` | N/A |  |  
| `Vector.wrap` | `Vector<A>` | no |  |  
| `Vector.copyFrom` | `ImmutableVector<A>` | yes| may not terminate if input is infinite | 
| `Vector.copySliceFrom` |`ImmutableVector<A>` | yes |  |
| `NonEmptyVector.tryWrap` |`Maybe<NonEmptyVector<A>>` | no |  |
| `NonEmptyVector.wrapOrThrow` |`NonEmptyVector<A>` | no | may throw exceptions |
| `NonEmptyVector.tryCopyFrom` |`Maybe<ImmutableNonEmptyVector<A>>` | yes |  |
| `NonEmptyVector.copyFromOrThrow` |`ImmutableNonEmptyVector<A>` | yes | may throw exceptions |

#### <a name="vector-wrapping">Wrapping an existing collection</a>

A `Vector` can be created by wrapping an existing collection or array using one of the `Vector.wrap` static methods. 

`Vector.wrap`:

- Does *not* make a copy of the underlying collection.
- Will not alter the underlying collection in any way.
- Will maintain a reference to the collection you wrap.

The underlying collection is protected against mutation from anyone you share the `Vector` with.  However, note that anyone who has a reference to the underlying collection is still able to mutate it.  Therefore, it is highly recommended that you do not mutate the collection or share the underlying collection with anyone else who might mutate it. 

#### <a name="vector-from-iterable">Taking from an `Iterable<A>`</a>

A `Vector` can be constructed from an `Iterable` using the `sliceFromIterable` or `takeFromIterable` methods.

`sliceFromIterable` or `takeFromIterable` may make a copy of the data, but will only do so if necessary.  If passed a `java.util.List` or another `Vector`, it will create the appropriate sized view of that collection without making a copy.  For other `Iterable`s that can't guarantee random access, these methods may make a copy internally before wrapping it.

#### <a name="vector-of">Building using `Vector.of`</a>

Calling `Vector.of` with one or more elements will return a new `ImmutableNonEmptyVector`.  Since the underlying collection in a `Vector` created this way is not exposed anywhere, it is 100% safe from mutation. 

#### <a name="vector-empty">Creating an empty `Vector`</a>

The `Vector.empty` static method will return an empty ``ImmutableVector<A>``.

## <a name="non-empty-vector">`NonEmptyVector<A>`</a>

A `NonEmptyVector<A>` is a `Vector<A>` that is known at compile-time to contain at least one element.  It is not possible to construct a `NonEmptyVector` that is empty.  Since it is also a ``Vector<A>``, it can be used anywhere a ``Vector<A>`` is called for.
`NonEmptyVector<A>` subtypes `NonEmptyIterable<A>`, providing a `head` method that is guaranteed to yield an element.

### <a name="creating-non-empty-vectors">Creating a `NonEmptyVector`</a>

#### <a name="non-empty-vector-wrapping">Wrapping an existing collection</a>

`NonEmptyVector.tryWrap` takes an array, a `java.util.List`, or a `Vector` as an argument, and returns a `Maybe<NonEmptyVector<A>>`.  If the provided collection is not empty, a `NonEmptyVector` will be created and returned in a `Maybe.just`, otherwise `Maybe.nothing` will be returned.

Alternatively, if you know for sure that the collection you are passing is not empty, then you can call `NonEmptyVector.wrapOrThrow`.  This will either return the `NonEmptyVector` directly, or throw an `IllegalArgumentException` if the provided collection is empty.

`NonEmptyVector.tryWrap` and `NonEmptyVector.wrapOrThrow` behave similarly to `Vector.wrap` in that a copy of the underlying collection is never made.

#### <a name="non-empty-vector-converting">Converting an existing `Vector`</a>
A `Vector` has the methods `toNonEmpty` and `toNonEmptyOrThrow` that will attempt to convert the `Vector` to a `NonEmptyVector` at run-time.  They will do so without making copies of any underlying data structures.

#### <a name="non-empty-vector-of">Building using `Vector.of`</a>

`Vector.of` always returns a `ImmutableNonEmptyVector`, so all `Vector`s constructed this way are compatible with `NonEmptyVector`.

## <a name="immutable-vector">`ImmutableVector<A>`</a>

An `ImmutableVector<A>` is a `Vector<A>` with the additional guarantee that it is 100% safe from mutation.  In other words, no one else holds any references to its underlying collection.

## <a name="immutable-non-empty-vector">`ImmutableNonEmptyVector<A>`</a>

An `ImmutableNonEmptyVector<A>` is a `Vector<A>` that also has all the guarantees of `NonEmptyVector<A>` and `ImmutableVector<A>`.  An `ImmutableNonEmptyVector<A>` can be used anywhere a `Vector<A>` is called for. 

## <a name="set">`Set<A>`</a>

## <a name="non-empty-set">`NonEmptySet<A>`</a>

## <a name="immutable-set">`ImmutableSet<A>`</a>

## <a name="immutable-non-empty-set">`ImmutableNonEmptySet<A>`</a>

# <a name="examples">Examples</a>

##<a name="vector-examples">`Vector` examples</a>

### Basics

`Vector`s can wrap arrays or `java.util.List`s. 
They hold on to a reference of the data structure they wrap and never mutate it themselves, and are guaranteed to never make copies of the underlying data structure unless explicitly asked.

While a Vector can be mutated elsewhere (i.e. from someone else who holds a reference to the underlying collection), it can not be mutated by someone who only holds the `Vector` itself, so it is safe to share freely.

(Note: if you want complete protection from mutation _anywhere_, you can use an `ImmutableVector` instead, which will be explained later).

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

### Slices

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

### Mapping

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
### No copies are made

The following is to prove that none of the above methods made a copy of the underlying array.   (Don't do this!)
```Java
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
```
Again, mutation is discouraged;  the above was just to prove that no copy was made.
We will switch it back:
```Java
arr[3] = 4;
```
### `ImmutableVector<A>`
If you want to be sure you are complete protected from mutation, you can upgrade to an `ImmutableVector`:

```Java
ImmutableVector<Integer> vector7 = vector1.toImmutable();

System.out.println("vector7 = " + vector7);
    // *** vector7 = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```
Note that `toImmutable` may make a copy of the underlying structure, but only if it is necessary.  Calling `toImmutable` on a `Vector` that is already immutable is a no-op.

Now we will show that `ImmutableVector`s are safe from mutation:

```Java
 arr[3] = 1000;

// vector1 was affected:
System.out.println("vector1 = " + vector1);
    // *** vector1 = Vector(1, 2, 3, 1000, 5, 6, 7, 8, 9, 10)

// vector7 was not:
System.out.println("vector7 = " + vector7);
    // *** vector7 = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

```
An `ImmutableVector<A>` can be used anywhere a `Vector<A>` is called for, but it has the additional compile-time guarantee that it is protected against mutation anywhere (at the cost of a one-time copy).
        
The methods `tail`, `take`, `slice`, `drop` and `fmap` on an `ImmutableVector` all yield `ImmutableVector`s for free:

```Java
ImmutableVector<Integer> vector8 = vector7.tail();
ImmutableVector<Integer> vector9 = vector7.take(5);
ImmutableVector<Integer> vector10 = vector7.drop(2);
ImmutableVector<Integer> vector11 = vector7.slice(3, 7);
ImmutableVector<Integer> vector12 = vector7.fmap(n -> n * 100);
```
### Creating `ImmutableVector`s directly
You can construct  an `ImmutableVector` directly using `Vector.of`:
```Java
ImmutableVector<String> vector13 = Vector.of("foo", "bar", "baz");

System.out.println("vector13 = " + vector13);
    // *** vector13 = Vector(foo, bar, baz)
```
### `NonEmptyVector<A>`
`Vector.of` actually returns an instance of `ImmutableNonEmptyVector<A>`, which can be used where any `Vector<A>`, `ImmutableVector<A>`, or `NonEmptyVector<A>` is called for.

```Java
 ImmutableNonEmptyVector<String> vector14 = Vector.of("foo", "bar", "baz");
```
`NonEmpty` is another guarantee you can provide or require at compile-time.  `NonEmpty` provides a `head` method that is guaranteed to yield an element: 
```Java
System.out.println("vector14.head() = " + vector14.head());
    // *** vector14.head() = foo
```
All `NonEmptyVector<A>`s can be used anywhere a `Vector<A>` is called for, so it is a concept you can opt-in to caring about.  All the following are legal:
```Java
Vector<String> vector15 = vector14;
ImmutableVector<String> vector16 = vector14;
NonEmptyVector<String> vector17 = vector14;
```
However, if you _require_ a `NonEmptyVector`, you need to provide one at compile-time.
```Java
Vector<String> vector18 = Vector.empty();

// The following line won't compile!
NonEmptyVector<String> vector19 = vector18;
```
Nor will the following compile, since `Vector<String>` has not been proven non-empty at compile-time:
```Java
Vector<String> vector20 = Vector.wrap(asList("foo", "bar", "baz"));

// The following line won't compile!
NonEmptyVector<String> vector21 = vector20;
```
If you want to upgrade to a `NonEmptyVector` at run-time, use the `toNonEmpty` or `toNonEmptyOrThrow` methods on a `Vector`.
```Java
Maybe<NonEmptyVector<String>> vector22 = vector21.toNonEmpty();

System.out.println("vector22 = " + vector22);
    // *** vector22 = Just Vector(foo, bar, baz)

Maybe<NonEmptyVector<String>> vector23 = vector21.toNonEmpty();

System.out.println("vector23 = " + vector23);
    // *** vector23 = Nothing
```

# <a name="license">License</a>

**collection-views** is distributed under [The MIT License](http://choosealicense.com/licenses/mit/).

