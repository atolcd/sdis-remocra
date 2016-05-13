package fr.sdis83.remocra.exception;

public class BusinessException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public BusinessException() {
        // Rien
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
