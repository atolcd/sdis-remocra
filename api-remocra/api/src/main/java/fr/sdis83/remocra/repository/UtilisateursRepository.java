package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;

import javax.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

public class UtilisateursRepository {

  private final DSLContext context;

  @Inject
  public UtilisateursRepository(DSLContext context) {
    this.context = context;
  }

  public UtilisateurModel getByUsername(String username) {
    return context
        .select(
            UTILISATEUR.ID,
            UTILISATEUR.IDENTIFIANT,
            UTILISATEUR.PASSWORD,
            UTILISATEUR.SALT,
            UTILISATEUR.ACTIF,
            UTILISATEUR.EMAIL)
        .from(UTILISATEUR)
        .where(UTILISATEUR.IDENTIFIANT.equalIgnoreCase(username))
        .fetchOne(
            (RecordMapper<Record, UtilisateurModel>)
                record ->
                    ImmutableUtilisateurModel.builder()
                        .id(record.get(UTILISATEUR.ID))
                        .identifiant(record.get(UTILISATEUR.IDENTIFIANT))
                        .password(record.get(UTILISATEUR.PASSWORD))
                        .salt(record.get(UTILISATEUR.SALT))
                        .actif(Boolean.TRUE.equals(record.get(UTILISATEUR.ACTIF)))
                        .email(record.get(UTILISATEUR.EMAIL))
                        .build());
  }

  /**
   * Retourne l'id de l'organisme de l'utilisateur
   *
   * @param idUtilisateur
   * @return idOrganisme
   */
  public Long getOrganisme(Long idUtilisateur) {
    return context
        .select(UTILISATEUR.ORGANISME)
        .from(UTILISATEUR)
        .where(UTILISATEUR.ID.eq(idUtilisateur))
        .fetchOneInto(Long.class);
  }
}
