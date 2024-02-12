package fr.sdis83.remocra.repository;

import static fr.sdis83.remocra.db.model.remocra.Tables.COMMUNE;
import static fr.sdis83.remocra.db.model.remocra.Tables.DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_DOCUMENT;
import static fr.sdis83.remocra.db.model.remocra.Tables.HYDRANT_VISITE;
import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_HYDRANT_SAISIE;
import static fr.sdis83.remocra.db.model.remocra.tables.HydrantPibi.HYDRANT_PIBI;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Point;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantVisite;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.HydrantService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.util.JSONUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.model.HydrantRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.jooq.RecordValueReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class HydrantRepository {

  @Autowired DSLContext context;

  @Autowired HydrantVisiteRepository hydrantVisiteRepository;

  @Autowired HydrantPibiRepository hydrantPibiRepository;

  @Autowired HydrantPenaRepository hydrantPenaRepository;

  @Autowired HydrantService hydrantService;

  @Autowired ParametreDataProvider parametreProvider;

  @Autowired UtilisateurService utilisateurService;

  @Autowired DataSource dataSource;

  @PersistenceContext protected EntityManager entityManager;

  public static final String AUTEUR_MODIFICATION_FLAG = "USER";
  public static final String TYPE_HYDRANT_PENA = "PENA";
  public static final String TYPE_HYDRANT_PIBI = "PIBI";

  private final Logger logger = Logger.getLogger(getClass());

  public HydrantRepository() {}

  @Bean
  public HydrantRepository hydrantRepository(DSLContext context) {
    return new HydrantRepository(context);
  }

  HydrantRepository(DSLContext context) {
    this.context = context;
  }

  public static final int NUM_CELL_OBSERVATION = 18;
  public static final int NUM_CELL_DEBUT_ANOMALIE = 13;
  public static final int NUM_CELL_FIN_ANOMALIE = 17;

  public void updateHydrantFromFiche(
      Long id, String jsonData, String typeHydrant, Map<String, MultipartFile> files)
      throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();

    Map<String, Object> data =
        objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
    Map<String, Object> visitesData =
        objectMapper.readValue(
            data.get("visites").toString(), new TypeReference<Map<String, Object>>() {});
    Map<String, Object> geometrieData =
        objectMapper.readValue(
            data.get("geometrie").toString(), new TypeReference<Map<String, Object>>() {});

    Hydrant hydrant = this.getById(id);
    if (hydrant == null) {
      throw new Exception("Le pei spécifié n'existe pas");
    }

    Point geom = this.hydrantService.coordonneesToPoint(geometrieData.toString());

    // Données de l'hydrant
    Hydrant h = new Hydrant();
    h.setId(id);

    // Reprise du numéro, ne sera changé que si les conditions sont réunies (pas de numéro interne
    // ou renumérotation activée)
    h.setNumero(hydrant.getNumero());

    h.setNumeroInterne(JSONUtil.getInteger(data, "numeroInterne"));
    h.setNature(JSONUtil.getLong(data, "nature"));
    h.setNatureDeci(JSONUtil.getLong(data, "natureDeci"));
    h.setAutoriteDeci(JSONUtil.getLong(data, "autoriteDeci"));
    h.setSpDeci(JSONUtil.getLong(data, "spDeci"));
    h.setMaintenanceDeci(JSONUtil.getLong(data, "maintenanceDeci"));
    h.setGestionnaire(JSONUtil.getLong(data, "gestionnaire"));
    h.setGestionnaireSite(JSONUtil.getLong(data, "gestionnaireSite"));
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
    h.setUtilisateurModification(utilisateurService.getCurrentUtilisateur().getId());
    h.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme().getId());
    h.setAuteurModificationFlag(AUTEUR_MODIFICATION_FLAG);
    h.setDateModification(new Instant());

    this.updateHydrant(h);

    // Visites
    this.hydrantVisiteRepository.deleteVisiteFromFiche(
        id, visitesData.get("deleteVisite").toString());
    this.hydrantVisiteRepository.addVisiteFromFiche(id, visitesData.get("addVisite").toString());

    // Caractéristiques techniques
    if (TYPE_HYDRANT_PIBI.equalsIgnoreCase(typeHydrant)) {
      this.hydrantPibiRepository.updateHydrantPibiFromFiche(id, data);
    } else if (TYPE_HYDRANT_PENA.equalsIgnoreCase(typeHydrant)) {
      this.hydrantPenaRepository.updateHydrantPenaFromFiche(id, data);
    }

    // Documents
    // TODO : lors du passage des documents en jooq, passer le code dans le repository
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files.values()) {
        if (!file.isEmpty()) {
          Document d =
              DocumentUtil.getInstance()
                  .createNonPersistedDocument(
                      Document.TypeDocument.HYDRANT,
                      file,
                      parametreProvider.get().getDossierDocHydrant());
          String sousType = DocumentUtil.getInstance().getSousType(file);

          Long idDoc =
              context
                  .insertInto(DOCUMENT)
                  .set(DOCUMENT.CODE, d.getCode())
                  .set(DOCUMENT.DATE_DOC, new Instant(d.getDateDoc()))
                  .set(DOCUMENT.FICHIER, d.getFichier())
                  .set(DOCUMENT.REPERTOIRE, d.getRepertoire())
                  .set(DOCUMENT.TYPE, d.getType().toString())
                  .returning(DOCUMENT.ID)
                  .fetchOne()
                  .getValue(DOCUMENT.ID);

          context
              .insertInto(HYDRANT_DOCUMENT)
              .set(HYDRANT_DOCUMENT.HYDRANT, id)
              .set(HYDRANT_DOCUMENT.DOCUMENT, idDoc)
              .execute();
        }
      }
    }
  }

  /**
   * Créé un hydrant depuis les informations passées par la fiche PEI Une fois l'hydrant créé, on
   * transmet les informations à la fonction d'update
   *
   * @param jsonData Les données du PEI
   * @param typeHydrant Le type de PEI
   * @param files Les fichiers du PEI
   */
  public void createHydrantFromFiche(
      String jsonData, String typeHydrant, Map<String, MultipartFile> files) throws Exception {

    ObjectMapper objectMapper = new ObjectMapper();

    Map<String, Object> data =
        objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
    Map<String, Object> visitesData =
        objectMapper.readValue(
            data.get("visites").toString(), new TypeReference<Map<String, Object>>() {});
    Map<String, Object> geometrieData =
        objectMapper.readValue(
            data.get("geometrie").toString(), new TypeReference<Map<String, Object>>() {});

    Point geom = this.hydrantService.coordonneesToPoint(geometrieData.toString());

    Hydrant h = new Hydrant();
    h.setCode(typeHydrant);
    h.setNature(JSONUtil.getLong(data, "nature"));
    h.setNatureDeci(JSONUtil.getLong(data, "natureDeci"));
    h.setAutoriteDeci(JSONUtil.getLong(data, "autoriteDeci"));
    h.setSpDeci(JSONUtil.getLong(data, "spDeci"));
    h.setMaintenanceDeci(JSONUtil.getLong(data, "maintenanceDeci"));
    h.setGestionnaire(JSONUtil.getLong(data, "gestionnaire"));
    h.setGestionnaireSite(JSONUtil.getLong(data, "gestionnaire_site"));
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
    h.setUtilisateurModification(utilisateurService.getCurrentUtilisateur().getId());
    h.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme().getId());
    h.setAuteurModificationFlag(AUTEUR_MODIFICATION_FLAG);
    h.setDateModification(new Instant());

    Long id = this.createHydrant(h, parametreProvider.get().getSridInt());

    if (TYPE_HYDRANT_PIBI.equalsIgnoreCase(typeHydrant)) {
      this.hydrantPibiRepository.createHydrantPibiFromFiche(id, data);
    } else if (TYPE_HYDRANT_PENA.equalsIgnoreCase(typeHydrant)) {
      this.hydrantPenaRepository.createHydrantPenaFromFiche(id, data);
    }

    this.hydrantVisiteRepository.deleteVisiteFromFiche(
        id, visitesData.get("deleteVisite").toString());
    this.hydrantVisiteRepository.addVisiteFromFiche(id, visitesData.get("addVisite").toString());

    // Documents
    // TODO : lors du passage des documents en jooq, passer le code dans le repository
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files.values()) {
        if (!file.isEmpty()) {
          Document d =
              DocumentUtil.getInstance()
                  .createNonPersistedDocument(
                      Document.TypeDocument.HYDRANT,
                      file,
                      parametreProvider.get().getDossierDocHydrant());
          String sousType = DocumentUtil.getInstance().getSousType(file);

          Long idDoc =
              context
                  .insertInto(DOCUMENT)
                  .set(DOCUMENT.CODE, d.getCode())
                  .set(DOCUMENT.DATE_DOC, new Instant(d.getDateDoc()))
                  .set(DOCUMENT.FICHIER, d.getFichier())
                  .set(DOCUMENT.REPERTOIRE, d.getRepertoire())
                  .set(DOCUMENT.TYPE, d.getType().toString())
                  .returning(DOCUMENT.ID)
                  .fetchOne()
                  .getValue(DOCUMENT.ID);

          context
              .insertInto(HYDRANT_DOCUMENT)
              .set(HYDRANT_DOCUMENT.HYDRANT, id)
              .set(HYDRANT_DOCUMENT.DOCUMENT, idDoc)
              .execute();
        }
      }
    }
  }

  /**
   * Créé un hydrant en base
   *
   * @return
   */
  private Long createHydrant(Hydrant h, int srid) {
    h = NumeroUtilRepository.setCodeZoneSpecAndNumeros(h, h.getCode(), srid);

    Long id =
        context
            .insertInto(HYDRANT)
            .set(HYDRANT.NUMERO, h.getNumero())
            .set(HYDRANT.NUMERO_INTERNE, h.getNumeroInterne())
            .set(HYDRANT.CODE, h.getCode())
            .set(HYDRANT.NATURE, h.getNature())
            .set(HYDRANT.NATURE_DECI, h.getNatureDeci())
            .set(HYDRANT.AUTORITE_DECI, h.getAutoriteDeci())
            .set(HYDRANT.SP_DECI, h.getSpDeci())
            .set(HYDRANT.MAINTENANCE_DECI, h.getMaintenanceDeci())
            .set(HYDRANT.GESTIONNAIRE, h.getGestionnaire())
            .set(HYDRANT.GESTIONNAIRE_SITE, h.getGestionnaireSite())
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
            .set(HYDRANT.AUTEUR_MODIFICATION_FLAG, h.getAuteurModificationFlag())
            .set(HYDRANT.ZONE_SPECIALE, h.getZoneSpeciale())
            .returning(HYDRANT.ID)
            .fetchOne()
            .getValue(HYDRANT.ID);

    return id;
  }

  /**
   * Met à jour un hydrant en base
   *
   * @param h Les données de l'hydrant
   * @return L'hydrant mis à jour
   */
  private Hydrant updateHydrant(Hydrant h) {

    if ((h.getNumeroInterne() == null || h.getNumeroInterne() <= 0)
        || parametreProvider.get().getHydrantRenumerotationActivation()) {
      h =
          NumeroUtilRepository.setCodeZoneSpecAndNumeros(
              h, h.getCode(), parametreProvider.get().getSridInt());
    }

    context
        .update(HYDRANT)
        .set(HYDRANT.NUMERO, h.getNumero())
        .set(HYDRANT.NUMERO_INTERNE, h.getNumeroInterne())
        .set(HYDRANT.NATURE, h.getNature())
        .set(HYDRANT.NATURE_DECI, h.getNatureDeci())
        .set(HYDRANT.AUTORITE_DECI, h.getAutoriteDeci())
        .set(HYDRANT.SP_DECI, h.getSpDeci())
        .set(HYDRANT.MAINTENANCE_DECI, h.getMaintenanceDeci())
        .set(HYDRANT.GESTIONNAIRE, h.getGestionnaire())
        .set(HYDRANT.GESTIONNAIRE_SITE, h.getGestionnaireSite())
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
        .set(HYDRANT.AUTEUR_MODIFICATION_FLAG, h.getAuteurModificationFlag())
        .set(HYDRANT.ZONE_SPECIALE, h.getZoneSpeciale())
        .where(HYDRANT.ID.eq(h.getId()))
        .execute();

    return this.getById(h.getId());
  }

  public List<HydrantRecord> getAll(
      List<ItemFilter> itemFilters, Integer limit, Integer start, List<ItemSorting> itemSortings) {
    String condition = this.getFilters(itemFilters);

    boolean triAlphaNumerique = this.parametreProvider.get().getHydrantMethodeTriAlphanumerique();

    String sortFields =
        (triAlphaNumerique)
            ? "numero ASC"
            : "length(numero) ASC, numero ASC"; // Critères de tri par défaut si aucun renseigné

    if (itemSortings.size() > 0) {
      Collection<String> listOrderFields = new ArrayList<String>();
      boolean hasNumero = false;
      for (ItemSorting item : itemSortings) {
        if ("numero".equals(item.getFieldName())) {
          hasNumero = true;
          if (!triAlphaNumerique) { // Un tri sur le numéro se transforme automatiquement en tri
            // naturel (sauf paramétrage contraire)
            listOrderFields.add("length(numero) " + item.getDirection());
          }
        }
        listOrderFields.add(item.getFieldName() + " " + item.getDirection());
      }
      if (!hasNumero) {
        listOrderFields.add(sortFields);
      }
      sortFields = StringUtils.join(listOrderFields.toArray(), ",");
    }

    Result<Record> hydrantRecord = null;

    String orgAndChildren =
        StringUtils.join(
            Organisme.getOrganismeAndChildren(
                    utilisateurService.getCurrentUtilisateur().getOrganisme().getId().intValue())
                .toArray(),
            ",");

    List<HydrantRecord> hr = new ArrayList<HydrantRecord>();
    StringBuffer sbReq =
        new StringBuffer(getAutoriteDeci())
            .append(
                "select h.id  as  id , h.code as code, ST_AsGeoJSON(h.geometrie) as jsonGeometrie, h.numero as numero, h.date_contr as dateContr,")
            .append(
                " case when char_length(h.voie)>0 and char_length(h.voie2)>0 then h.voie || ' - ' || h.voie2 else voie end as adresse,")
            .append(" h.dispo_terrestre as dispoTerrestre,")
            .append(" h.dispo_hbe as dispoHbe,")
            .append(" h.date_reco as dateReco, h.numero_interne as numeroInterne,")
            .append(" h.date_recep as dateRecep,")
            .append(" c.nom as nomCommune,")
            .append(" thnd.nom as nomNatureDeci,")
            .append(" thn.nom as natureNom,")
            .append(
                " (SELECT COALESCE(string_agg(nom, ', ' order by nom), '') FROM remocra.tournee t where t.affectation in ("
                    + orgAndChildren
                    + ") and ")
            .append(
                " t.id in (SELECT tournees FROM remocra.hydrant_tournees ht where ht.hydrant = h.id)) as nomTournee,")
            .append(
                " (select count(*) from remocra.hydrant_anomalies ha where ha.hydrant = h.id AND ha.anomalies = (select tha.id from remocra.type_hydrant_anomalie tha where tha.code = 'INDISPONIBILITE_TEMP')) as indispoTemp, ")
            .append(" autoriteDeci.autoriteDeci")
            .append(" from remocra.hydrant h")
            .append(" join remocra.commune c on c.id = h.commune")
            .append(" join remocra.type_hydrant_nature_deci thnd on thnd.id = h.nature_deci")
            .append(" join remocra.type_hydrant_nature thn on thn.id = h.nature")
            .append(" left join autoriteDeci on autoriteDeci.id = h.autorite_deci ")
            .append(" where " + condition)
            .append(" order by " + sortFields)
            .append(" limit " + limit)
            .append(" offset " + start);

    hydrantRecord = context.fetch(sbReq.toString());

    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new RecordValueReader());
    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
    for (Record r : hydrantRecord) {
      HydrantRecord hydrant = modelMapper.map(r, HydrantRecord.class);
      hr.add(hydrant);
    }
    return hr;
  }

  public String getFilters(List<ItemFilter> itemFilters) {
    String condition = "1 = 1";
    for (ItemFilter itemFilter : itemFilters) {
      if ("tournee".equals(itemFilter.getFieldName())) {
        condition +=
            " and h.id in  (select hydrant from remocra.hydrant_tournees where tournees = "
                + Long.valueOf(itemFilter.getValue())
                + ")";
      } else if ("zoneCompetence".equals(itemFilter.getFieldName())) {
        Long zoneCompetenceId = utilisateurService.getCurrentZoneCompetenceId();
        condition +=
            " and st_contains((select st_simplify(zc.geometrie ,1) from remocra.zone_competence zc where  id ="
                + zoneCompetenceId
                + " ), h.geometrie)";
      } else if ("autoriteDeci".equals(itemFilter.getFieldName())) {
        condition += " and autoriteDeci ilike '%" + itemFilter.getValue() + "%'";
      } else if ("numero".equals(itemFilter.getFieldName())) {
        condition +=
            " and upper(h.numero) like '%"
                + itemFilter.getValue().toUpperCase(Locale.FRANCE)
                + "%' ";
      } else if ("dateReco".equals(itemFilter.getFieldName())) {
        Integer nbMonths = Integer.valueOf(itemFilter.getValue());
        if (nbMonths != 0) {
          final DateTime datePrive =
              new DateTime()
                  .minus(Period.days(parametreProvider.get().getHydrantRenouvellementRecoPrive()));
          final DateTime datePublic =
              new DateTime()
                  .minus(Period.days(parametreProvider.get().getHydrantRenouvellementRecoPublic()));
          if (nbMonths > 0) {
            condition +=
                " and case when thnd.code = 'PRIVE' then h.date_reco <= '"
                    + datePrive.plus(Period.months(nbMonths)).toDate()
                    + "'::date and h.date_reco >= '"
                    + datePrive.toDate()
                    + "'::date"
                    + " else h.date_reco <='"
                    + datePublic.plus(Period.months(nbMonths)).toDate()
                    + "'::date and h.date_reco >= '"
                    + datePublic.toDate()
                    + "'::date end";
          } else {
            condition +=
                " and case when thnd.code = 'PRIVE' then h.date_reco <= '"
                    + datePrive.toDate()
                    + "'::date else h.date_reco <='"
                    + datePublic.toDate()
                    + "'::date end";
          }
        }
      } else if ("dateContr".equals(itemFilter.getFieldName())) {
        Integer nbMonths = Integer.valueOf(itemFilter.getValue());
        if (nbMonths != 0) {
          final DateTime datePrive =
              new DateTime()
                  .minus(Period.days(parametreProvider.get().getHydrantRenouvellementCtrlPrive()));
          final DateTime datePublic =
              new DateTime()
                  .minus(Period.days(parametreProvider.get().getHydrantRenouvellementCtrlPublic()));
          if (nbMonths > 0) {
            condition +=
                " and case when thnd.code = 'PRIVE' then h.date_contr <= '"
                    + datePrive.plus(Period.months(nbMonths)).toDate()
                    + "'::date and h.date_contr >= '"
                    + datePrive.toDate()
                    + "'::date"
                    + " else h.date_contr <='"
                    + datePublic.plus(Period.months(nbMonths)).toDate()
                    + "'::date and h.date_contr >= '"
                    + datePublic.toDate()
                    + "'::date end";
          } else {
            condition +=
                " and case when thnd.code = 'PRIVE' then h.date_contr <= '"
                    + datePrive.toDate()
                    + "'::date else h.date_contr <='"
                    + datePublic.toDate()
                    + "'::date end";
          }
        }
      } else if ("nature".equals(itemFilter.getFieldName())) {
        condition += " and h.nature = " + Long.valueOf(itemFilter.getValue());
      } else if ("naturecode".equals(itemFilter.getFieldName())) {
        // Exemples de valeur : 'PI,PA' ou 'PI' ou ''

      } else if ("dispoTerrestre".equals(itemFilter.getFieldName())) {
        condition += " and h.dispo_terrestre like '" + itemFilter.getValue() + "'";
      } else if ("dispoHbe".equals(itemFilter.getFieldName())) {
        condition += " and h.dispo_hbe like '" + itemFilter.getValue() + "'";
      } else if ("nomCommune".equals(itemFilter.getFieldName())) {
        /**
         * A cause de l'utilisation hybride du champ commune, le serveur peut récupérer deux données
         * différentes sur le même path selon le type de donnée fourni : - Un nombre entier
         * (identifiant) lors d'une sélection dans la combo - Une chaîne de caratères (nom) lors de
         * la saisie de texte dans la combo
         */
        if (StringUtils.isNumeric(itemFilter.getValue())) {
          condition += " and c.id = " + Long.valueOf(itemFilter.getValue());
        } else {
          condition +=
              " and upper(c.nom) like '%" + itemFilter.getValue().toUpperCase(Locale.FRANCE) + "%'";
        }
      } else if ("nomNatureDeci".equals(itemFilter.getFieldName())) {
        condition += " and h.nature_deci = " + Long.valueOf(itemFilter.getValue());
      } else if ("codeNatureDeci".equals(itemFilter.getFieldName())) {

      } else if ("adresse".equals(itemFilter.getFieldName())) {
        condition +=
            " and (lower(h.voie || ' - ' || h.voie2) like '%"
                + itemFilter.getValue().toLowerCase()
                + "%' or lower(h.voie)  like '%"
                + itemFilter.getValue().toLowerCase()
                + "%')";
      } else if ("numeroInterne".equals(itemFilter.getFieldName())) {
        condition += " and h.numero_interne = " + Integer.valueOf(itemFilter.getValue());
      } else {
        return condition;
      }
    }
    return condition;
  }

  /**
   * Bout de requête qui permet de récupérer l'autorité DECI
   *
   * @return
   */
  private String getAutoriteDeci() {
    return "with autoriteDeci AS (select h.autorite_deci as id,  "
        + " case "
        + "   when type_organisme.code = 'COMMUNE' then 'Maire (' || o.nom || ')'"
        + "   when type_organisme.code = 'EPCI' then 'Président (' || o.nom||')'"
        + "   when type_organisme.code = 'PREFECTURE' then 'Préfet (' || o.nom||')'"
        + " else o.nom end as autoriteDeci"
        + " from remocra.organisme o join remocra.type_organisme on type_organisme.id = o.type_organisme"
        + " join remocra.hydrant h on h.autorite_deci = o.id"
        + " group by h.autorite_deci, type_organisme.code, o.nom) ";
  }

  @Transactional
  public long countHydrants(List<ItemFilter> itemFilters) {
    String condition = this.getFilters(itemFilters);
    StringBuffer sbReq =
        new StringBuffer(getAutoriteDeci())
            .append(" select count(h.id) as  total")
            .append(" from remocra.hydrant h")
            .append(" join remocra.commune c on c.id = h.commune")
            .append(" join remocra.type_hydrant_nature_deci thnd on thnd.id = h.nature_deci")
            .append(" join remocra.type_hydrant_nature thn on thn.id = h.nature")
            .append(" left join autoriteDeci on autoriteDeci.id = h.autorite_deci")
            .append(" where")
            .append(" " + condition);

    return (Long) context.fetchOne(sbReq.toString()).getValue("total");
  }

  public Hydrant getById(Long id) {
    return context.selectFrom(HYDRANT).where(HYDRANT.ID.eq(id)).fetchOneInto(Hydrant.class);
  }

  /**
   * Vérifie que la cohérence du triplet (id, numéroInterne, codeInsee) pour l'hydrant
   *
   * @param id long
   * @param numeroInterne int
   * @param codeInsee String
   * @return true si le résultat est cohérent, false sinon
   */
  public boolean checkForImportCTP(long id, int numeroInterne, String codeInsee) {
    return context.fetchExists(
        context
            .select(HYDRANT.ID)
            .from(HYDRANT)
            .join(COMMUNE)
            .on(COMMUNE.ID.eq(HYDRANT.COMMUNE))
            .where(COMMUNE.INSEE.eq(codeInsee))
            .and(HYDRANT.NUMERO_INTERNE.eq(numeroInterne))
            .and(HYDRANT.ID.eq(id)));
  }

  /**
   * Vérifie si le PEI dont l'ID est passé en paramètre est bien dans la zone de compétence fournie
   *
   * @param idHydrant id de l'hydrant
   * @param idZoneCompetence id de la zone de compétence
   * @return boolean true s'il est dans la zone de compétence, false sinon
   */
  public boolean withinZoneCompetence(long idHydrant, long idZoneCompetence) {
    return context
        .resultQuery(
            "SELECT ST_CONTAINS(zc.geometrie, h.geometrie) "
                + "FROM remocra.zone_competence zc "
                + "JOIN remocra.hydrant h on h.id = {0}"
                + "WHERE zc.id = {1};",
            idHydrant, idZoneCompetence)
        .fetchOneInto(boolean.class);
  }

  /**
   * Retourne le nombre de visites de contrôle effectuées après un instant donné pour le PEI
   *
   * @param idHydrant long
   * @param instant Instant
   * @return int
   */
  public int getNbVisitesCtrlWithCondition(long idHydrant, Instant instant) {
    return getNbVisitesCtrlWithCondition(idHydrant, HYDRANT_VISITE.DATE.greaterThan(instant));
  }

  /**
   * Retourne le nombre de visites de contrôle effectuées à un instant donné pour le PEI
   *
   * @param idHydrant long
   * @param instant Instant
   * @return int
   */
  public int getNbVisitesEqInstant(long idHydrant, Instant instant) {

    return context
        .selectCount()
        .from(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(idHydrant))
        .and(HYDRANT_VISITE.DATE.eq(instant))
        .fetchOneInto(int.class);
  }

  private int getNbVisitesCtrlWithCondition(long idHydrant, Condition predicatInstant) {
    return context
        .selectCount()
        .from(HYDRANT_VISITE)
        .join(TYPE_HYDRANT_SAISIE)
        .on(HYDRANT_VISITE.TYPE.eq(TYPE_HYDRANT_SAISIE.ID))
        .where(
            HYDRANT_VISITE
                .HYDRANT
                .eq(idHydrant)
                .and(TYPE_HYDRANT_SAISIE.CODE.eq(GlobalConstants.TypeVisite.CONTROLE.getCode()))
                .and(HYDRANT_VISITE.CTRL_DEBIT_PRESSION)
                .and(predicatInstant))
        .fetchOneInto(int.class);
  }

  public HydrantVisite getDernierVisite(long idHydrant, Instant instant) {
    return context
        .selectFrom(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(idHydrant))
        .and(HYDRANT_VISITE.DATE.lessThan(instant))
        .orderBy(HYDRANT_VISITE.DATE.desc())
        .limit(1)
        .fetchOneInto(HydrantVisite.class);
  }

  public int getNbVisites(long idHydrant) {
    return context
        .selectCount()
        .from(HYDRANT_VISITE)
        .where(HYDRANT_VISITE.HYDRANT.eq(idHydrant))
        .fetchOneInto(int.class);
  }

  /**
   * Permet d'update la position d'un hydrant en prenant en compte le changement de commune s'il y
   * a.
   *
   * @param id identifiant de l'hydrant
   * @param point la position de l'hydrant
   * @param srid
   * @return l'hydrant modifié
   * @throws BusinessException
   */
  private Hydrant updateHydrantDeplacement(Long id, Point point, Integer srid)
      throws BusinessException {
    Hydrant h = this.getById(id);
    if (h == null) {
      BusinessException e = new BusinessException("L'hydrant n'existe pas en base");
      throw e;
    }

    h.setDateGps(null);
    h.setDateModification(new Instant());
    h.setUtilisateurModification(utilisateurService.getCurrentUtilisateur().getId());
    h.setAuteurModificationFlag(AUTEUR_MODIFICATION_FLAG);
    h.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme().getId());
    point.setSRID(srid);
    h.setGeometrie(point);

    // Gestion de la commune
    List<Commune> listeCommune =
        Commune.findCommunesByPoint(srid, point.toString(), parametreProvider.get().getSridInt());
    if (!listeCommune.isEmpty()) {
      if (!h.getCommune().equals(listeCommune.get(0).getId())) { // Si la commune a changé
        h.setCommune(listeCommune.get(0).getId()); // update h.commune
        // remise à zéro de SpDECI et AutoritéDECI car ces valeurs dépendent de la commune
        h.setSpDeci(null);
        h.setAutoriteDeci(null);
      }
    }
    return this.updateHydrant(h);
  }

  /**
   * Permet de déplacer un hydrant depuis la carte
   *
   * @param id
   * @param point
   * @param srid
   * @throws CRSException
   * @throws IllegalCoordinateException
   * @throws BusinessException
   */
  public void deplacer(Long id, Point point, Integer srid)
      throws CRSException, IllegalCoordinateException, BusinessException {
    fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant h =
        this.updateHydrantDeplacement(id, point, srid);

    if (TYPE_HYDRANT_PENA.equalsIgnoreCase(h.getCode())) {
      try {
        String coordDFCI =
            GeometryUtil.findCoordDFCIFromGeom(
                dataSource, point, parametreProvider.get().getSridInt());
        hydrantPenaRepository.updateCoorDdfci(id, coordDFCI);
      } catch (Exception e) {
        logger.debug("Problème lors de la requête sur la table remocra_referentiel.carro_dfci", e);
      }
    }
  }

  public Long getIdHydrantByNumero(String numero) {
    return context
        .select(HYDRANT.ID)
        .from(HYDRANT)
        .where(HYDRANT.NUMERO.eq(numero))
        .fetchOneInto(Long.class);
  }

  public void updateHydrantGestionnaire(Long idHydrant, Long newGestionnaireId) {
    context
        .update(HYDRANT)
        .set(HYDRANT.GESTIONNAIRE, newGestionnaireId)
        .where(HYDRANT.ID.eq(idHydrant))
        .execute();
  }

  public void updateHydrantGestionnaireSite(Long idHydrant, Long newGestionnaireSiteId) {
    context
        .update(HYDRANT)
        .set(HYDRANT.GESTIONNAIRE_SITE, newGestionnaireSiteId)
        .where(HYDRANT.ID.eq(idHydrant))
        .execute();
  }

  /**
   * Retourne la distance entre le PEI et les coordonnées passées en paramètre
   *
   * @param idHydrant long
   * @param latitude double
   * @param longitude double
   * @return int
   */
  public int getDistanceFromCoordonnees(long idHydrant, double latitude, double longitude) {
    int srid = parametreProvider.get().getSridInt();
    return context
        .resultQuery(
            "SELECT ST_DISTANCE(ST_transform(ST_SetSRID(ST_MakePoint({0}, {1}),{2}), {3}), h.geometrie) "
                + "FROM remocra.hydrant h "
                + "WHERE h.id = {4};",
            longitude, latitude, Integer.parseInt(GlobalConstants.SRID_4326), srid, idHydrant)
        .fetchOneInto(int.class);
  }

  /**
   * Met à jour la géométrie d'un PEI
   *
   * @param idHydrant long
   * @param p Point
   */
  public void updateGeometrie(long idHydrant, Point p) {
    context.update(HYDRANT).set(HYDRANT.GEOMETRIE, p).where(HYDRANT.ID.eq(idHydrant)).execute();
  }

  /**
   * Déclenche les triggers associés à la modification d'un identifiant de PEI.
   *
   * @param idHydrant long
   */
  public void updatePibiForTrigger(long idHydrant) {
    context
        .update(HYDRANT_PIBI)
        .set(HYDRANT_PIBI.ID, idHydrant)
        .where(HYDRANT_PIBI.ID.eq(idHydrant))
        .execute();
  }
}
