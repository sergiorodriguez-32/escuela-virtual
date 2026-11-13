package es.academia.campushub.controller.rest;

import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.PersistenciaPendienteException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
import java.time.Instant;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduce las excepciones a respuestas HTTP (400/404/409). */
@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(PersistenciaPendienteException.class)
  public ResponseEntity<Map<String, Object>> pending(PersistenciaPendienteException ex) {
    return response(HttpStatus.NOT_IMPLEMENTED, "NOT_IMPLEMENTED", ex.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> integrity(DataIntegrityViolationException ex) {
    // No devolver SQL ni detalles internos al cliente.
    return response(HttpStatus.CONFLICT, "DATA_INTEGRITY", "Restricción de integridad incumplida");
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> malformed(HttpMessageNotReadableException ex) {
    return response(HttpStatus.BAD_REQUEST, "BAD_JSON", "El cuerpo JSON no es válido");
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> missing(ResourceNotFoundException ex) {
    return response(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
  }

  @ExceptionHandler(BusinessConflictException.class)
  public ResponseEntity<Map<String, Object>> conflict(BusinessConflictException ex) {
    return response(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage());
  }

  @ExceptionHandler(InvalidInputException.class)
  public ResponseEntity<Map<String, Object>> invalid(InvalidInputException ex) {
    return response(HttpStatus.BAD_REQUEST, "INVALID_INPUT", ex.getMessage());
  }

  private ResponseEntity<Map<String, Object>> response(
      HttpStatus status, String code, String message) {
    return ResponseEntity.status(status)
        .body(Map.of("code", code, "message", message, "timestamp", Instant.now().toString()));
  }
}
