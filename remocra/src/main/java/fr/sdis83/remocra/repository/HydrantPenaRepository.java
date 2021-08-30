package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantPena;
import fr.sdis83.remocra.util.JSONUtil;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_PENA;

@Configuration
public class HydrantPenaRepository {

  @Autowired
  DSLContext context;

  @Autowired
  HydrantAspirationRepository hydrantAspirationRepository;

  public HydrantPenaRepository() {
  }

  @Bean
  public HydrantPenaRepository hydrantPenaRepository(DSLContext context) {
    return new HydrantPenaRepository(context);
  }

  HydrantPenaRepository(DSLContext context) {
    this.context = context;
  }

  /**
   * Créé un PENA depuis les informations transmises par la fiche PEI
   * @param id L'identifiant de l'hydrant
   * @param data Les données du PENA
   * @return Le PENA contenant ses nouvelles informations
   */
  public HydrantPena createHydrantPenaFromFiche(Long id, Map<String, Object> data) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> aspirationsData = objectMapper.readValue(data.get("aspirations").toString(), new TypeReference<Map<String,Object>>(){});

    try {
      this.hydrantAspirationRepository.addHydrantAspirationFromFiche(id, aspirationsData.get("addAspirations").toString());
      this.hydrantAspirationRepository.deleteHydrantAspirationsFromFiche(id, aspirationsData.get("deleteAspirations").toString());
    } catch (CRSException e) {
      e.printStackTrace();
    } catch (IllegalCoordinateException e) {
      e.printStackTrace();
    }

    HydrantPena pena = new HydrantPena();
    pena.setId(id);
    pena.setIllimitee(JSONUtil.getBoolean(data, "illimitee"));
    pena.setIncertaine(JSONUtil.getBoolean(data, "incertaine"));
    pena.setCapacite(JSONUtil.getString(data, "capacite"));
    pena.setQAppoint(JSONUtil.getDouble(data, "QAppoint"));
    pena.setMateriau(JSONUtil.getLong(data, "materiau"));
    pena.setHbe(JSONUtil.getBoolean(data, "hbe"));

    return this.createHydrantPena(pena);
  }

  /**
   * Met à jour un PENA depuis les informations transmises par la fiche PEI
   * @param id L'identifiant de l'hydrant
   * @param data Les données du PENA
   * @return Le PENA contenant ses nouvelles informations
   */
  public HydrantPena updateHydrantPenaFromFiche(Long id, Map<String, Object> data) throws IOException {

    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> aspirationsData = objectMapper.readValue(data.get("aspirations").toString(), new TypeReference<Map<String,Object>>(){});

    try {
      this.hydrantAspirationRepository.addHydrantAspirationFromFiche(id, aspirationsData.get("addAspirations").toString());
      this.hydrantAspirationRepository.deleteHydrantAspirationsFromFiche(id, aspirationsData.get("deleteAspirations").toString());
    } catch (CRSException e) {
      e.printStackTrace();
    } catch (IllegalCoordinateException e) {
      e.printStackTrace();
    }
    HydrantPena pena = new HydrantPena();
    pena.setId(id);
    pena.setIllimitee(JSONUtil.getBoolean(data, "illimitee"));
    pena.setIncertaine(JSONUtil.getBoolean(data, "incertaine"));
    pena.setCapacite(JSONUtil.getString(data, "capacite"));
    pena.setQAppoint(JSONUtil.getDouble(data, "QAppoint"));
    pena.setMateriau(JSONUtil.getLong(data, "materiau"));
    pena.setHbe(JSONUtil.getBoolean(data, "hbe"));

    return this.updateHydrantPena(pena);
  }

  /**
   * Met à jour un PENA en base
   * @param pena Les informations du PENA
   * @return Le PENA contenant ses nouvelles informations
   */
  private HydrantPena updateHydrantPena(HydrantPena pena) {
    context.update(HYDRANT_PENA)
      .set(HYDRANT_PENA.ILLIMITEE, pena.getIllimitee())
      .set(HYDRANT_PENA.INCERTAINE, pena.getIncertaine())
      .set(HYDRANT_PENA.CAPACITE, pena.getCapacite())
      .set(HYDRANT_PENA.Q_APPOINT, pena.getQAppoint())
      .set(HYDRANT_PENA.MATERIAU, pena.getMateriau())
      .set(HYDRANT_PENA.HBE, pena.getHbe())
      .where(HYDRANT_PENA.ID.eq(pena.getId()))
      .execute();

    return context
      .selectFrom(HYDRANT_PENA)
      .where(HYDRANT_PENA.ID.eq(pena.getId()))
      .fetchOneInto(HydrantPena.class);
  }

  /**
   * Créé un PENA en base
   */
  private HydrantPena createHydrantPena(HydrantPena pena) {
    context
      .insertInto(HYDRANT_PENA)
      .set(HYDRANT_PENA.ID, pena.getId())
      .set(HYDRANT_PENA.ILLIMITEE, pena.getIllimitee())
      .set(HYDRANT_PENA.INCERTAINE, pena.getIncertaine())
      .set(HYDRANT_PENA.CAPACITE, pena.getCapacite())
      .set(HYDRANT_PENA.Q_APPOINT, pena.getQAppoint())
      .set(HYDRANT_PENA.MATERIAU, pena.getMateriau())
      .set(HYDRANT_PENA.HBE, pena.getHbe())
      .execute();

    return context
      .selectFrom(HYDRANT_PENA)
      .where(HYDRANT_PENA.ID.eq(pena.getId()))
      .fetchOneInto(HydrantPena.class);
  }
}