package dev.marksman.collectionviews;

import com.jnape.palatable.lambda.functions.Fn1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Replicate.replicate;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImmutableNonEmptyVectorTest {

    @Nested
    @DisplayName("fmap")
    class FmapTests {

        private ImmutableNonEmptyVector<Integer> subject;
        private Integer[] underlying;

        @BeforeEach
        void beforeEach() {
            underlying = new Integer[]{1, 2, 3};
            subject = Vector.wrap(underlying).toImmutable().toNonEmptyOrThrow();
        }


        @Test
        void throwsOnNullFunction() {
            assertThrows(NullPointerException.class, () -> subject.fmap(null));
        }

        @Test
        void fmap() {
            assertThat(subject.fmap(Object::toString), contains("1", "2", "3"));
        }

        @Test
        void functorIdentity() {
            assertEquals(subject, subject.fmap(id()));
        }

        @Test
        void functorComposition() {
            Fn1<Integer, Integer> f = n -> n * 2;
            Fn1<Integer, String> g = Object::toString;
            assertEquals(subject.fmap(f).fmap(g), subject.fmap(f.andThen(g)));
        }

        @Test
        void notAffectedByMutation() {
            underlying[0] = 10;
            assertThat(subject.fmap(n -> n * 2), contains(2, 4, 6));
        }

        @Test
        void stackSafe() {
            ImmutableNonEmptyVector<Integer> mapped = foldLeft((acc, __) -> acc.fmap(n -> n + 1),
                    subject, replicate(10_000, UNIT));
            assertThat(mapped, contains(10_001, 10_002, 10_003));
        }
    }

}
