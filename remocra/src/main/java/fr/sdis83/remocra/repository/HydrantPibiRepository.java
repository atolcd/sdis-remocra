package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantPibi;
import fr.sdis83.remocra.util.JSONUtil;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PIBI;

@Configuration
public class HydrantPibiRepository {

  @Autowired
  DSLContext context;

  public HydrantPibiRepository() {
  }

  @Bean
  public HydrantPibiRepository hydrantPibiRepository(DSLContext context) {
    return new HydrantPibiRepository(context);
  }

  HydrantPibiRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Met à jour un PIBI depuis les informations transmises par la fiche PEI
   * @param id L'identifiant de l'hydrant
   * @param data Les données du PIBI
   * @return Le PIBI contenant ses nouvelles informations
   */
  public HydrantPibi updateHydrantPibiFromFiche(Long id, Map<String, Object> data) {

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

    return this.updateHydrantPibi(pibi);
  }

  /**
   * Met à jour un PIBI en base
   * @param pibi Les informations du PIBI
   * @return Le PIBI contenant ses nouvelles informations
   */
  private HydrantPibi updateHydrantPibi(HydrantPibi pibi) {

    // On met en place le jumelage
    if(pibi.getJumele() != null) {
      // Si le PEI était déjà jumellé, on retire le jumelage
      context.update(HYDRANT_PIBI)
        .set(HYDRANT_PIBI.JUMELE, DSL.val(null, SQLDataType.BIGINT))
        .where(HYDRANT_PIBI.JUMELE.eq(pibi.getJumele()))
        .execute();

      context.update(HYDRANT_PIBI)
        .set(HYDRANT_PIBI.JUMELE, pibi.getId())
        .where(HYDRANT_PIBI.ID.eq(pibi.getJumele()))
        .execute();
    }

    context.update(HYDRANT_PIBI)
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
      .where(HYDRANT_PIBI.ID.eq(pibi.getId()))
      .execute();

    return context
      .selectFrom(HYDRANT_PIBI)
      .where(HYDRANT_PIBI.ID.eq(pibi.getId()))
      .fetchOneInto(HydrantPibi.class);
  }

  /**
   * Créé un PIBI en base
   * @param id L'identifiant de l'hydrant
   * @return L'identifiant du PIBI créé
   */
  public Long createHydrantPibi(Long id) {
    return context
      .insertInto(HYDRANT_PIBI)
      .set(HYDRANT_PIBI.ID, id)
      .returning(HYDRANT_PIBI.ID).fetchOne().getValue(HYDRANT_PIBI.ID);
  }
}
