package fr.sdis83.remocra.web.exceptions;

import javax.ws.rs.core.Response;

public class ResponseException extends Exception {

  private Response.Status status;

  public ResponseException(Response.Status status, String msg) {
    super(msg);
    this.status = status;
  }

  public Integer getStatusCode() {
    return this.status.getStatusCode();
  }
}
