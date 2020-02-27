package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.web.model.DestinataireModel;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
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
   * @return liste de destinataires provenant de la table UTILISATEUR
   */
  public List<DestinataireModel> getDestinataireUtilisateur(List<Integer> idOrganismes, String filtre) {
    List<DestinataireModel> destinataires = context.select(UTILISATEUR.ID, DSL.concat(DSL.concat(UTILISATEUR.NOM, " "), UTILISATEUR.PRENOM).as("nom"), UTILISATEUR.EMAIL, PROFIL_UTILISATEUR.NOM.as("fonction"), DSL.val("Utilisateur").as("type"))
      .from(UTILISATEUR)
      .join(PROFIL_UTILISATEUR).on(UTILISATEUR.PROFIL_UTILISATEUR.eq(PROFIL_UTILISATEUR.ID))
      .where(UTILISATEUR.ORGANISME.in(idOrganismes)
      .and(UTILISATEUR.MESSAGE_REMOCRA)
      .and((lower(UTILISATEUR.NOM).like("%"+filtre.toLowerCase()+"%"))
          .or(lower(UTILISATEUR.PRENOM).like("%"+filtre.toLowerCase()+"%"))
          .or(lower(UTILISATEUR.EMAIL).like("%"+filtre.toLowerCase()+"%"))
      )).fetchInto(DestinataireModel.class);
    return destinataires;

  }

  /**
   * @param idOrganismes Identifiants d'organismes
   * @return liste de destinataires provenant de la table ORGANISME
   */
  public List<DestinataireModel> getDestinataireOrganisme(List<Integer> idOrganismes, String filtre){
    List<DestinataireModel> destinataires = context.select(ORGANISME.ID, ORGANISME.NOM, ORGANISME.EMAIL_CONTACT.as("email"), DSL.val("Organisme").as("type")).from(ORGANISME)
    .where(ORGANISME.ID.in(idOrganismes))
    .and(lower(ORGANISME.NOM).like("%"+filtre.toLowerCase()+"%")
      .or(lower(ORGANISME.EMAIL_CONTACT).like("%"+filtre.toLowerCase()+"%")))
    .and(ORGANISME.EMAIL_CONTACT.isNotNull()).fetchInto(DestinataireModel.class);
    return destinataires;
  }

  /**
   * @param idOrganismes Identifiants d'organismes
   * @return liste de destinataires provenant de la table CONTACT
   */
  public List<DestinataireModel> getDestinataireContact(List<Integer> idOrganismes, String filtre){
    List<DestinataireModel> destinataires = context.select(CONTACT.ID, DSL.concat(DSL.concat(CONTACT.NOM, " "), CONTACT.PRENOM).as("nom"), CONTACT.EMAIL, CONTACT.FONCTION.as("function"), DSL.val("CONTACT").as("type"))
      .from(CONTACT).where(CONTACT.ID_APPARTENANCE.in(idOrganismes)
      .and((lower(CONTACT.NOM).like("%"+filtre.toLowerCase()+"%"))
        .or(lower(CONTACT.PRENOM).like("%"+filtre.toLowerCase()+"%")))).fetchInto(DestinataireModel.class);
    return destinataires;
  }

}
