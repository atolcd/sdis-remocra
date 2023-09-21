package fr.sdis83.remocra.exception;

public class XmlDroitException extends Exception {

  private static final long serialVersionUID = 1L;

  public XmlDroitException() {
    //
  }

  public XmlDroitException(String message) {
    super(message, null);
  }

  public XmlDroitException(String message, Throwable cause) {
    super(message, cause);
  }

  public String getMessageXMLError() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><error><message>Problème de droit : \n"
        + this.getMessage()
        + "</message></error>";
  }
}
