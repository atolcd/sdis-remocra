package fr.sdis83.remocra.repository;

import org.jooq.DSLContext;

import javax.inject.Inject;
import static fr.sdis83.remocra.db.model.remocra.Tables.GESTIONNAIRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.CONTACT;
import static fr.sdis83.remocra.db.model.remocra.Tables.ROLE;

public class GestionnaireRepository {
    private final DSLContext context;

    @Inject
    public GestionnaireRepository(DSLContext context) {
        this.context = context;
    }

    public boolean checkExist(Long idGestionnaire) {
        return context.fetchExists(
                context.selectFrom(GESTIONNAIRE)
                        .where(GESTIONNAIRE.ID.eq(idGestionnaire))
        );
    }

    public boolean checkContactExist(Long idContact) {
        return context.fetchExists(
                context.selectFrom(CONTACT)
                        .where(CONTACT.ID.eq(idContact))
        );
    }

    public boolean checkRoleExist(Long idRole) {
        return context.fetchExists(
                context.selectFrom(ROLE)
                        .where(ROLE.ID.eq(idRole))
        );
    }
}
