package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.jooq.SortField;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.ZONE_COMPETENCE;
import static fr.sdis83.remocra.db.model.remocra.Tables.ZONE_COMPETENCE_COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.UTILISATEUR;

@Configuration
public class CommuneRepository {
    @Autowired
    DSLContext context;

    public CommuneRepository() {
    }

    @Bean
    public CommuneRepository communeRepository(DSLContext context) {
        return new CommuneRepository(context);
    }

    CommuneRepository(DSLContext context) {
        this.context = context;
    }

    /**
     * Permet de récupérer la liste des communes en fonction des droits de l'utilisateur et des filtres appliqués
     * @param organismes : liste des organismes accessibles
     * @param query : search field renseigné par l'utilisateur
     * @param insee : permet de pouvoir filtrer sur uniquement les communes du département
     *              (il est possible pour des raisons métier d'avoir les communes limitrophes d'autres départements)
     * @param asc : booléen pour savoir dans quel sens on trie
     * @param limit
     * @param offset
     * @return la liste des communes auxquelles l'utilisateur a accès
     */
    public List<Commune> getListCommune(
            List<Integer> organismes,
            String query,
            String insee,
            boolean asc,
            int limit,
            int offset) {
        // On prend en compte la longueur pour éviter que les communes à 2 caractères n'apparaissent pas
        Collection<SortField<?>> order = new ArrayList();
        if(asc) {
            order.add(COMMUNE.NOM.length().asc());
            order.add(COMMUNE.NOM.asc());
        } else {
            order.add(COMMUNE.NOM.desc());
        }
        return context.selectDistinct(
                    COMMUNE.ID,
                    COMMUNE.NOM,
                    COMMUNE.CODE,
                    COMMUNE.INSEE,
                    COMMUNE.GEOMETRIE,
                    COMMUNE.NOM.length())
                .from(COMMUNE)
                .join(ZONE_COMPETENCE_COMMUNE)
                .on(COMMUNE.ID.eq(ZONE_COMPETENCE_COMMUNE.COMMUNE_ID))
                .join(ZONE_COMPETENCE)
                .on(ZONE_COMPETENCE_COMMUNE.ZONE_COMPETENCE_ID.eq(ZONE_COMPETENCE.ID))
                .join(ORGANISME)
                .on(ORGANISME.ZONE_COMPETENCE.eq(ZONE_COMPETENCE.ID))
                .leftOuterJoin(UTILISATEUR)
                .on(UTILISATEUR.ORGANISME.eq(ORGANISME.ID))
                .where(UTILISATEUR.ORGANISME.in(organismes))
                .and(COMMUNE.NOM.upper().like("%"+query.toUpperCase()+"%"))
                .and(COMMUNE.INSEE.like(insee))
                .orderBy(order)
                .limit(limit)
                .offset(offset)
                .fetchInto(Commune.class);
    }
}
