package fr.sdis83.remocra.repository;

import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;
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


  /**
   * @param id identifiant d'un organisme
   * @return liste de String au format <id;nom;"ORGANISME">
   */
  public List<String> getDestinataireOrganisme(Integer id, String filtre){
    List<String> destinataires = new ArrayList<String>();
    Result<Record2<Long, String>> result = context.select(ORGANISME.ID, ORGANISME.NOM).from(ORGANISME)
    .where(ORGANISME.ID.eq(Long.valueOf(id))
    .and(lower(ORGANISME.NOM).like("%"+filtre.toLowerCase()+"%"))
    .and(ORGANISME.EMAIL_CONTACT.isNotNull())).fetch();
    for(Record r : result){
      destinataires.add(r.getValue(0)+";"+r.getValue(1)+";"+"ORGANISME");
    }
    return destinataires;
  }

  /**
   * @param id identifiant d'un organisme
   * @return liste de String au format <id,nom prenom,"CONTACT">
   */
  public List<String> getDesinataireContact(Integer id, String filtre){
    List<String> destinataires = new ArrayList<String>();
    Result<Record3<Long, String, String>> result = context.select(CONTACT.ID, CONTACT.NOM, CONTACT.PRENOM)
      .from(CONTACT).where(CONTACT.ID_APPARTENANCE.eq(String.valueOf(id))
      .and((lower(CONTACT.NOM).like("%"+filtre.toLowerCase()+"%"))
        .or(lower(CONTACT.PRENOM).like("%"+filtre.toLowerCase()+"%")))).fetch();
    for(Record r : result){
      destinataires.add(r.getValue(0)+";"+r.getValue(1)+" "+r.getValue(2)+";"+"CONTACT");
    }
    return destinataires;
  }

  /**
   * @param id identifiant d'un organisme
   * @return liste de String au format <id,nom prenom,"UTILISATEUR">
   */
  public List<String> getDestinataireUtilisateur(Integer id, String filtre){
    List<String> destinataires = new ArrayList<String>();
    Result<Record3<Long, String, String>> result = context.select(UTILISATEUR.ID, UTILISATEUR.NOM, UTILISATEUR.PRENOM)
      .from(UTILISATEUR).
      where(UTILISATEUR.ORGANISME.eq(Long.valueOf(id))
      .and(UTILISATEUR.MESSAGE_REMOCRA)
      .and((lower(UTILISATEUR.NOM).like("%"+filtre.toLowerCase()+"%"))
        .or(lower(UTILISATEUR.PRENOM).like("%"+filtre.toLowerCase()+"%")))).fetch();
    for(Record r : result){
      destinataires.add(r.getValue(0)+";"+r.getValue(1)+" "+r.getValue(2)+";"+"UTILISATEUR");
    }
    return destinataires;
  }

}
