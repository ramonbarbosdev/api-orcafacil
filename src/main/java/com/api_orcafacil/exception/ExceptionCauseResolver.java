package com.api_orcafacil.exception;

import java.util.Optional;

public final class ExceptionCauseResolver {

    private ExceptionCauseResolver() {
    }

    public static Throwable rootCause(Throwable ex) {
        if (ex == null) {
            return null;
        }
        Throwable current = ex;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    public static <T extends Throwable> Optional<T> findCause(Throwable ex, Class<T> type) {
        Throwable current = ex;
        while (current != null) {
            if (type.isInstance(current)) {
                return Optional.of(type.cast(current));
            }
            current = current.getCause();
        }
        return Optional.empty();
    }
}
