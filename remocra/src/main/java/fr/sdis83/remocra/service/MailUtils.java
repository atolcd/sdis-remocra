package fr.sdis83.remocra.service;

import fr.sdis83.remocra.domain.remocra.Email;
import fr.sdis83.remocra.domain.remocra.EmailModele;
import fr.sdis83.remocra.domain.remocra.EmailModele.EmailModeleKeyMap;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailUtils {

  // Via utilisateur
  public void envoiEmailWithModele(
      EmailModele modele,
      Utilisateur expediteur,
      Utilisateur destinataire,
      EmailModeleKeyMap args) {
    envoiEmail(
        expediteur, destinataire, modele.getObjetReplaced(args), modele.getCorpsReplaced(args));
  }

  public void envoiEmail(
      Utilisateur expediteur, Utilisateur destinataire, String objet, String corps) {
    Email email = getEmailExceptDest(expediteur, objet, corps);
    email.setDestinataire(getPrettyDblString(destinataire.getPrenom(), destinataire.getNom()));
    email.setDestinataireEmail(destinataire.getEmail());
    email.merge();
  }

  // Via adresse email (pas par utilisateur applicatif)
  public void envoiEmailWithModele(
      EmailModele modele, Utilisateur expediteur, String destinataire, EmailModeleKeyMap args) {
    envoiEmail(
        expediteur, destinataire, modele.getObjetReplaced(args), modele.getCorpsReplaced(args));
  }

  public void envoiEmail(Utilisateur expediteur, String destinataire, String objet, String corps) {
    Email email = getEmailExceptDest(expediteur, objet, corps);
    email.setDestinataire(destinataire);
    email.setDestinataireEmail(destinataire);
    email.merge();
  }

  // Utilitaires
  protected Email getEmailExceptDest(Utilisateur expediteur, String objet, String corps) {
    Email email = new Email();
    email.setExpediteur(getPrettyDblString(expediteur.getPrenom(), expediteur.getNom()));
    email.setExpediteurEmail(expediteur.getEmail());
    email.setCorps(corps);
    email.setObjet(objet);
    return email;
  }

  protected static String getPrettyDblString(String st1, String st2) {
    return (st1 == null ? "" : st1)
        + (st1 != null && st2 != null ? " " : "")
        + (st2 == null ? "" : st2);
  }
}
