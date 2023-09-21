package fr.sdis83.remocra.exception;

public class SQLBusinessException extends java.sql.SQLException {

  private static final long serialVersionUID = 1L;

  public SQLBusinessException(String reason) {
    super(reason);
  }

  public SQLBusinessException(String reason, Throwable cause) {
    super(reason, cause);
  }

  public SQLBusinessException(String reason, String sqlState) {
    super(reason, sqlState);
  }

  public SQLBusinessException(String reason, String sqlState, Throwable cause) {
    super(reason, sqlState, cause);
  }

  public String getMessageXMLError() {
    if (this.getSQLState().startsWith("08")) {
      return "<error><message>Problème de connexion à la base</message></error>";
    } else if (this.getSQLState().startsWith("23")) {
      // NOT NULL, UNIQUE,CONTRAINT ...
      return "<error><message>Problème de contrainte d'intégrité</message></error>";
    } else if (this.getSQLState().equals("42830")) {
      // Type de la clé étrangère n'est pas bon
      return "<error><message>Problème de clé étrangère</message></error>";
    } else if (this.getSQLState().equals("99")) {
      // quand on fait un getSingleResult qui doit obligatoirement
      // retourner un résultat
      return "<error><message>Un élément du référentiel n'a pas été trouvé à partir de son code</message></error>";
    }
    return "<error><message>Problème avec la base de données</message></error>";
  }
}
