package es.academia.campushub.util.exception;

/** Datos de entrada inválidos (HTTP 400). */
public class InvalidInputException extends RuntimeException {
  public InvalidInputException(String message) {
    super(message);
  }
}
