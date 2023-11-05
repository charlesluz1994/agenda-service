package cluz.com.agenda.exception;

public class DataIntegrityViolationException extends RuntimeException {
    public DataIntegrityViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataIntegrityViolationException(String message) {
        super(message);
    }
}