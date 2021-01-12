package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.db.model.tables.Organisme;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.pei.PeiModel;
import fr.sdis83.remocra.web.model.pei.PeiSpecifiqueModel;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;

import java.util.List;

import static fr.sdis83.remocra.db.model.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.Tables.HYDRANT_PENA;
import static fr.sdis83.remocra.db.model.Tables.HYDRANT_PIBI;
import static fr.sdis83.remocra.db.model.Tables.HYDRANT_VISITE;
import static fr.sdis83.remocra.db.model.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_DIAMETRE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_DOMAINE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_NATURE;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_NATURE_DECI;
import static fr.sdis83.remocra.db.model.Tables.TYPE_HYDRANT_NIVEAU;


public class PeiRepository {

    private final DSLContext context;
    @Inject
    public PeiRepository(DSLContext context) {
        this.context = context;
    }

    public boolean peiExist(String numero){
        Long idPei = context.select(HYDRANT.ID)
                .from(HYDRANT)
                .where(HYDRANT.NUMERO.equalIgnoreCase(numero))
                .fetchOneInto(Long.class);

        return idPei != null;
    }

    public String getAll(String insee, String type, String codeNature, String natureDeci, Integer limit, Integer start) throws JsonProcessingException {
        Condition condition = this.getConditions(insee, type, codeNature, natureDeci);

        Field<Object> X = DSL.field("round(st_x(hydrant.geometrie)::numeric, 2)").as("x");
        Field<Object> Y = DSL.field("round(st_y(hydrant.geometrie)::numeric, 2)").as("y");
        Field<Object> LONG = DSL.field("round(st_x(st_transform(hydrant.geometrie, 4326))::numeric, 8)").as("lon");
        Field<Object> LAT = DSL.field("round(st_y(st_transform(hydrant.geometrie, 4326))::numeric, 8)").as("lat");

        List<PeiModel> list = context.select(
            HYDRANT.NUMERO,
            COMMUNE.INSEE,
            HYDRANT.NUMERO_INTERNE.as("refTerr"),
            TYPE_HYDRANT_NATURE.CODE.as("typePei"),
            TYPE_HYDRANT_DIAMETRE.CODE.as("diamPei"),
            TYPE_HYDRANT_DOMAINE.NOM.as("statut"),
            HYDRANT.VOIE,
            HYDRANT_PIBI.PRESSION.as("pressStat"),
            HYDRANT_PIBI.PRESSION_DYN.as("pressDyn"),
            HYDRANT_PIBI.DEBIT.as("debit"),
            HYDRANT.DATE_RECEP.as("dateMes"),
            HYDRANT.DATE_CONTR.as("dateCt"),
            HYDRANT.DATE_RECO.as("dateRo"),
            X,
            Y,
            LONG,
            LAT
        ).from(HYDRANT)
        .leftJoin(COMMUNE).on(COMMUNE.ID.eq(HYDRANT.COMMUNE))
        .leftJoin(TYPE_HYDRANT_NATURE).on(TYPE_HYDRANT_NATURE.ID.eq(HYDRANT.NATURE))
        .leftJoin(HYDRANT_PIBI).on(HYDRANT_PIBI.ID.eq(HYDRANT.ID))
        .leftJoin(TYPE_HYDRANT_DIAMETRE).on(TYPE_HYDRANT_DIAMETRE.ID.eq(HYDRANT_PIBI.DIAMETRE))
        .leftJoin(TYPE_HYDRANT_DOMAINE).on(TYPE_HYDRANT_DOMAINE.ID.eq(HYDRANT.DOMAINE))
        .leftJoin(HYDRANT_PENA).on(HYDRANT_PENA.ID.eq(HYDRANT.ID))
        .leftJoin(TYPE_HYDRANT_NATURE_DECI).on(TYPE_HYDRANT_NATURE_DECI.ID.eq(HYDRANT.NATURE_DECI))
        .where(condition)
        .limit((limit == null || limit < 0) ? this.count() : limit)
        .offset((start == null || start < 0) ? 0 : start)
        .fetchInto(PeiModel.class);
        return new ObjectMapper().writeValueAsString(list);
    }

    private Integer count() {
        return context.fetchCount(HYDRANT);
    }

    private Condition getConditions(String insee, String type, String codeNature, String natureDeci){
        Condition condition = DSL.trueCondition();
        if(insee != null){
            condition = condition.and(COMMUNE.INSEE.eq(insee.toUpperCase()));
        }
        if(type != null){
            condition = condition.and(HYDRANT.CODE.eq(type.toUpperCase()));
        }
        if (codeNature != null) {
            condition = condition.and(TYPE_HYDRANT_NATURE.CODE.eq(codeNature.toUpperCase()));
        }
        if (natureDeci != null) {
            condition = condition.and(TYPE_HYDRANT_NATURE_DECI.CODE.eq(natureDeci.toUpperCase()));
        }
        return condition;
    }

    public String getPeiSpecifique(String numero) throws JsonProcessingException, ResponseException {
        Organisme autoritePolice = ORGANISME.as("autoritePolice");
        Organisme servicePublicDeci = ORGANISME.as("servicePublicDeci");

        Field<Object> X = DSL.field("round(st_x(hydrant.geometrie)::numeric, 2)").as("x");
        Field<Object> Y = DSL.field("round(st_y(hydrant.geometrie)::numeric, 2)").as("y");


        PeiSpecifiqueModel pei = context.select(
                HYDRANT.NUMERO.as("numero"),
                HYDRANT.CODE.as("type"),
                TYPE_HYDRANT_NATURE.CODE.as("nature"),
                autoritePolice.NOM.as("organismeAutoritePolice"),
                servicePublicDeci.NOM.as("organismeServicePublicDeci"),
                HYDRANT.MAINTENANCE_DECI.as("organismeTechnique"),
                TYPE_HYDRANT_NATURE_DECI.CODE.as("natureDeci"),
                X,
                Y,
                COMMUNE.NOM.as("commune"),
                HYDRANT.SUFFIXE_VOIE.as("suffixe"),
                HYDRANT.EN_FACE.as("enFace"),
                TYPE_HYDRANT_NIVEAU.CODE.as("niveau"),
                TYPE_HYDRANT_DOMAINE.CODE.as("domaine"),
                HYDRANT.VOIE.as("voie"),
                HYDRANT.VOIE2.as("carrefour"),
                HYDRANT.LIEU_DIT.as("lieuDit"),
                HYDRANT.COMPLEMENT.as("complement"),
                HYDRANT.DATE_MODIFICATION.as("dateDerniereModification"),
                DSL.greatest(HYDRANT.DATE_CONTR, HYDRANT.DATE_RECEP, HYDRANT.DATE_RECO, HYDRANT.DATE_VERIF).as("dateDerniereVisite"),
                HYDRANT.DISPO_TERRESTRE.as("dispoTerrestre"),
                HYDRANT.DISPO_HBE.as("dispoAerienne")

        ).from(HYDRANT)
                .leftJoin(TYPE_HYDRANT_NATURE).on(TYPE_HYDRANT_NATURE.ID.eq(HYDRANT.NATURE))
                .leftJoin(autoritePolice).on(autoritePolice.ID.eq(HYDRANT.ORGANISME))
                .leftJoin(servicePublicDeci).on(servicePublicDeci.ID.eq(HYDRANT.AUTORITE_DECI))
                .leftJoin(TYPE_HYDRANT_NATURE_DECI).on(TYPE_HYDRANT_NATURE_DECI.ID.eq(HYDRANT.NATURE_DECI))
                .leftJoin(COMMUNE).on(COMMUNE.ID.eq(HYDRANT.COMMUNE))
                .leftJoin(TYPE_HYDRANT_NIVEAU).on(TYPE_HYDRANT_NIVEAU.ID.eq(HYDRANT.NIVEAU))
                .leftJoin(TYPE_HYDRANT_DOMAINE).on(TYPE_HYDRANT_DOMAINE.ID.eq(HYDRANT.DOMAINE))
                .where(HYDRANT.NUMERO.eq(numero))
                .fetchOneInto(PeiSpecifiqueModel.class);

        if(pei != null){
            return new ObjectMapper().writeValueAsString(pei);
        } else {
            throw new ResponseException(HttpStatus.BAD_REQUEST, "Le numéro spécifié ne correspond à aucun hydrant");
        }




    }
}
