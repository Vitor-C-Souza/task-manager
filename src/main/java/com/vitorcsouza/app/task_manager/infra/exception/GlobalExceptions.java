package com.vitorcsouza.app.task_manager.infra.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptions {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<StandardError> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<ValidationError.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> new ValidationError.FieldError(f.getField(), f.getDefaultMessage()))
                .toList();

        ValidationError error = new ValidationError(
                Instant.now(),
                status.value(),
                "Erro de validação nos campos",
                "Um ou mais campos contêm erros de validação.", // Mensagem limpa e amigável
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> database(DatabaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                "Violação de integridade de dados",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> jsonError(HttpMessageNotReadableException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                "Requisição inválida",
                "O corpo da requisição JSON está malformatado ou ausente.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> typeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format("O parâmetro '%s' recebeu o valor '%s', que é inválido para o tipo esperado.",
                e.getName(), e.getValue());

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Parâmetro inválido na URL",
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> dataIntegrity(DataIntegrityViolationException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                "Violação de integridade no banco de dados",
                "Não foi possível realizar a operação devido a restrições de integridade dos dados.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardError> methodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                "Método HTTP não suportado",
                String.format("O método %s não é suportado para este endpoint.", e.getMethod()),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> genericException(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        StandardError error = new StandardError(
                Instant.now(),
                status.value(),
                "Erro interno do servidor",
                "Ocorreu um erro inesperado no sistema. Tente novamente mais tarde.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }
}
