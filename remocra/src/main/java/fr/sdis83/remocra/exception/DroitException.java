package fr.sdis83.remocra.exception;

public class DroitException extends Exception {

    private static final long serialVersionUID = 1L;

    public DroitException() {
        //
    }

    public DroitException(String message) {
        super(message);
    }

    public DroitException(String message, Throwable cause) {
        super(message, cause);
    }
}
