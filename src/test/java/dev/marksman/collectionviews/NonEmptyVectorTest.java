package dev.marksman.collectionviews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class NonEmptyVectorTest {

    @Nested
    @DisplayName("wrap")
    class WrapTests {

        @Nested
        @DisplayName("wrap array")
        class WrapArrayTests {

            @Nested
            @DisplayName("wrap size 3 array")
            class WrapArray3Tests {
                private NonEmptyVector<String> subject;
                String[] underlying;

                @BeforeEach
                void beforeEach() {
                    underlying = new String[]{"bar", "baz"};
                    subject = NonEmptyVector.wrap("foo", underlying);
                }

                @Test
                void willNotMakeCopy() {
                    assertThat(subject, contains("foo", "bar", "baz"));
                    underlying[1] = "quux";
                    assertThat(subject, contains("foo", "bar", "quux"));
                }

                @Test
                void notEmpty() {
                    assertFalse(subject.isEmpty());
                }

                @Test
                void sizeIs3() {
                    assertEquals(3, subject.size());
                }

                @Test
                void head() {
                    assertEquals("foo", subject.head());
                }

                @Test
                void getForValidIndices() {
                    assertEquals(just("foo"), subject.get(0));
                    assertEquals(just("bar"), subject.get(1));
                    assertEquals(just("baz"), subject.get(2));
                }

                @Test
                void getForInvalidIndices() {
                    assertEquals(nothing(), subject.get(3));
                    assertEquals(nothing(), subject.get(-1));
                }

                @Test
                void unsafeGetForValidIndices() {
                    assertEquals("foo", subject.unsafeGet(0));
                    assertEquals("bar", subject.unsafeGet(1));
                    assertEquals("baz", subject.unsafeGet(2));
                }

                @Test
                void unsafeGetThrowsForInvalidIndices() {
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo", "bar", "baz"));
                }

                @Test
                void tailIteratesCorrectly() {
                    assertThat(subject.tail(), contains("bar", "baz"));
                }
            }

            @Nested
            @DisplayName("wrap size 1 array")
            class WrapSingletonArrayTests {
                private NonEmptyVector<String> subject;

                @BeforeEach
                void beforeEach() {
                    subject = NonEmptyVector.wrap("foo", new String[]{});
                }

                @Test
                void notEmpty() {
                    assertFalse(subject.isEmpty());
                }

                @Test
                void sizeIs1() {
                    assertEquals(1, subject.size());
                }

                @Test
                void head() {
                    assertEquals("foo", subject.head());
                }

                @Test
                void getForValidIndices() {
                    assertEquals(just("foo"), subject.get(0));
                }

                @Test
                void getForInvalidIndices() {
                    assertEquals(nothing(), subject.get(1));
                    assertEquals(nothing(), subject.get(-1));
                }

                @Test
                void unsafeGetForValidIndices() {
                    assertEquals("foo", subject.unsafeGet(0));
                }

                @Test
                void unsafeGetThrowsForInvalidIndices() {
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

                @Test
                void tailIteratesCorrectly() {
                    assertThat(subject.tail(), emptyIterable());
                }
            }

        }

        @Nested
        @DisplayName("wrap List")
        class WrapListTests {

            @Nested
            @DisplayName("wrap size 3 List")
            class WrapList3Tests {
                private NonEmptyVector<String> subject;
                List<String> underlying;

                @BeforeEach
                void beforeEach() {
                    underlying = asList("bar", "baz");
                    subject = NonEmptyVector.wrap("foo", underlying);
                }

                @Test
                void willNotMakeCopy() {
                    assertThat(subject, contains("foo", "bar", "baz"));
                    underlying.set(1, "quux");
                    assertThat(subject, contains("foo", "bar", "quux"));
                }

                @Test
                void notEmpty() {
                    assertFalse(subject.isEmpty());
                }

                @Test
                void sizeIs3() {
                    assertEquals(3, subject.size());
                }

                @Test
                void head() {
                    assertEquals("foo", subject.head());
                }

                @Test
                void getForValidIndices() {
                    assertEquals(just("foo"), subject.get(0));
                    assertEquals(just("bar"), subject.get(1));
                    assertEquals(just("baz"), subject.get(2));
                }

                @Test
                void getForInvalidIndices() {
                    assertEquals(nothing(), subject.get(3));
                    assertEquals(nothing(), subject.get(-1));
                }

                @Test
                void unsafeGetForValidIndices() {
                    assertEquals("foo", subject.unsafeGet(0));
                    assertEquals("bar", subject.unsafeGet(1));
                    assertEquals("baz", subject.unsafeGet(2));
                }

                @Test
                void unsafeGetThrowsForInvalidIndices() {
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo", "bar", "baz"));
                }

                @Test
                void tailIteratesCorrectly() {
                    assertThat(subject.tail(), contains("bar", "baz"));
                }
            }

            @Nested
            @DisplayName("wrap size 1 List")
            class WrapSingletonListTests {
                private NonEmptyVector<String> subject;

                @BeforeEach
                void beforeEach() {
                    subject = NonEmptyVector.wrap("foo", emptyList());
                }

                @Test
                void notEmpty() {
                    assertFalse(subject.isEmpty());
                }

                @Test
                void sizeIs1() {
                    assertEquals(1, subject.size());
                }

                @Test
                void head() {
                    assertEquals("foo", subject.head());
                }

                @Test
                void getForValidIndices() {
                    assertEquals(just("foo"), subject.get(0));
                }

                @Test
                void getForInvalidIndices() {
                    assertEquals(nothing(), subject.get(1));
                    assertEquals(nothing(), subject.get(-1));
                }

                @Test
                void unsafeGetForValidIndices() {
                    assertEquals("foo", subject.unsafeGet(0));
                }

                @Test
                void unsafeGetThrowsForInvalidIndices() {
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

                @Test
                void tailIteratesCorrectly() {
                    assertThat(subject.tail(), emptyIterable());
                }
            }

        }

        @Nested
        @DisplayName("wrap Vector")
        class WrapVectorTests {

            @Nested
            @DisplayName("wrap size 3 Vector")
            class WrapVector3Tests {
                private NonEmptyVector<String> subject;

                @BeforeEach
                void beforeEach() {
                    subject = NonEmptyVector.wrap("foo", Vector.of("bar", "baz"));
                }

                @Test
                void notEmpty() {
                    assertFalse(subject.isEmpty());
                }

                @Test
                void sizeIs3() {
                    assertEquals(3, subject.size());
                }

                @Test
                void head() {
                    assertEquals("foo", subject.head());
                }

                @Test
                void getForValidIndices() {
                    assertEquals(just("foo"), subject.get(0));
                    assertEquals(just("bar"), subject.get(1));
                    assertEquals(just("baz"), subject.get(2));
                }

                @Test
                void getForInvalidIndices() {
                    assertEquals(nothing(), subject.get(3));
                    assertEquals(nothing(), subject.get(-1));
                }

                @Test
                void unsafeGetForValidIndices() {
                    assertEquals("foo", subject.unsafeGet(0));
                    assertEquals("bar", subject.unsafeGet(1));
                    assertEquals("baz", subject.unsafeGet(2));
                }

                @Test
                void unsafeGetThrowsForInvalidIndices() {
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo", "bar", "baz"));
                }

                @Test
                void tailIteratesCorrectly() {
                    assertThat(subject.tail(), contains("bar", "baz"));
                }
            }

            @Nested
            @DisplayName("wrap size 1 Vector")
            class WrapSingletonVectorTests {
                private NonEmptyVector<String> subject;

                @BeforeEach
                void beforeEach() {
                    subject = NonEmptyVector.wrap("foo", Vector.empty());
                }

                @Test
                void notEmpty() {
                    assertFalse(subject.isEmpty());
                }

                @Test
                void sizeIs1() {
                    assertEquals(1, subject.size());
                }

                @Test
                void head() {
                    assertEquals("foo", subject.head());
                }

                @Test
                void getForValidIndices() {
                    assertEquals(just("foo"), subject.get(0));
                }

                @Test
                void getForInvalidIndices() {
                    assertEquals(nothing(), subject.get(1));
                    assertEquals(nothing(), subject.get(-1));
                }

                @Test
                void unsafeGetForValidIndices() {
                    assertEquals("foo", subject.unsafeGet(0));
                }

                @Test
                void unsafeGetThrowsForInvalidIndices() {
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
                    assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, contains("foo"));
                }

                @Test
                void tailIteratesCorrectly() {
                    assertThat(subject.tail(), emptyIterable());
                }
            }

        }

    }

    @Nested
    @DisplayName("'of' constructor")
    class ConstructorTests {
        @Nested
        @DisplayName("of size 3")
        class OfSize3Tests {
            private NonEmptyVector<String> subject;

            @BeforeEach
            void beforeEach() {
                subject = NonEmptyVector.of("foo", "bar", "baz");
            }

            @Test
            void notEmpty() {
                assertFalse(subject.isEmpty());
            }

            @Test
            void sizeIs3() {
                assertEquals(3, subject.size());
            }

            @Test
            void head() {
                assertEquals("foo", subject.head());
            }

            @Test
            void getForValidIndices() {
                assertEquals(just("foo"), subject.get(0));
                assertEquals(just("bar"), subject.get(1));
                assertEquals(just("baz"), subject.get(2));
            }

            @Test
            void getForInvalidIndices() {
                assertEquals(nothing(), subject.get(3));
                assertEquals(nothing(), subject.get(-1));
            }

            @Test
            void unsafeGetForValidIndices() {
                assertEquals("foo", subject.unsafeGet(0));
                assertEquals("bar", subject.unsafeGet(1));
                assertEquals("baz", subject.unsafeGet(2));
            }

            @Test
            void unsafeGetThrowsForInvalidIndices() {
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(3));
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, contains("foo", "bar", "baz"));
            }

            @Test
            void tailIteratesCorrectly() {
                assertThat(subject.tail(), contains("bar", "baz"));
            }
        }

        @Nested
        @DisplayName("of size 1")
        class SingletonVectorTests {
            private NonEmptyVector<String> subject;

            @BeforeEach
            void beforeEach() {
                subject = NonEmptyVector.of("foo");
            }

            @Test
            void notEmpty() {
                assertFalse(subject.isEmpty());
            }

            @Test
            void sizeIs1() {
                assertEquals(1, subject.size());
            }

            @Test
            void head() {
                assertEquals("foo", subject.head());
            }

            @Test
            void getForValidIndices() {
                assertEquals(just("foo"), subject.get(0));
            }

            @Test
            void getForInvalidIndices() {
                assertEquals(nothing(), subject.get(1));
                assertEquals(nothing(), subject.get(-1));
            }

            @Test
            void unsafeGetForValidIndices() {
                assertEquals("foo", subject.unsafeGet(0));
            }

            @Test
            void unsafeGetThrowsForInvalidIndices() {
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(1));
                assertThrows(IndexOutOfBoundsException.class, () -> subject.unsafeGet(-1));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, contains("foo"));
            }

            @Test
            void tailIteratesCorrectly() {
                assertThat(subject.tail(), emptyIterable());
            }
        }
    }

    @Nested
    @DisplayName("tryWrap")
    class TryWrapTests {
        @Test
        void arraySuccess() {
            NonEmptyVector<String> result = NonEmptyVector.tryWrap(new String[]{"foo"}).orElseThrow(AssertionError::new);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void listSuccess() {
            NonEmptyVector<String> result = NonEmptyVector.tryWrap(singletonList("foo")).orElseThrow(AssertionError::new);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void vectorSuccess() {
            NonEmptyVector<String> result = NonEmptyVector.tryWrap(Vector.of("foo")).orElseThrow(AssertionError::new);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void customVectorSuccess() {
            // Vector is not empty, but doesn't subtype NonEmptyVector
            Vector<String> underlying = new Vector<String>() {
                @Override
                public int size() {
                    return 1;
                }

                @Override
                public String unsafeGet(int index) {
                    return "foo";
                }
            };

            NonEmptyVector<String> result = NonEmptyVector.tryWrap(underlying).orElseThrow(AssertionError::new);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void arrayFailure() {
            assertEquals(nothing(), NonEmptyVector.tryWrap(new String[]{}));
        }

        @Test
        void listFailure() {
            assertEquals(nothing(), NonEmptyVector.tryWrap(emptyList()));
        }

        @Test
        void vectorFailure() {
            assertEquals(nothing(), NonEmptyVector.tryWrap(Vector.empty()));
        }

        @Test
        void customVectorFailure() {
            Vector<String> underlying = new Vector<String>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public String unsafeGet(int index) {
                    throw new IndexOutOfBoundsException();
                }
            };

            assertEquals(nothing(), NonEmptyVector.tryWrap(underlying));
        }
    }

    @Nested
    @DisplayName("wrapOrThrow")
    class WrapOrThrowTests {
        @Test
        void arraySuccess() {
            NonEmptyVector<String> result = NonEmptyVector.wrapOrThrow(new String[]{"foo"});
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void listSuccess() {
            NonEmptyVector<String> result = NonEmptyVector.wrapOrThrow(singletonList("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void vectorSuccess() {
            NonEmptyVector<String> result = NonEmptyVector.wrapOrThrow(Vector.of("foo"));
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void customVectorSuccess() {
            // Vector is not empty, but doesn't subtype NonEmptyVector
            Vector<String> underlying = new Vector<String>() {
                @Override
                public int size() {
                    return 1;
                }

                @Override
                public String unsafeGet(int index) {
                    return "foo";
                }
            };

            NonEmptyVector<String> result = NonEmptyVector.wrapOrThrow(underlying);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
        }

        @Test
        void arrayFailure() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.wrapOrThrow(new String[]{}));
        }

        @Test
        void listFailure() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.wrapOrThrow(emptyList()));
        }

        @Test
        void vectorFailure() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.wrapOrThrow(Vector.empty()));
        }

        @Test
        void customVectorFailure() {
            Vector<String> underlying = new Vector<String>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public String unsafeGet(int index) {
                    throw new IndexOutOfBoundsException();
                }
            };

            assertThrows(IllegalArgumentException.class, () -> NonEmptyVector.wrapOrThrow(underlying));
        }
    }

}
