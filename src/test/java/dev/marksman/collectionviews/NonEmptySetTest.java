package dev.marksman.collectionviews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;

class NonEmptySetTest {

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
