package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.repository.ImmutableUtilisateurModel;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

import javax.inject.Inject;

import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;

public class UtilisateursRepository {

    private final DSLContext context;

    @Inject
    public UtilisateursRepository(DSLContext context) {
        this.context = context;
    }

    public UtilisateurModel getByUsername(String username) {
        return context
                .select(UTILISATEUR.ID,
                        UTILISATEUR.IDENTIFIANT,
                        UTILISATEUR.PASSWORD,
                        UTILISATEUR.SALT,
                        UTILISATEUR.ACTIF,
                        UTILISATEUR.EMAIL)
                .from(UTILISATEUR)
                .where(UTILISATEUR.IDENTIFIANT.equalIgnoreCase(username))
                .fetchOne((RecordMapper<Record, UtilisateurModel>) record ->
                        ImmutableUtilisateurModel.builder()
                                .id(record.get(UTILISATEUR.ID))
                                .identifiant(record.get(UTILISATEUR.IDENTIFIANT))
                                .password(record.get(UTILISATEUR.PASSWORD))
                                .salt(record.get(UTILISATEUR.SALT))
                                .actif(Boolean.TRUE.equals(record.get(UTILISATEUR.ACTIF)))
                                .email(record.get(UTILISATEUR.EMAIL))
                                .build()
                );
    }
}
