package fr.sdis83.remocra.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.service.HydrantService;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.util.JSONUtil;
import fr.sdis83.remocra.util.NumeroUtil;
import org.joda.time.Instant;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

import static fr.sdis83.remocra.db.model.remocra.Tables.DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_DOCUMENT;

@Configuration
public class HydrantRepository {

  @Autowired
  DSLContext context;

  @Autowired
  HydrantVisiteRepository hydrantVisiteRepository;

  @Autowired
  HydrantPibiRepository hydrantPibiRepository;

  @Autowired
  HydrantPenaRepository hydrantPenaRepository;

  @Autowired
  HydrantService hydrantService;

  @Autowired
  ParamConfService paramConfService;

  @Autowired
  UtilisateurService utilisateurService;

  @PersistenceContext
  protected EntityManager entityManager;

  public HydrantRepository() {
  }

  @Bean
  public HydrantRepository hydrantRepository(DSLContext context) {
    return new HydrantRepository(context);
  }

  HydrantRepository(DSLContext context) {
    this.context = context;
  }

  @Transactional
  public void updateHydrantFromFiche(Long id, String jsonData, String typeHydrant, Map<String, MultipartFile> files) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    Map<String, Object> data = objectMapper.readValue(jsonData, new TypeReference<Map<String,Object>>(){});
    Map<String, Object> visitesData = objectMapper.readValue(data.get("visites").toString(), new TypeReference<Map<String,Object>>(){});
    Map<String, Object> geometrieData = objectMapper.readValue(data.get("geometrie").toString(), new TypeReference<Map<String,Object>>(){});

    Hydrant hydrant = context.selectFrom(HYDRANT).where(HYDRANT.ID.eq(id)).fetchOneInto(Hydrant.class);
    if(hydrant == null) {
      throw new Exception("Le pei spécifié n'existe pas");
    }

    Point geom = this.hydrantService.coordonneesToPoint(geometrieData.toString());

    // Données de l'hydrant
    Hydrant h = new Hydrant();
    h.setId(id);
    h.setNumeroInterne(hydrant.getNumeroInterne());
    h.setNature(JSONUtil.getLong(data, "nature"));
    h.setNatureDeci(JSONUtil.getLong(data, "natureDeci"));
    h.setAutoriteDeci(JSONUtil.getLong(data, "autoriteDeci"));
    h.setSpDeci(JSONUtil.getLong(data, "spDeci"));
    h.setMaintenanceDeci(JSONUtil.getLong(data, "maintenanceDeci"));
    h.setGestionnaire(JSONUtil.getLong(data, "gestionnaire"));
    h.setSite(JSONUtil.getLong(data, "site"));
    h.setSuffixeVoie(JSONUtil.getString(data, "suffixeVoie"));
    h.setCommune(JSONUtil.getLong(data, "commune"));
    h.setDomaine(JSONUtil.getLong(data, "domaine"));
    h.setNumeroVoie(JSONUtil.getInteger(data, "numeroVoie"));
    h.setEnFace(JSONUtil.getBoolean(data, "enFace"));
    h.setNiveau(JSONUtil.getLong(data, "niveau"));
    h.setVoie(JSONUtil.getString(data, "voie"));
    h.setVoie2(JSONUtil.getString(data, "voie2"));
    h.setLieuDit(JSONUtil.getString(data, "lieuDit"));
    h.setComplement(JSONUtil.getString(data, "complement"));
    h.setAnneeFabrication(JSONUtil.getInteger(data, "anneeFabrication"));
    h.setGeometrie(geom);
    h.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme().getId());
    h.setUtilisateurModification(utilisateurService.getCurrentUtilisateur().getId());
    h.setDateModification(new Instant());

    this.updateHydrant(h);

    // Visites
    this.hydrantVisiteRepository.deleteVisiteFromFiche(id, visitesData.get("deleteVisite").toString());
    this.hydrantVisiteRepository.addVisiteFromFiche(id, visitesData.get("addVisite").toString());

    // Caractéristiques techniques
    if("PIBI".equalsIgnoreCase(typeHydrant)) {
      this.hydrantPibiRepository.updateHydrantPibiFromFiche(id, data);
    } else if("PENA".equalsIgnoreCase(typeHydrant)) {
      this.hydrantPenaRepository.updateHydrantPenaFromFiche(id, data);
    }

    // Documents
    // TODO : lors du passage des documents en jooq, passer le code dans le repository
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files.values()) {
        if (!file.isEmpty()) {
          Document d = DocumentUtil.getInstance().createNonPersistedDocument(Document.TypeDocument.HYDRANT, file, paramConfService.getDossierDocHydrant());
          String sousType = DocumentUtil.getInstance().getSousType(file);

          Long idDoc = context.insertInto(DOCUMENT)
            .set(DOCUMENT.CODE, d.getCode())
            .set(DOCUMENT.DATE_DOC, new Instant(d.getDateDoc()))
            .set(DOCUMENT.FICHIER, d.getFichier())
            .set(DOCUMENT.REPERTOIRE, d.getRepertoire())
            .set(DOCUMENT.TYPE, d.getType().toString())
            .returning(DOCUMENT.ID).fetchOne().getValue(DOCUMENT.ID);

          context.insertInto(HYDRANT_DOCUMENT)
            .set(HYDRANT_DOCUMENT.HYDRANT, id)
            .set(HYDRANT_DOCUMENT.DOCUMENT, idDoc)
            .execute();
        }
      }
    }
  }

  /**
   * Créé un hydrant depuis les informations passées par la fiche PEI
   * Une fois l'hydrant créé, on transmet les informations à la fonction d'update
   * @param jsonData Les données du PEI
   * @param typeHydrant Le type de PEI
   * @param files Les fichiers du PEI
   */
  public void createHydrantFromFiche(String jsonData, String typeHydrant, Map<String, MultipartFile> files) throws Exception {

    Long id = this.createHydrant();

    if("PIBI".equalsIgnoreCase(typeHydrant)) {
      this.hydrantPibiRepository.createHydrantPibi(id);
    } else if("PENA".equalsIgnoreCase(typeHydrant)) {
      this.hydrantPenaRepository.createHydrantPena(id);
    }

    this.updateHydrantFromFiche(id, jsonData, typeHydrant, files);

  }

  /**
   * Met à jour un hydrant en base
   * @param h Les données de l'hydrant
   * @return L'hydrant mis à jour
   */
  private Hydrant updateHydrant(Hydrant h) {

    context.update(HYDRANT)
      .set(HYDRANT.NUMERO_INTERNE, h.getNumeroInterne())
      .set(HYDRANT.NATURE, h.getNature())
      .set(HYDRANT.NATURE_DECI, h.getNatureDeci())
      .set(HYDRANT.AUTORITE_DECI, h.getAutoriteDeci())
      .set(HYDRANT.SP_DECI, h.getSpDeci())
      .set(HYDRANT.MAINTENANCE_DECI, h.getMaintenanceDeci())
      .set(HYDRANT.GESTIONNAIRE, h.getGestionnaire())
      .set(HYDRANT.SITE, h.getSite())
      .set(HYDRANT.SUFFIXE_VOIE, h.getSuffixeVoie())
      .set(HYDRANT.COMMUNE, h.getCommune())
      .set(HYDRANT.DOMAINE, h.getDomaine())
      .set(HYDRANT.NUMERO_VOIE, h.getNumeroVoie())
      .set(HYDRANT.EN_FACE, h.getEnFace())
      .set(HYDRANT.NIVEAU, h.getNiveau())
      .set(HYDRANT.VOIE, h.getVoie())
      .set(HYDRANT.VOIE2, h.getVoie2())
      .set(HYDRANT.LIEU_DIT, h.getLieuDit())
      .set(HYDRANT.COMPLEMENT, h.getComplement())
      .set(HYDRANT.ANNEE_FABRICATION, h.getAnneeFabrication())
      .set(HYDRANT.GEOMETRIE, h.getGeometrie())
      .set(HYDRANT.DATE_MODIFICATION, h.getDateModification())
      .set(HYDRANT.ORGANISME, h.getOrganisme())
      .set(HYDRANT.UTILISATEUR_MODIFICATION, h.getUtilisateurModification())
      .where(HYDRANT.ID.eq(h.getId()))
      .execute();

    // Mise à jour numéro et numéro interne
    fr.sdis83.remocra.domain.remocra.Hydrant hydrant = fr.sdis83.remocra.domain.remocra.Hydrant.findHydrant(h.getId());
    NumeroUtil.setCodeZoneSpecAndNumeros(hydrant);
    this.entityManager.persist(hydrant);

    return context
      .selectFrom(HYDRANT)
      .where(HYDRANT.ID.eq(h.getId()))
      .fetchOneInto(Hydrant.class);
  }

  /**
   * Créé un hydrant en base
   * @return
   */
  private Long createHydrant() {

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 2154);
    Point p = geometryFactory.createPoint(new Coordinate(0, 0));

    return context
      .insertInto(HYDRANT)
      .set(HYDRANT.GEOMETRIE, p)
      .returning(HYDRANT.ID).fetchOne().getValue(HYDRANT.ID);
  }
}