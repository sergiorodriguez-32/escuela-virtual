package es.academia.campushub.util.exception;

/** Persistencia aún sin implementar. */
public class PersistenciaPendienteException extends RuntimeException {
  public PersistenciaPendienteException(String operacion) {
    super("Falta implementar persistencia: " + operacion);
  }
}
