package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PIBI;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_NATURE;

import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantPibi;
import fr.sdis83.remocra.util.JSONUtil;
import java.util.List;
import java.util.Map;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HydrantPibiRepository {

  private static final String BI = "BI";
  private static final Integer DISTANCE_MINIMAL_JUMELAGE = 25;
  private static final String SRID = "SRID=" + GlobalConstants.SRID_PARAM + ";";

  @Autowired DSLContext context;

  public HydrantPibiRepository() {}

  @Bean
  public HydrantPibiRepository hydrantPibiRepository(DSLContext context) {
    return new HydrantPibiRepository(context);
  }

  HydrantPibiRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Créé un PIBI depuis les informations transmises par la fiche PEI
   *
   * @param id L'identifiant de l'hydrant
   * @param data Les données du PIBI
   * @return Le PIBI contenant ses nouvelles informations
   */
  public HydrantPibi createHydrantPibiFromFiche(Long id, Map<String, Object> data) {
    HydrantPibi pibi = new HydrantPibi();
    pibi.setId(id);
    pibi.setDiametre(JSONUtil.getLong(data, "diametre"));
    pibi.setDispositifInviolabilite(JSONUtil.getBoolean(data, "dispositif_inviolabilite"));
    pibi.setRenversable(JSONUtil.getBoolean(data, "renversable"));
    pibi.setJumele(JSONUtil.getLong(data, "jumele"));
    pibi.setMarque(JSONUtil.getLong(data, "marque"));
    pibi.setModele(JSONUtil.getLong(data, "modele"));
    pibi.setServiceEaux(JSONUtil.getLong(data, "serviceEaux"));
    pibi.setTypeReseauAlimentation(JSONUtil.getLong(data, "typeReseauAlimentation"));
    pibi.setDebitRenforce(JSONUtil.getBoolean(data, "debitRenforce"));
    pibi.setTypeReseauCanalisation(JSONUtil.getLong(data, "typeReseauCanalisation"));
    pibi.setDiametreCanalisation(JSONUtil.getInteger(data, "diametreCanalisation"));
    pibi.setReservoir(JSONUtil.getLong(data, "reservoir"));
    pibi.setSurpresse(JSONUtil.getBoolean(data, "surpresse"));
    pibi.setAdditive(JSONUtil.getBoolean(data, "additive"));
    pibi.setNumeroscp(JSONUtil.getString(data, "numeroSCP"));

    return this.createHydrantPibi(pibi);
  }

  /**
   * Met à jour un PIBI depuis les informations transmises par la fiche PEI
   *
   * @param id L'identifiant de l'hydrant
   * @param data Les données du PIBI
   * @return Le PIBI contenant ses nouvelles informations
   */
  public HydrantPibi updateHydrantPibiFromFiche(Long id, Map<String, Object> data) {

    HydrantPibi pibi = new HydrantPibi();
    pibi.setId(id);
    pibi.setDiametre(JSONUtil.getLong(data, "diametre"));
    pibi.setDebitNominal(JSONUtil.getInteger(data, "debitNominal"));
    pibi.setDispositifInviolabilite(JSONUtil.getBoolean(data, "dispositif_inviolabilite"));
    pibi.setRenversable(JSONUtil.getBoolean(data, "renversable"));
    pibi.setJumele(JSONUtil.getLong(data, "jumele"));
    pibi.setMarque(JSONUtil.getLong(data, "marque"));
    pibi.setModele(JSONUtil.getLong(data, "modele"));
    pibi.setServiceEaux(JSONUtil.getLong(data, "serviceEaux"));
    pibi.setTypeReseauAlimentation(JSONUtil.getLong(data, "typeReseauAlimentation"));
    pibi.setDebitRenforce(JSONUtil.getBoolean(data, "debitRenforce"));
    pibi.setTypeReseauCanalisation(JSONUtil.getLong(data, "typeReseauCanalisation"));
    pibi.setDiametreCanalisation(JSONUtil.getInteger(data, "diametreCanalisation"));
    pibi.setReservoir(JSONUtil.getLong(data, "reservoir"));
    pibi.setSurpresse(JSONUtil.getBoolean(data, "surpresse"));
    pibi.setAdditive(JSONUtil.getBoolean(data, "additive"));
    pibi.setNumeroscp(JSONUtil.getString(data, "numeroSCP"));

    return this.updateHydrantPibi(pibi);
  }

  /**
   * Met à jour un PIBI en base
   *
   * @param pibi Les informations du PIBI
   * @return Le PIBI contenant ses nouvelles informations
   */
  private HydrantPibi updateHydrantPibi(HydrantPibi pibi) {

    // On met en place le jumelage
    if (pibi.getJumele() != null) {
      // Si le PEI était déjà jumellé, on retire le jumelage
      context
          .update(HYDRANT_PIBI)
          .set(HYDRANT_PIBI.JUMELE, DSL.val(null, SQLDataType.BIGINT))
          .where(HYDRANT_PIBI.JUMELE.eq(pibi.getJumele()))
          .execute();

      context
          .update(HYDRANT_PIBI)
          .set(HYDRANT_PIBI.JUMELE, pibi.getId())
          .where(HYDRANT_PIBI.ID.eq(pibi.getJumele()))
          .execute();
    }

    context
        .update(HYDRANT_PIBI)
        .set(HYDRANT_PIBI.DIAMETRE, pibi.getDiametre())
        .set(HYDRANT_PIBI.DEBIT_NOMINAL, pibi.getDebitNominal())
        .set(HYDRANT_PIBI.DISPOSITIF_INVIOLABILITE, pibi.getDispositifInviolabilite())
        .set(HYDRANT_PIBI.RENVERSABLE, pibi.getRenversable())
        .set(HYDRANT_PIBI.JUMELE, pibi.getJumele())
        .set(HYDRANT_PIBI.MARQUE, pibi.getMarque())
        .set(HYDRANT_PIBI.MODELE, pibi.getModele())
        .set(HYDRANT_PIBI.SERVICE_EAUX, pibi.getServiceEaux())
        .set(HYDRANT_PIBI.TYPE_RESEAU_ALIMENTATION, pibi.getTypeReseauAlimentation())
        .set(HYDRANT_PIBI.DEBIT_RENFORCE, pibi.getDebitRenforce())
        .set(HYDRANT_PIBI.TYPE_RESEAU_CANALISATION, pibi.getTypeReseauCanalisation())
        .set(HYDRANT_PIBI.RESERVOIR, pibi.getReservoir())
        .set(HYDRANT_PIBI.SURPRESSE, pibi.getSurpresse())
        .set(HYDRANT_PIBI.ADDITIVE, pibi.getAdditive())
        .set(HYDRANT_PIBI.DIAMETRE_CANALISATION, pibi.getDiametreCanalisation())
        .set(HYDRANT_PIBI.NUMEROSCP, pibi.getNumeroscp())
        .where(HYDRANT_PIBI.ID.eq(pibi.getId()))
        .execute();

    return context
        .selectFrom(HYDRANT_PIBI)
        .where(HYDRANT_PIBI.ID.eq(pibi.getId()))
        .fetchOneInto(HydrantPibi.class);
  }

  /** Créé un PIBI en base */
  private HydrantPibi createHydrantPibi(HydrantPibi pibi) {
    context
        .insertInto(HYDRANT_PIBI)
        .set(HYDRANT_PIBI.ID, pibi.getId())
        .set(HYDRANT_PIBI.DIAMETRE, pibi.getDiametre())
        .set(HYDRANT_PIBI.DISPOSITIF_INVIOLABILITE, pibi.getDispositifInviolabilite())
        .set(HYDRANT_PIBI.RENVERSABLE, pibi.getRenversable())
        .set(HYDRANT_PIBI.JUMELE, pibi.getJumele())
        .set(HYDRANT_PIBI.MARQUE, pibi.getMarque())
        .set(HYDRANT_PIBI.MODELE, pibi.getModele())
        .set(HYDRANT_PIBI.SERVICE_EAUX, pibi.getServiceEaux())
        .set(HYDRANT_PIBI.TYPE_RESEAU_ALIMENTATION, pibi.getTypeReseauAlimentation())
        .set(HYDRANT_PIBI.DEBIT_RENFORCE, pibi.getDebitRenforce())
        .set(HYDRANT_PIBI.TYPE_RESEAU_CANALISATION, pibi.getTypeReseauCanalisation())
        .set(HYDRANT_PIBI.RESERVOIR, pibi.getReservoir())
        .set(HYDRANT_PIBI.SURPRESSE, pibi.getSurpresse())
        .set(HYDRANT_PIBI.ADDITIVE, pibi.getAdditive())
        .set(HYDRANT_PIBI.DIAMETRE_CANALISATION, pibi.getDiametreCanalisation())
        .set(HYDRANT_PIBI.NUMEROSCP, pibi.getNumeroscp())
        .execute();

    return context
        .selectFrom(HYDRANT_PIBI)
        .where(HYDRANT_PIBI.ID.eq(pibi.getId()))
        .fetchOneInto(HydrantPibi.class);
  }

  /**
   * Permet de trouver tous les hydrants éligibles pour un jumelage situés autour d'une géométrie
   * donnée Le jumelage est possible si deux hydrants de type BI se trouvent à moins de
   * DISTANCE_MINIMAL_JUMELAGE mètres l'un de l'autre
   *
   * @param geometrie La géométrie du PEI dont on recherche les jumelages possibles
   * @return Un objet JSON contenant l'identifiant et le numéro des hydrant pouvant être utilisés
   *     pour le jumelage
   */
  public JSONObject findJumelage(String geometrie) {
    JSONArray data = new JSONArray();
    List<Hydrant> liste =
        context
            .select(HYDRANT.ID, HYDRANT.NUMERO)
            .from(HYDRANT)
            .join(TYPE_HYDRANT_NATURE)
            .on(TYPE_HYDRANT_NATURE.ID.eq(HYDRANT.NATURE))
            .join(HYDRANT_PIBI)
            .on(HYDRANT.ID.eq(HYDRANT_PIBI.ID))
            .where(
                "ST_DISTANCE({0}, {1}) < {2}",
                HYDRANT.GEOMETRIE, SRID + geometrie, DISTANCE_MINIMAL_JUMELAGE)
            .and(TYPE_HYDRANT_NATURE.CODE.eq(BI))
            .and(HYDRANT_PIBI.JUMELE.isNull())
            .fetchInto(Hydrant.class);

    for (Hydrant hydrant : liste) {
      JSONObject obj = new JSONObject();
      obj.put("id", hydrant.getId().toString());
      obj.put("numero", hydrant.getNumero());
      data.put(obj);
    }
    JSONObject json = new JSONObject();
    json.put("data", data);
    return json;
  }
}
