package fr.sdis83.remocra.web.exceptions;

import org.springframework.http.HttpStatus;

public class ResponseException extends Exception {

  private HttpStatus status;

  public ResponseException(HttpStatus status, String msg) {
    super(msg);
    this.status = status;
  }

  public Integer getStatusCode() {
    return this.status.value();
  }

}
