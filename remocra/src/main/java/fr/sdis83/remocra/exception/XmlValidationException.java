package fr.sdis83.remocra.exception;

public class XmlValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    private int ligne;

    private int colonne;

    public XmlValidationException() {
        //
    }

    public XmlValidationException(String message, int ligne, int colonne) {
        super(message);
        this.ligne = ligne;
        this.colonne = colonne;
    }

    public XmlValidationException(String message, Throwable cause, int ligne, int colonne) {
        super(message, cause);
        this.ligne = ligne;
        this.colonne = colonne;
    }

    public String getMessageXMLError() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><error><message>Problème de validation XML \n" + this.getMessage() + "</message><ligne>" + this.ligne
                + "</ligne><colonne>" + this.colonne + "</colonne></error>";
    }

    public int getLigne() {
        return ligne;
    }

    public int getColonne() {
        return colonne;
    }

}
