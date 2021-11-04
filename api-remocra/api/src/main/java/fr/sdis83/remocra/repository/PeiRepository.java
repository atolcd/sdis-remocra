package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.db.model.remocra.tables.Hydrant;
import fr.sdis83.remocra.db.model.remocra.tables.Organisme;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantPena;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantPibi;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.pei.PeiForm;
import fr.sdis83.remocra.web.model.pei.PeiModel;
import fr.sdis83.remocra.web.model.pei.PeiSpecifiqueModel;
import fr.sdis83.remocra.web.model.pei.PenaModel;
import fr.sdis83.remocra.web.model.pei.PibiModel;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import java.util.List;

import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PENA;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PIBI;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_VISITE;
import static fr.sdis83.remocra.db.model.remocra.Tables.ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.SITE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_DIAMETRE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_DOMAINE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_MARQUE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_MATERIAU;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_MODELE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE_DECI;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NIVEAU;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_RESEAU_ALIMENTATION;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_RESEAU_CANALISATION;


public class PeiRepository {

    private final DSLContext context;
    @Inject
    public PeiRepository(DSLContext context) {
        this.context = context;
    }

    /**
     * Indique si le PEI spécifié existe bien en base
     * @param numero Le numéro du PEI
     */
    public boolean peiExist(String numero){
      return context.fetchExists(context.select(HYDRANT.ID)
        .from(HYDRANT)
        .where(HYDRANT.NUMERO.equalIgnoreCase(numero)));
    }

    public List<PeiModel> getAll(String insee, String type, String codeNature, String natureDeci, Integer limit, Integer start,
     Long userId, String userTypeOrganisme) {
        Condition condition = this.getConditions(insee, type, codeNature, natureDeci, userId, userTypeOrganisme);

        Field<Object> X = DSL.field("round(st_x(hydrant.geometrie)::numeric, 2)").as("x");
        Field<Object> Y = DSL.field("round(st_y(hydrant.geometrie)::numeric, 2)").as("y");
        Field<Object> LONG = DSL.field("round(st_x(st_transform(hydrant.geometrie, 4326))::numeric, 8)").as("lon");
        Field<Object> LAT = DSL.field("round(st_y(st_transform(hydrant.geometrie, 4326))::numeric, 8)").as("lat");

        return context
            .select(
            COMMUNE.INSEE,
            HYDRANT.NUMERO.as("idSdis"),
            DSL.field("''::text").as("idGestion"),
            ORGANISME.NOM.as("nomGest"),
            HYDRANT.NUMERO_INTERNE.as("refTerr"),
            DSL.replace(TYPE_HYDRANT_NATURE.CODE.cast(String.class), "CI_FIXE", "CI").as("typePei"),
            DSL.field("''::text").as("typeRd"),
            TYPE_HYDRANT_DIAMETRE.NOM.as("diamPei"),
            HYDRANT_PIBI.DIAMETRE_CANALISATION.as("diamCana"),
            TYPE_HYDRANT_NATURE.NOM.as("sourcePei"),
            DSL.lower(DSL.replace(TYPE_HYDRANT_DOMAINE.NOM, "é", "e")).as("statut"),
            SITE.NOM.as("nomEtab"),
            HYDRANT.VOIE.as("situation"),
            HYDRANT_PIBI.PRESSION_DYN.as("pressDyn"),
            HYDRANT_PIBI.PRESSION.as("pressStat"),
            HYDRANT_PIBI.DEBIT.as("debit"),
            HYDRANT_PENA.VOL_CONSTATE.as("volume"),
            DSL.when(HYDRANT.DISPO_TERRESTRE.eq("DISPO")
                    .or(HYDRANT.DISPO_TERRESTRE.eq("NON_CONFORME")),true)
                .otherwise(false).as("disponible"),
            DSL.field("''::text").as("dateDispo"),
            DSL.toDate(HYDRANT.DATE_RECEP.cast(String.class), "YYYY-MM-DD").as("dateMes"),
            DSL.toDate(HYDRANT.DATE_MODIFICATION.cast(String.class), "YYYY-MM-DD").as("dateMaj"),
            DSL.toDate(HYDRANT.DATE_CONTR.cast(String.class), "YYYY-MM-DD").as("dateCt"),
            DSL.toDate(HYDRANT.DATE_RECO.cast(String.class), "YYYY-MM-DD").as("dateRo"),
            X,
            Y,
            LONG,
            LAT,
            DSL.field("''::text").as("prec")
        ).from(HYDRANT)
        .leftJoin(COMMUNE).on(COMMUNE.ID.eq(HYDRANT.COMMUNE))
        .leftJoin(TYPE_HYDRANT_NATURE).on(TYPE_HYDRANT_NATURE.ID.eq(HYDRANT.NATURE))
        .leftJoin(HYDRANT_PIBI).on(HYDRANT_PIBI.ID.eq(HYDRANT.ID))
        .leftJoin(TYPE_HYDRANT_DIAMETRE).on(TYPE_HYDRANT_DIAMETRE.ID.eq(HYDRANT_PIBI.DIAMETRE))
        .leftJoin(TYPE_HYDRANT_DOMAINE).on(TYPE_HYDRANT_DOMAINE.ID.eq(HYDRANT.DOMAINE))
        .leftJoin(HYDRANT_PENA).on(HYDRANT_PENA.ID.eq(HYDRANT.ID))
        .leftJoin(TYPE_HYDRANT_NATURE_DECI).on(TYPE_HYDRANT_NATURE_DECI.ID.eq(HYDRANT.NATURE_DECI))
        .leftJoin(SITE).on(SITE.ID.eq(HYDRANT.SITE))
        .leftJoin(ORGANISME).on(ORGANISME.ID.eq(HYDRANT_PIBI.SERVICE_EAUX))
        .where(condition)
        .limit((limit == null || limit < 0) ? this.count() : limit)
        .offset((start == null || start < 0) ? 0 : start)
        .fetchInto(PeiModel.class);
    }

    private Integer count() {
        return context.fetchCount(HYDRANT);
    }

    private Condition getConditions(String insee, String type, String codeNature, String natureDeci, Long userId, String userTypeOrganisme){
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

      // Conditions de récupération des PEI par rapport à l'organisme de l'utilisateur courant;
      Condition conditionOrganisme = DSL.falseCondition();

      // L'organisme est la maintenance DECI de l'hydrant
      if("SERVICEEAUX".equalsIgnoreCase(userTypeOrganisme)
        || "PRESTATAIRE_TECHNIQUE".equalsIgnoreCase(userTypeOrganisme)
        || "COMMUNE".equalsIgnoreCase(userTypeOrganisme)
        || "EPCI".equalsIgnoreCase(userTypeOrganisme)) {
        conditionOrganisme = conditionOrganisme.or(HYDRANT.MAINTENANCE_DECI.eq(userId));
      }

      // L'organisme est le service public DECI de l'hydrant
      if("COMMUNE".equalsIgnoreCase(userTypeOrganisme)
        || "EPCI".equalsIgnoreCase(userTypeOrganisme)) {
        conditionOrganisme = conditionOrganisme.or(HYDRANT.SP_DECI.eq(userId));
      }


      // L'organisme est le service des eaux de l'hydrant
      if("SERVICEEAUX".equalsIgnoreCase(userTypeOrganisme)) {
        conditionOrganisme = conditionOrganisme.or(HYDRANT_PIBI.SERVICE_EAUX.eq(userId));
      }

      return conditionOrganisme.and((condition));
    }

    public PeiSpecifiqueModel getPeiSpecifique(String numero) {

      Organisme autoritePolice = ORGANISME.as("autoritePolice");
      Organisme servicePublicDeci = ORGANISME.as("servicePublicDeci");

      Field<Object> X = DSL.field("round(st_x(hydrant.geometrie)::numeric, 2)").as("x");
      Field<Object> Y = DSL.field("round(st_y(hydrant.geometrie)::numeric, 2)").as("y");


      return context.select(
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
        .leftJoin(HYDRANT_PIBI).on(HYDRANT_PIBI.ID.eq(HYDRANT.ID))
        .where(HYDRANT.NUMERO.eq(numero))
        .fetchOneInto(PeiSpecifiqueModel.class);
    }

    public String getPeiCaracteristiques(String numero) throws JsonProcessingException {
      String typePei = context
        .select(HYDRANT.CODE)
        .from(HYDRANT)
        .where(HYDRANT.NUMERO.equalIgnoreCase(numero))
        .fetchOneInto(String.class);

      if(typePei.equalsIgnoreCase("PIBI")){
          return getPibiCaracteristiques(numero);
      } else{
          return getPenaCaracteristiques(numero);
      }
    }

    public String getPibiCaracteristiques(String numero) throws JsonProcessingException {

      Hydrant pibiJumele = HYDRANT.as("pibiJumele");
      PibiModel pibi = context.select(
                pibiJumele.NUMERO.as("jumelage"),
                TYPE_HYDRANT_DIAMETRE.CODE.as("diametre"),
                HYDRANT_PIBI.DISPOSITIF_INVIOLABILITE.as("inviolabilite"), // TODO : fix typo
                HYDRANT_PIBI.RENVERSABLE.as("renversable"),
                TYPE_HYDRANT_MARQUE.CODE.as("marque"),
                TYPE_HYDRANT_MODELE.CODE.as("modele"),
                HYDRANT.ANNEE_FABRICATION.as("anneeFabrication"),
                TYPE_RESEAU_ALIMENTATION.CODE.as("natureReseau"),
                TYPE_RESEAU_CANALISATION.CODE.as("natureCanalisation"),
                HYDRANT_PIBI.SURPRESSE.as("reseauSurpresse"),
                HYDRANT_PIBI.ADDITIVE.as("reseauAdditive"),
                HYDRANT_PIBI.DIAMETRE_CANALISATION.as("diametreCanalisation")
        ).from(HYDRANT_PIBI)
                .leftJoin(HYDRANT).on(HYDRANT.ID.eq(HYDRANT_PIBI.ID))
                .leftJoin(pibiJumele).on(pibiJumele.ID.eq(HYDRANT_PIBI.JUMELE))
                .leftJoin(TYPE_HYDRANT_DIAMETRE).on(TYPE_HYDRANT_DIAMETRE.ID.eq(HYDRANT_PIBI.DIAMETRE))
                .leftJoin(TYPE_HYDRANT_MARQUE).on(TYPE_HYDRANT_MARQUE.ID.eq(HYDRANT_PIBI.MARQUE))
                .leftJoin(TYPE_HYDRANT_MODELE).on(TYPE_HYDRANT_MODELE.ID.eq(HYDRANT_PIBI.MODELE))
                .leftJoin(TYPE_RESEAU_ALIMENTATION).on(TYPE_RESEAU_ALIMENTATION.ID.eq(HYDRANT_PIBI.TYPE_RESEAU_ALIMENTATION))
                .leftJoin(TYPE_RESEAU_CANALISATION).on(TYPE_RESEAU_CANALISATION.ID.eq(HYDRANT_PIBI.TYPE_RESEAU_CANALISATION))
                .where(HYDRANT.NUMERO.eq(numero))
                .fetchOneInto(PibiModel.class);
        return new ObjectMapper().writeValueAsString(pibi);
    }

    public String getPenaCaracteristiques(String numero) throws JsonProcessingException {
        PenaModel pena = context.select(
                HYDRANT_PENA.ILLIMITEE.as("capaciteIllimitee"),
                HYDRANT_PENA.INCERTAINE.as("ressourceIncertaine"),
                HYDRANT_PENA.CAPACITE.as("capacite"),
                HYDRANT_PENA.Q_APPOINT.as("debitAppoint"),
                TYPE_HYDRANT_MATERIAU.CODE.as("codeMateriau"),
                HYDRANT_PENA.HBE.as("equipeHBE")
        ).from(HYDRANT_PENA)
                .leftJoin(HYDRANT).on(HYDRANT.ID.eq(HYDRANT_PENA.ID))
                .leftJoin(TYPE_HYDRANT_MATERIAU).on(TYPE_HYDRANT_MATERIAU.ID.eq(HYDRANT_PENA.MATERIAU))
                .where(HYDRANT.NUMERO.eq(numero))
                .fetchAnyInto(PenaModel.class);

        return new ObjectMapper().writeValueAsString(pena);
    }

    public void updatePibiCaracteristiques(HydrantPibi hydrantPibi, Integer anneeFabrication) throws ResponseException {
      context.update(HYDRANT_PIBI)
        .set(HYDRANT_PIBI.DIAMETRE, hydrantPibi.getDiametre())
        .set(HYDRANT_PIBI.DIAMETRE_CANALISATION, hydrantPibi.getDiametreCanalisation())
        .set(HYDRANT_PIBI.MARQUE, hydrantPibi.getMarque())
        .set(HYDRANT_PIBI.MODELE, hydrantPibi.getModele())
        .set(HYDRANT_PIBI.TYPE_RESEAU_ALIMENTATION, hydrantPibi.getTypeReseauAlimentation())
        .set(HYDRANT_PIBI.TYPE_RESEAU_CANALISATION, hydrantPibi.getTypeReseauCanalisation())
        .set(HYDRANT_PIBI.DISPOSITIF_INVIOLABILITE, hydrantPibi.getDispositifInviolabilite())
        .set(HYDRANT_PIBI.RENVERSABLE, hydrantPibi.getRenversable())
        .set(HYDRANT_PIBI.SURPRESSE, hydrantPibi.getRenversable())
        .set(HYDRANT_PIBI.ADDITIVE, hydrantPibi.getAdditive())
        .where(HYDRANT_PIBI.ID.eq(hydrantPibi.getId()))
        .execute();

      context.update(HYDRANT)
        .set(HYDRANT.ANNEE_FABRICATION, anneeFabrication)
        .where(HYDRANT.ID.eq(hydrantPibi.getId()))
        .execute();
    }

    public void updatePenaCaracteristiques(HydrantPena hydrantPena) throws ResponseException {
      context.update(HYDRANT_PENA)
        .set(HYDRANT_PENA.MATERIAU, hydrantPena.getMateriau())
        .set(HYDRANT_PENA.ILLIMITEE, hydrantPena.getIllimitee())
        .set(HYDRANT_PENA.INCERTAINE, hydrantPena.getIncertaine())
        .set(HYDRANT_PENA.CAPACITE, hydrantPena.getCapacite())
        .set(HYDRANT_PENA.Q_APPOINT, hydrantPena.getQAppoint())
        .set(HYDRANT_PENA.HBE, hydrantPena.getHbe())
        .where(HYDRANT_PENA.ID.eq(hydrantPena.getId()))
        .execute();
    }

    /*
        Fonction qui met en place le jumelage entre deux pei
     */
    public String jumelagePei(String numero, String numeroJumeau) {

        Long idPeiJumeau = context.select(HYDRANT.ID)
                .from(HYDRANT)
                .where(HYDRANT.NUMERO.eq(numeroJumeau))
                .fetchOneInto(Long.class);

        if(idPeiJumeau != null){
            Hydrant h1 = HYDRANT.as("h1");
            Hydrant h2 = HYDRANT.as("h2");

            Field<Object> distance = DSL.field("(st_distance(h1.geometrie, h2.geometrie) < 25)");

            boolean distanceOk = context.select(distance)
                    .from(h1, h2)
                    .where(h1.NUMERO.eq(numero))
                    .and(h2.NUMERO.eq(numeroJumeau))
                    .fetchOneInto(Boolean.class);

            /*
            On regarde si les PEI sont des BI et s'ils sont distant de 25m max
            Si c'est la cas, on défait le jumelage existant si au moins l'un d'eux est déjà jumelé avec un autre PIBI
            et on met en place le jumelage
            */
            if(distanceOk && peiBi(numero) && peiBi(numeroJumeau)){
                //On récupère l'identifiant du pei
                Long idPei = context.select(HYDRANT.ID)
                        .from(HYDRANT)
                        .where(HYDRANT.NUMERO.eq(numero))
                        .fetchOneInto(Long.class);

                //On défait les jumelage sur les pei
                defaitJumelage(idPei);
                defaitJumelage(idPeiJumeau);

                //On ajoute le jumelage au pei
                context.update(HYDRANT_PIBI)
                        .set(HYDRANT_PIBI.JUMELE, idPeiJumeau)
                        .where(HYDRANT_PIBI.ID.eq(idPei))
                        .execute();

                //On ajoute le jumelage au peiJumeau
                context.update(HYDRANT_PIBI)
                        .set(HYDRANT_PIBI.JUMELE, idPei)
                        .where(HYDRANT_PIBI.ID.eq(idPeiJumeau))
                        .execute();
                return null;
            } else {
                return "Le jumelage entre les deux hydrants renseignés n'est pas possible. " +
                        "La distance entre les deux hydrants doit être inféreure à 25 mètres, et les hydrants doivent être de nature BI";
            }
        } else {
            return "Le numéro de PEI jumelé saisi ne correspond à aucun hydrant connu";
        }
    }

    /*
        Retourne true si le pei est de nature BI
     */
    public boolean peiBi(String numero){
        return context.select(DSL.field(TYPE_HYDRANT_NATURE.CODE.eq("BI")))
                .from(TYPE_HYDRANT_NATURE)
                .join(HYDRANT).on(HYDRANT.NATURE.eq(TYPE_HYDRANT_NATURE.ID))
                .where(HYDRANT.NUMERO.eq(numero))
                .fetchOneInto(Boolean.class);
    }

    /*
        Défait le jumelage d'un pei s'il en possède un
    */
    public void defaitJumelage(Long idPei){
        Long idAncienJumeau = context.select(HYDRANT_PIBI.JUMELE)
                .from(HYDRANT_PIBI)
                .where(HYDRANT_PIBI.ID.eq(idPei))
                .fetchOneInto(Long.class);

        if(idAncienJumeau != null){
            context.update(HYDRANT_PIBI)
                    .set(HYDRANT_PIBI.JUMELE, (Long) null)
                    .where(HYDRANT_PIBI.ID.eq(idAncienJumeau)).execute();

            context.update(HYDRANT_PIBI)
                    .set(HYDRANT_PIBI.JUMELE, (Long) null)
                    .where(HYDRANT_PIBI.ID.eq(idPei)).execute();
        }
    }
}
