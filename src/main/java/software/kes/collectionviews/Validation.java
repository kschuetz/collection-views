package software.kes.collectionviews;

import java.util.Objects;

final class Validation {

    private Validation() {

    }

    static void requirePositive(String paramName, int value) {
        if (value < 1) {
            throw new IllegalArgumentException(paramName + " must be >= 1");
        }
    }

    static void requireNonNegative(String paramName, int value) {
        if (value < 0) {
            throw new IllegalArgumentException(paramName + " must be >= 0");
        }
    }

    static void validateSlice(int startIndex, int endIndexExclusive) {
        requireNonNegative("startIndex", startIndex);
        requireNonNegative("endIndexExclusive", endIndexExclusive);
    }

    static <A> void validateSlice(int startIndex, int endIndexExclusive, A source) {
        validateSlice(startIndex, endIndexExclusive);
        Objects.requireNonNull(source);
    }

    static <A> void validateCopyFrom(int maxCount, A source) {
        Validation.requireNonNegative("maxCount", maxCount);
        Objects.requireNonNull(source);
    }

    static <A> void validateNonEmptyCopyFrom(int maxCount, A source) {
        Validation.requirePositive("maxCount", maxCount);
        Objects.requireNonNull(source);
    }

    static <A> void validateTake(int count, A source) {
        validateTake(count);
        Objects.requireNonNull(source);
    }

    static <A> void validateTake(int count) {
        Validation.requireNonNegative("count", count);
    }

    static <A> void validateDrop(int count, A source) {
        validateDrop(count);
        Objects.requireNonNull(source);
    }

    static <A> void validateDrop(int count) {
        Validation.requireNonNegative("count", count);
    }

    static void validateFill(int count) {
        Validation.requireNonNegative("count", count);
    }

    static void validateNonEmptyFill(int count) {
        Validation.requirePositive("count", count);
    }

}
