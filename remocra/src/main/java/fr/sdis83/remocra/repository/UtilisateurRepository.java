package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Utilisateur;
import org.joda.time.Instant;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilisateurRepository {

  @Autowired DSLContext context;

  public UtilisateurRepository() {}

  @Bean
  public UtilisateurRepository utilisateurRepository(DSLContext context) {
    return new UtilisateurRepository(context);
  }

  UtilisateurRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Récupère un utilisateur en fonction de l'identifiant de connexion
   *
   * @param username de connexion de l'utilisateur
   * @return le username issue de la base s'il existe
   */
  public Utilisateur findUtilisateurWithoutCase(String username) {
    return context
        .selectFrom(UTILISATEUR)
        .where(UTILISATEUR.IDENTIFIANT.equalIgnoreCase(username))
        .fetchOneInto(Utilisateur.class);
  }

  /**
   * Récupère un mot de passe en fonction de l'identifiant de connexion
   *
   * @param identifiant de connexion de l'utilisateur
   * @return le mot de passe
   */
  public String getPasswordUtilisateur(String identifiant) {
    return context
        .select(UTILISATEUR.PASSWORD)
        .from(UTILISATEUR)
        .where(UTILISATEUR.IDENTIFIANT.eq(identifiant))
        .fetchOneInto(String.class);
  }

  /**
   * Met à jour la dernière connexion de l'utilisateur
   *
   * @param idUtilisateur
   */
  public void postDerniereConnexion(Long idUtilisateur) {
    context
        .update(UTILISATEUR)
        .set(UTILISATEUR.DERNIERE_CONNEXION, new Instant())
        .where(UTILISATEUR.ID.eq(idUtilisateur))
        .execute();
  }
}
