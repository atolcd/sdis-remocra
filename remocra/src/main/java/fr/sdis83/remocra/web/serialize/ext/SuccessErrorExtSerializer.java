package fr.sdis83.remocra.web.serialize.ext;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.json.JsonObjectResponse;

public final class SuccessErrorExtSerializer {

    public final static String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private HttpStatus errorStatus;
    private String successErrorMessage;
    private boolean success;
    private String contentType = null;

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public SuccessErrorExtSerializer(boolean success, String successErrorMessage) {
        this(success, successErrorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public SuccessErrorExtSerializer(boolean success, String successErrorMessage, String contentType) {
        this(success, successErrorMessage);
        this.contentType = contentType;
    }

    public SuccessErrorExtSerializer(boolean success, String successErrorMessage, HttpStatus errorStatus) {
        this.errorStatus = errorStatus;
        this.successErrorMessage = successErrorMessage;
        this.success = success;
    }

    public SuccessErrorExtSerializer(boolean success, String successErrorMessage, HttpStatus errorStatus, String contentType) {
        this(success, successErrorMessage, errorStatus);
        this.contentType = contentType;
    }

    public ResponseEntity<String> serialize() {
        HttpStatus returnStatus = success ? HttpStatus.OK : this.errorStatus;
        JsonObjectResponse response = new JsonObjectResponse();
        response.setMessage(successErrorMessage);
        response.setSuccess(success);
        response.setTotal(success ? 1 : 0);

        HttpHeaders responseHeaders = new HttpHeaders();
        // FIXME Pour que l'encodage soit pris correctement (application/json ne
        // passe pas)...
        responseHeaders.add("Content-Type", this.contentType == null ? DEFAULT_CONTENT_TYPE : this.contentType);
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").exclude("*.password").exclude("*.salt")
                .serialize(response), responseHeaders, returnStatus);
    }
}
