package fr.sdis83.remocra.exception;

public class AnomalieException extends Exception {

    private static final long serialVersionUID = 1L;

    public AnomalieException() {
        // TODO Auto-generated constructor stub
    }

    public AnomalieException(String message) {
        super(message);
    }

    public AnomalieException(String message, Throwable cause) {
        super(message, cause);
    }
}
