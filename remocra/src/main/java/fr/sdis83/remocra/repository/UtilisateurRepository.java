package fr.sdis83.remocra.repository;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import fr.sdis83.remocra.db.model.remocra.tables.pojos.Utilisateur;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;

@Configuration
public class UtilisateurRepository {


    @Autowired
    DSLContext context;

    public UtilisateurRepository() {
    }

    @Bean
    public UtilisateurRepository utilisateurRepository(DSLContext context) {
        return new UtilisateurRepository(context);
    }

    UtilisateurRepository(DSLContext context) {
        this.context = context;
    }


    /**
     * Récupère un mot de passe en fonction de l'identifiant de connexion
     * @param identifiant de connexion de l'utilisateur
     * @return le mot de passe
     */
    public String getPasswordUtilisateur(String identifiant) {
       return context.select(UTILISATEUR.PASSWORD)
                .from(UTILISATEUR)
                .where(UTILISATEUR.IDENTIFIANT.eq(identifiant))
                .fetchOneInto(String.class);
    }



}