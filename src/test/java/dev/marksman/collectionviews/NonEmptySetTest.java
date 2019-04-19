package dev.marksman.collectionviews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class NonEmptySetTest {

    @Nested
    @DisplayName("wrap")
    class WrapTests {

        @Nested
        @DisplayName("wrap java.util.Set")
        class WrapJavaUtilSetTests {

            @Test
            void willNotDuplicateElements() {
                java.util.Set<String> underlying = new java.util.HashSet<String>(asList("foo", "bar"));
                NonEmptySet<String> subject = NonEmptySet.wrap("foo", underlying);
                assertEquals(2, subject.size());
            }

            @Nested
            @DisplayName("wrap size 3 java.util.Set")
            class WrapJavaUtilSetSize3Tests {
                private NonEmptySet<String> subject;
                private java.util.Set<String> underlying;

                @BeforeEach
                void beforeEach() {
                    underlying = new java.util.HashSet<>(asList("foo", "bar", "baz"));
                    subject = NonEmptySet.wrap("foo", underlying);
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
                void containsPositive() {
                    assertTrue(subject.contains("foo"));
                    assertTrue(subject.contains("bar"));
                    assertTrue(subject.contains("baz"));
                }

                @Test
                void containsNegative() {
                    assertFalse(subject.contains("qux"));
                    assertFalse(subject.contains("quux"));
                    assertFalse(subject.contains("qwerty"));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, containsInAnyOrder("foo", "bar", "baz"));
                }

                @Test
                void willNotMakeCopyOfUnderlying() {
                    underlying.add("don't do this!");
                    assertTrue(subject.contains("don't do this!"));
                    underlying.remove("foo");
                    assertFalse(subject.contains("foo"));
                }

            }

            @Nested
            @DisplayName("wrap size 1 java.util.Set")
            class WrapJavaUtilSetSize1Tests {
                private NonEmptySet<String> subject;

                @BeforeEach
                void beforeEach() {
                    subject = NonEmptySet.wrap("foo", Collections.emptySet());
                }

                @Test
                void notEmpty() {
                    assertFalse(subject.isEmpty());
                }

                @Test
                void sizeIs3() {
                    assertEquals(1, subject.size());
                }

                @Test
                void containsPositive() {
                    assertTrue(subject.contains("foo"));
                }

                @Test
                void containsNegative() {
                    assertFalse(subject.contains("qux"));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, containsInAnyOrder("foo"));
                }

            }
        }

        @Nested
        @DisplayName("wrap Set")
        class WrapSetTests {

            @Test
            void willNotDuplicateElements() {
                NonEmptySet<String> subject = NonEmptySet.wrap("foo", Set.of("foo", "foo", "bar"));
                assertEquals(2, subject.size());
            }

            @Nested
            @DisplayName("wrap size 3 Set")
            class WrapSetSize3Tests {
                private NonEmptySet<String> subject;

                @BeforeEach
                void beforeEach() {
                    subject = NonEmptySet.wrap("foo", Set.of("bar", "baz"));
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
                void containsPositive() {
                    assertTrue(subject.contains("foo"));
                    assertTrue(subject.contains("bar"));
                    assertTrue(subject.contains("baz"));
                }

                @Test
                void containsNegative() {
                    assertFalse(subject.contains("qux"));
                    assertFalse(subject.contains("quux"));
                    assertFalse(subject.contains("qwerty"));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, containsInAnyOrder("foo", "bar", "baz"));
                }
            }

            @Nested
            @DisplayName("wrap size 1 Set")
            class WrapSetSize1Tests {
                private NonEmptySet<String> subject;

                @BeforeEach
                void beforeEach() {
                    subject = NonEmptySet.wrap("foo", Set.empty());
                }

                @Test
                void notEmpty() {
                    assertFalse(subject.isEmpty());
                }

                @Test
                void sizeIs3() {
                    assertEquals(1, subject.size());
                }

                @Test
                void containsPositive() {
                    assertTrue(subject.contains("foo"));
                }

                @Test
                void containsNegative() {
                    assertFalse(subject.contains("qux"));
                }

                @Test
                void iteratesCorrectly() {
                    assertThat(subject, containsInAnyOrder("foo"));
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
            private NonEmptySet<String> subject;

            @BeforeEach
            void beforeEach() {
                subject = NonEmptySet.of("foo", "bar", "baz");
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
            void containsPositive() {
                assertTrue(subject.contains("foo"));
                assertTrue(subject.contains("bar"));
                assertTrue(subject.contains("baz"));
            }

            @Test
            void containsNegative() {
                assertFalse(subject.contains("qux"));
                assertFalse(subject.contains("quux"));
                assertFalse(subject.contains("qwerty"));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, containsInAnyOrder("foo", "bar", "baz"));
            }
        }

        @Nested
        @DisplayName("of size 1")
        class OfSize1Tests {
            private NonEmptySet<String> subject;

            @BeforeEach
            void beforeEach() {
                subject = NonEmptySet.of("foo");
            }

            @Test
            void notEmpty() {
                assertFalse(subject.isEmpty());
            }

            @Test
            void sizeIs3() {
                assertEquals(1, subject.size());
            }

            @Test
            void containsPositive() {
                assertTrue(subject.contains("foo"));
            }

            @Test
            void containsNegative() {
                assertFalse(subject.contains("bar"));
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, containsInAnyOrder("foo"));
            }
        }

    }

    @Nested
    @DisplayName("tryWrap")
    class TryWrapTests {

        @Test
        void javaUtilSetSuccess() {
            NonEmptySet<String> result = NonEmptySet.tryWrap(singleton("foo")).orElseThrow(AssertionError::new);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
            assertTrue(result.contains("foo"));
        }

        @Test
        void setSuccess() {
            NonEmptySet<String> result = NonEmptySet.tryWrap(Set.of("foo")).orElseThrow(AssertionError::new);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
            assertTrue(result.contains("foo"));
        }

        @Test
        void customSetSuccess() {
            // A Set that is not empty, but doesn't subtype NonEmptySet
            Set<String> customSet = new Set<String>() {
                @Override
                public int size() {
                    return 1;
                }

                @Override
                public boolean contains(String element) {
                    return element.equals("foo");
                }

                @Override
                public Iterator<String> iterator() {
                    return singleton("foo").iterator();
                }
            };

            NonEmptySet<String> result = NonEmptySet.tryWrap(customSet).orElseThrow(AssertionError::new);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
            assertTrue(result.contains("foo"));
        }

        @Test
        void javaUtilSetFailure() {
            assertEquals(nothing(), NonEmptySet.tryWrap(emptySet()));
        }

        @Test
        void setFailure() {
            assertEquals(nothing(), NonEmptySet.tryWrap(Set.empty()));
        }

        @Test
        void customSetFailure() {
            Set<String> customSet = new Set<String>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean contains(String element) {
                    return false;
                }

                @Override
                public Iterator<String> iterator() {
                    return emptyIterator();
                }
            };

            assertEquals(nothing(), NonEmptySet.tryWrap(customSet));
        }

    }

    @Nested
    @DisplayName("tryWrap")
    class WrapOrThrowTests {

        @Test
        void javaUtilSetSuccess() {
            NonEmptySet<String> result = NonEmptySet.wrapOrThrow(singleton("foo"));
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
            assertTrue(result.contains("foo"));
        }

        @Test
        void setSuccess() {
            NonEmptySet<String> result = NonEmptySet.wrapOrThrow(Set.of("foo"));
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
            assertTrue(result.contains("foo"));
        }

        @Test
        void customSetSuccess() {
            // A Set that is not empty, but doesn't subtype NonEmptySet
            Set<String> customSet = new Set<String>() {
                @Override
                public int size() {
                    return 1;
                }

                @Override
                public boolean contains(String element) {
                    return element.equals("foo");
                }

                @Override
                public Iterator<String> iterator() {
                    return singleton("foo").iterator();
                }
            };

            NonEmptySet<String> result = NonEmptySet.wrapOrThrow(customSet);
            assertThat(result, contains("foo"));
            assertEquals("foo", result.head());
            assertEquals(1, result.size());
            assertTrue(result.contains("foo"));
        }

        @Test
        void javaUtilSetFailure() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptySet.wrapOrThrow(emptySet()));
        }

        @Test
        void setFailure() {
            assertThrows(IllegalArgumentException.class, () -> NonEmptySet.wrapOrThrow(Set.empty()));
        }

        @Test
        void customSetFailure() {
            Set<String> customSet = new Set<String>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean contains(String element) {
                    return false;
                }

                @Override
                public Iterator<String> iterator() {
                    return emptyIterator();
                }
            };

            assertThrows(IllegalArgumentException.class, () -> NonEmptySet.wrapOrThrow(customSet));
        }

    }


}
