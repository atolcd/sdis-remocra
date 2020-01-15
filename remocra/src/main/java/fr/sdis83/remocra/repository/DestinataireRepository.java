package fr.sdis83.remocra.repository;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Result;
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
   * @param id identifiant d'un organisme
   * @return liste de String au format <id;nom;"ORGANISME">
   */
  public List<String> getDestinataireOrganisme(Long id, String filtre){
    List<String> destinataires = new ArrayList<String>();
    Result<Record3<Long, String, String>> result = context.select(ORGANISME.ID, ORGANISME.NOM, ORGANISME.EMAIL_CONTACT).from(ORGANISME)
    .where(ORGANISME.ID.eq(id)
    .and(lower(ORGANISME.NOM).like("%"+filtre.toLowerCase()+"%")
      .or(lower(ORGANISME.EMAIL_CONTACT).like("%"+filtre.toLowerCase()+"%")))
    .and(ORGANISME.EMAIL_CONTACT.isNotNull())).fetch();
    for(Record r : result){
      //                            id                 nom               email             fonction
      destinataires.add("ORGANISME;"+r.getValue(0)+";"+r.getValue(1)+";"+r.getValue(2)+";"+" ");
    }
    return destinataires;
  }

  /**
   * @param id identifiant d'un organisme
   * @return liste de String au format <id,nom prenom,"CONTACT">
   */
  public List<String> getDesinataireContact(Long id, String filtre){
    List<String> destinataires = new ArrayList<String>();
    Result<Record5<Long, String, String, String, String>> result = context.select(CONTACT.ID, CONTACT.NOM, CONTACT.PRENOM, CONTACT.EMAIL, CONTACT.FONCTION)
      .from(CONTACT).where(CONTACT.ID_APPARTENANCE.eq(String.valueOf(id))
      .and((lower(CONTACT.NOM).like("%"+filtre.toLowerCase()+"%"))
        .or(lower(CONTACT.PRENOM).like("%"+filtre.toLowerCase()+"%")))).fetch();
    for(Record r : result){
      //                            id                   nom                 prenom               email               fonction
      destinataires.add("CONTACT;"+ r.getValue(0)+";"+r.getValue(1)+" "+r.getValue(2)+";"+r.getValue(3)+";"+r.getValue(4));
    }
    return destinataires;
  }

  /**
   * @param id identifiant d'un organisme
   * @return liste de String au format <id,nom prenom,"UTILISATEUR">
   */
  public List<String> getDestinataireUtilisateur(Long id, String filtre){
    List<String> destinataires = new ArrayList<String>();
    Result<Record5<Long, String, String, String, String>> result = context.select(UTILISATEUR.ID, UTILISATEUR.NOM, UTILISATEUR.PRENOM, UTILISATEUR.EMAIL, PROFIL_UTILISATEUR.NOM)
      .from(UTILISATEUR)
      .join(PROFIL_UTILISATEUR).on(UTILISATEUR.PROFIL_UTILISATEUR.eq(PROFIL_UTILISATEUR.ID))
      .where(UTILISATEUR.ORGANISME.eq(id)
      .and(UTILISATEUR.MESSAGE_REMOCRA)
      .and((lower(UTILISATEUR.NOM).like("%"+filtre.toLowerCase()+"%"))
        .or(lower(UTILISATEUR.PRENOM).like("%"+filtre.toLowerCase()+"%"))
        .or(lower(UTILISATEUR.EMAIL).like("%"+filtre.toLowerCase()+"%"))
        )).fetch();
    for(Record r : result){
      //                               id                 nom                 prenom               email               profil
      destinataires.add("UTILISATEUR;"+r.getValue(0)+";"+r.getValue(1)+" "+r.getValue(2)+";"+r.getValue(3)+";"+r.getValue(4));
    }
    return destinataires;
  }

}
