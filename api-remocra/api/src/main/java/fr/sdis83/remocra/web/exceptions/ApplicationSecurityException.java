package fr.sdis83.remocra.web.exceptions;

public class ApplicationSecurityException extends RuntimeException {

/**
 *
 */
private static final long serialVersionUID = -4013660839658352601L;

public ApplicationSecurityException() {
}

public ApplicationSecurityException(String message) {
        super(message);
}

public ApplicationSecurityException(String message, Throwable cause) {
        super(message, cause);
}

public ApplicationSecurityException(Throwable cause) {
        super(cause);
}
}
