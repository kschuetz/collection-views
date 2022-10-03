package software.kes.collectionviews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NonEmptySetAdapterTest {

    class CustomSet implements Set<String> {

        CustomSet(String element) {
            this.element = element;
        }

        String element;

        @Override
        public boolean contains(String element) {
            return Objects.equals(this.element, element);
        }

        @Override
        public int hashCode() {
            return SetHelpers.setHashCode(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Set<?>) {
                return SetHelpers.setEquals(this, (Set<?>) obj);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return SetHelpers.setToString(this);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public Iterator<String> iterator() {
            return singleton(element).iterator();
        }

    }

    class ImmutableCustomSet extends CustomSet implements ImmutableSet<String> {

        ImmutableCustomSet(String element) {
            super(element);
        }

    }

    @Nested
    @DisplayName("convert to non-empty")
    class ConvertToNonEmpty {

        private CustomSet original;

        @BeforeEach
        void setUp() {
            original = new CustomSet("foo");
        }

        @Test
        void toNonEmptySucceeds() {
            assertEquals(just(Set.of("foo")), original.toNonEmpty());
        }

        @Test
        void toNonEmptyOrThrowSucceeds() {
            assertEquals(Set.of("foo"), original.toNonEmptyOrThrow());
        }

        @Nested
        @DisplayName("converted")
        class Converted {

            private NonEmptySet<String> subject;

            @BeforeEach
            void setUp() {
                subject = original.toNonEmptyOrThrow();
            }

            @Test
            void correctSize() {
                assertEquals(1, subject.size());
            }

            @Test
            void correctHead() {
                assertEquals("foo", subject.head());
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, containsInAnyOrder("foo"));
            }

            @Test
            void tailIteratesCorrectly() {
                assertThat(subject.tail(), emptyIterable());
            }

            @Test
            void equalToItself() {
                assertEquals(subject, subject);
            }

            @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
            @Test
            void equalToOriginal() {
                assertEquals(subject, original);
                assertEquals(original, subject);
            }

            @Test
            void equalToImmutable() {
                assertEquals(subject, subject.toImmutable());
                assertEquals(subject.toImmutable(), subject);
            }

        }

    }

    @Nested
    @DisplayName("immutable convert to non-empty")
    class ImmutableConvertToNonEmpty {

        private ImmutableCustomSet original;

        @BeforeEach
        void setUp() {
            original = new ImmutableCustomSet("foo");
        }

        @Test
        void toNonEmptySucceeds() {
            assertEquals(just(Set.of("foo")), original.toNonEmpty());
        }

        @Test
        void toNonEmptyOrThrowSucceeds() {
            assertEquals(Set.of("foo"), original.toNonEmptyOrThrow());
        }

        @Nested
        @DisplayName("converted")
        class Converted {

            private NonEmptySet<String> subject;

            @BeforeEach
            void setUp() {
                subject = original.toNonEmptyOrThrow();
            }

            @Test
            void correctSize() {
                assertEquals(1, subject.size());
            }

            @Test
            void correctHead() {
                assertEquals("foo", subject.head());
            }

            @Test
            void iteratesCorrectly() {
                assertThat(subject, containsInAnyOrder("foo"));
            }

            @Test
            void tailIteratesCorrectly() {
                assertThat(subject.tail(), emptyIterable());
            }

            @Test
            void equalToItself() {
                assertEquals(subject, subject);
            }

            @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
            @Test
            void equalToOriginal() {
                assertEquals(subject, original);
                assertEquals(original, subject);
            }

            @Test
            void equalToImmutable() {
                assertEquals(subject, subject.toImmutable());
                assertEquals(subject.toImmutable(), subject);
            }

        }

    }

}
