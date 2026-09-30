package com.vitorcsouza.app.task_manager.infra.exception;

import java.time.Instant;
import java.util.List;

public record ValidationError(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path,
        List<FieldError> errors
) {
    public record FieldError(String field, String message) {}
}
