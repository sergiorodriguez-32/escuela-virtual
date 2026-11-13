package es.academia.campushub.util.exception;

/** Conflicto de negocio (HTTP 409). */
public class BusinessConflictException extends RuntimeException {
  public BusinessConflictException(String message) {
    super(message);
  }
}
