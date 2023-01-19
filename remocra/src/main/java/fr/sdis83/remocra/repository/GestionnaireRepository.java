package fr.sdis83.remocra.repository;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT_ROLES;
import static fr.sdis83.remocra.db.model.remocra.Tables.GESTIONNAIRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.GESTIONNAIRE_SITE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.SITE;

@Configuration
public class GestionnaireRepository {
    @Autowired
    DSLContext context;

    GestionnaireRepository(DSLContext context){
        this.context = context;
    }

    public GestionnaireRepository() {
    }

    @Bean
    public  GestionnaireRepository gestionnaireRepository(DSLContext context){
        return new GestionnaireRepository(context);
    }

    /**
     * Retourne la liste des numéros d'hydrants d'un gestionnaire
     * @param idGestionnaire
     * @return une liste des numéros d'hydrants
     */
    public List<String> getHydrantWithIdGestionnaire(Long idGestionnaire) {
        return context.selectDistinct(HYDRANT.NUMERO)
                .from(HYDRANT)
                .where(HYDRANT.GESTIONNAIRE.eq(idGestionnaire))
                .fetchInto(String.class);
    }

    /**
     * Récupère tous les idContact d'un gestionnaire
     * @param idGestionnaire
     * @return la liste des identifiants des contacts
     */
    public List<Long> getContactGestionnaire(Long idGestionnaire) {
        return context.select(CONTACT.ID).from(CONTACT)
                .where(CONTACT.APPARTENANCE.eq("GESTIONNAIRE"))
                .and(CONTACT.ID_APPARTENANCE.eq(idGestionnaire.toString()))
                .fetchInto(Long.class);
    }

    /**
     * Supprime les liens entre les contacts et les rôles
     * @param listIdContact
     */
    public void deleteContactRole(List<Long> listIdContact) {
        context.deleteFrom(CONTACT_ROLES)
                .where(CONTACT_ROLES.CONTACT.in(listIdContact))
                .execute();
    }


    /**
     * Supprime les contacts d'un gestionnaire
     * @param idGestionnaire
     */
    public void deleteContactGestionnaire(Long idGestionnaire) {
        context.deleteFrom(CONTACT)
                .where(CONTACT.APPARTENANCE.eq("GESTIONNAIRE"))
                .and(CONTACT.ID_APPARTENANCE.eq(idGestionnaire.toString()))
                .execute();
    }
    /**
     * Supprime le site
     * @param idGestionnaire
     */
    public void deleteSite(Long idGestionnaire) {
        context.deleteFrom(SITE)
                .where(SITE.GESTIONNAIRE_SITE.eq(idGestionnaire))
                .execute();
    }



    /**
     * Supprime le gestionnaire site
     * @param idGestionnaire
     */
    public void deleteGestionnaireSite(Long idGestionnaire) {
        context.deleteFrom(GESTIONNAIRE_SITE)
                .where(GESTIONNAIRE_SITE.GESTIONNAIRE.eq(idGestionnaire))
                .execute();
    }

    /**
     * Supprime le gestionnaire
     * @param idGestionnaire
     */
    public void deleteGestionnaire(Long idGestionnaire) {
        context.deleteFrom(GESTIONNAIRE)
                .where(GESTIONNAIRE.ID.eq(idGestionnaire))
                .execute();
    }


}
