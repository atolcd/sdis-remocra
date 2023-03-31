package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.web.model.DestinataireModel;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.remocra.Tables.GESTIONNAIRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;
import static fr.sdis83.remocra.db.model.remocra.Tables.PROFIL_UTILISATEUR;


import static org.jooq.impl.DSL.lower;


import java.util.ArrayList;
import java.util.List;

@Configuration
public class DestinataireRepository {

  @Autowired
  DSLContext context;

  DestinataireRepository(DSLContext context){
    this.context = context;
  }

  public DestinataireRepository(){

  }

  @Bean
  public  DestinataireRepository destinataireRepository(DSLContext context){
    return new DestinataireRepository(context);
  }

  public List<Integer> getAllIdOrganismes(){
    Result<Record1<Integer>> result = context.select(ORGANISME.ID.cast(SQLDataType.INTEGER)).from(ORGANISME).fetch();
    List<Integer> destinataires = new ArrayList<Integer>();
    for (Record r : result){
      destinataires.add((Integer)r.getValue(0));
    }
    return destinataires;
  }

  /**
   * @param idOrganismes Identifiants d'organismes
   * @param filtre Valeur de recherche
   * @param strict Boolean si vrai : recherche strict, sinon : recherche la dans les résultats potentiels
   * @return liste de destinataires provenant de la table UTILISATEUR
   */
  public List<DestinataireModel> getDestinataireUtilisateur(List<Integer> idOrganismes, String filtre, Boolean strict) {
    List<DestinataireModel> destinataires = context.select(UTILISATEUR.ID, DSL.concat(UTILISATEUR.NOM, DSL.val(" "), UTILISATEUR.PRENOM).as("nom"), UTILISATEUR.EMAIL, PROFIL_UTILISATEUR.NOM.as("fonction"), DSL.val("Utilisateur").as("type"))
      .from(UTILISATEUR)
      .join(PROFIL_UTILISATEUR).on(UTILISATEUR.PROFIL_UTILISATEUR.eq(PROFIL_UTILISATEUR.ID))
      .where(UTILISATEUR.ORGANISME.in(idOrganismes)
      .and(UTILISATEUR.MESSAGE_REMOCRA)
      .and(UTILISATEUR.ACTIF.isTrue())
      .and((lower(UTILISATEUR.NOM).like(setFiltre(filtre, strict)))
          .or(lower(UTILISATEUR.PRENOM).like(setFiltre(filtre, strict)))
          .or(lower(UTILISATEUR.EMAIL).like(setFiltre(filtre, strict)))
      )).fetchInto(DestinataireModel.class);
    return destinataires;

  }

  /**
   * @param idOrganismes Identifiants d'organismes
   * @param filtre Valeur de recherche
   * @param strict Boolean si vrai : recherche strict, sinon : recherche la dans les résultats potentiels
   * @return liste de destinataires provenant de la table ORGANISME
   */
  public List<DestinataireModel> getDestinataireOrganisme(List<Integer> idOrganismes, String filtre, Boolean strict){
    List<DestinataireModel> destinataires = context.select(ORGANISME.ID, ORGANISME.NOM, ORGANISME.EMAIL_CONTACT.as("email"), DSL.val("Organisme").as("type")).from(ORGANISME)
    .where(ORGANISME.ID.in(idOrganismes))
    .and(ORGANISME.NOM.likeIgnoreCase(setFiltre(filtre, strict))
      .or(ORGANISME.EMAIL_CONTACT.likeIgnoreCase(setFiltre(filtre, strict))))
    .and(ORGANISME.EMAIL_CONTACT.isNotNull())
    .and(ORGANISME.ACTIF.isTrue()).fetchInto(DestinataireModel.class);
    return destinataires;
  }

  /**
   * @param filtre Valeur de recherche
   * @param strict Boolean si vrai : recherche strict, sinon : recherche la dans les résultats potentiel
   * @return liste de destinataires provenant de la table ORGANISME
   */
  public List<DestinataireModel> getDestinataireContactGestionnaire(String filtre, Boolean strict){
    List<DestinataireModel> destinataires = context.select(CONTACT.ID, DSL.concat(CONTACT.NOM, DSL.val(" "), CONTACT.PRENOM).as("nom"), CONTACT.EMAIL, CONTACT.FONCTION.as("function"), DSL.val("Contact").as("type"))
      .from(CONTACT).where(CONTACT.APPARTENANCE.eq("GESTIONNAIRE"))
      .and(CONTACT.NOM.like(setFiltre(filtre, strict)))
        .or(CONTACT.PRENOM.like(setFiltre(filtre, strict)))
          .fetchInto(DestinataireModel.class);
    return destinataires;
  }

  /**
   * @param idOrganismes Identifiants d'organismes
   * @return liste de destinataires provenant de la table CONTACT
   */
  public List<DestinataireModel> getDestinataireContactOrganisme(List<Integer> idOrganismes, String filtre, Boolean strict){
    List<DestinataireModel> destinataires = context.select(CONTACT.ID, DSL.concat(CONTACT.NOM, DSL.val(" "), CONTACT.PRENOM).as("nom"), CONTACT.EMAIL, CONTACT.FONCTION.as("function"), DSL.val("Contact").as("type"))
      .from(CONTACT).where(CONTACT.ID_APPARTENANCE.in(idOrganismes)
      .and(CONTACT.APPARTENANCE.eq("ORGANISME"))
      .and((lower(CONTACT.NOM).like(setFiltre(filtre, strict)))
        .or(lower(CONTACT.PRENOM).like(setFiltre(filtre, strict)))))
          .fetchInto(DestinataireModel.class);
    return destinataires;
  }

  private static String setFiltre(String filtre, Boolean strict) {
    if(strict) { return filtre.toLowerCase() + "%"; }
    else { return "%" + filtre.toLowerCase() + "%"; }
  }
}
