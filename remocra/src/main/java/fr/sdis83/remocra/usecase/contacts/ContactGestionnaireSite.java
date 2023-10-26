package fr.sdis83.remocra.usecase.contacts;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Contact;

/** Est utilisé pour l'affichage des contacts d'un gestionnaire */
public class ContactGestionnaireSite {
  private Contact contact;
  private String siteContactNom;

  public ContactGestionnaireSite() {}

  public ContactGestionnaireSite(Contact contact, String siteContact) {
    this.contact = contact;
    this.siteContactNom = siteContact;
  }

  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
  }

  public String getSiteContactNom() {
    return siteContactNom;
  }

  public void setSiteContactNom(String siteContact) {
    this.siteContactNom = siteContact;
  }
}
