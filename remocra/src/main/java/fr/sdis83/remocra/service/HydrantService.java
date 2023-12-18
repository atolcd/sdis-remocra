package fr.sdis83.remocra.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.domain.remocra.Tournee;
import fr.sdis83.remocra.domain.remocra.TypeHydrantNature;
import fr.sdis83.remocra.domain.remocra.TypeHydrantNatureDeci;
import fr.sdis83.remocra.domain.remocra.ZoneSpeciale;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.repository.NumeroUtilRepository;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.apache.log4j.Logger;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class HydrantService extends AbstractHydrantService<Hydrant> {

  @Autowired private UtilisateurService utilisateurService;

  @Autowired private ParametreDataProvider parametreDataProvider;

  public HydrantService() {
    super(Hydrant.class);
  }

  public String getAbsOrderFieldName() {
    return "numero";
  }

  @Bean
  public HydrantService hydrantService() {
    return new HydrantService();
  }

  private final Logger logger = Logger.getLogger(getClass());

  protected boolean processItemSortings(
      ArrayList<Order> orders,
      ItemSorting itemSorting,
      CriteriaBuilder cBuilder,
      Root<Hydrant> from) {
    if ("tourneeId".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("tournees", JoinType.LEFT).get("id");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else if ("nomTournee".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("tournees", JoinType.LEFT).get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else if ("natureNom".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("nature").get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else if ("nomCommune".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("commune").get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else if ("adresse".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.get("adresse");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else if ("nomNatureDeci".equals(itemSorting.getFieldName())) {
      Expression<String> cpPath = from.join("natureDeci").get("nom");
      orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
      return true;
    } else {
      return super.processItemSortings(orders, itemSorting, cBuilder, from);
    }
  }

  @Transactional
  public int desaffecter(String json, Boolean allOrganismes) {
    ArrayList<Integer> items = new JSONDeserializer<ArrayList<Integer>>().deserialize(json);
    ArrayList<Long> ids = new ArrayList<Long>();

    // On ne désaffecte que pour les tournées de notre organisme ou des organismes enfants si
    // allOrganismes = true
    // Sinon, on ne désaffecte que pour les tournées de notre organisme
    UtilisateurService utilisateurService = new UtilisateurService();
    ArrayList<Integer> idOrganismes = new ArrayList<Integer>();

    // Desaffectation pour tous les utilisateurs
    if (allOrganismes) {
      idOrganismes =
          Organisme.getOrganismeAndChildren(
              utilisateurService.getCurrentUtilisateur().getOrganisme().getId().intValue());
    }
    // Désaffectation pour l'organisme de l'utilisateur courant seulement
    else {
      idOrganismes.add(
          utilisateurService.getCurrentUtilisateur().getOrganisme().getId().intValue());
    }

    for (Integer item : items) {
      ids.add(Long.valueOf(item));
    }
    if (ids.size() > 0) {
      Query query;
      query =
          entityManager
              .createNativeQuery(
                  ("DELETE FROM remocra.hydrant_tournees WHERE hydrant in (:ids) AND tournees in (select id from remocra.tournee WHERE affectation in (:idOrganismes))"))
              .setParameter("ids", ids)
              .setParameter("idOrganismes", idOrganismes);
      return query.executeUpdate();
    }
    return 0;
  }

  @Transactional
  public Map<Hydrant, String> checkTournee(String json, Long idOrganisme) {
    ArrayList<Integer> items = new JSONDeserializer<ArrayList<Integer>>().deserialize(json);
    ArrayList<Long> ids = new ArrayList<Long>();
    Map<Hydrant, String> withSameOrganism = new HashMap<Hydrant, String>();
    for (Integer item : items) {
      ids.add(Long.valueOf(item));
    }
    if (ids.size() > 0) {
      Long currentOrganisme =
          (idOrganisme != null)
              ? idOrganisme
              : utilisateurService.getCurrentUtilisateur().getOrganisme().getId();
      Query query =
          entityManager
              .createNativeQuery(
                  "select (CAST (t.affectation AS INTEGER )) as affectation, t.nom as nom, th.hydrant as id"
                      + " from remocra.tournee t"
                      + " join remocra.hydrant_tournees th"
                      + " on t.id = th.tournees"
                      + " where (th.hydrant in (:ids)) order by nom")
              .setParameter("ids", ids);
      List<Object[]> tournees = query.getResultList();
      for (Object[] t : tournees) {
        if (Long.valueOf(t[0].toString()).longValue() == currentOrganisme.longValue()) {
          withSameOrganism.put(
              Hydrant.findHydrant(Long.valueOf(t[2].toString())), String.valueOf(t[1]));
        }
      }
    }
    return withSameOrganism;
  }

  /**
   * Vérifie si la tournée peut être créée Conditions (mutuellement exclusives): Seulement des PEI
   * privés OU PEI public/conventionnés
   *
   * @param json Un tableau des id des hydrants
   * @return TRUE si la tournée respecte les conditions, FALSE sinon
   */
  @Transactional
  public Boolean checkHydrantsNatureDeci(String json) {
    ArrayList<Integer> items = new JSONDeserializer<ArrayList<Integer>>().deserialize(json);
    ArrayList<Long> ids = new ArrayList<Long>();
    for (Integer item : items) {
      ids.add(Long.valueOf(item));
    }
    if (ids.size() > 0) {
      Query query =
          entityManager
              .createNativeQuery(
                  "select distinct hn.code"
                      + " from remocra.hydrant h"
                      + " join remocra.type_hydrant_nature_deci hn"
                      + " on h.nature_deci = hn.id"
                      + " where (h.id in (:ids))")
              .setParameter("ids", ids);
      ArrayList<String> naturesDECI = (ArrayList<String>) query.getResultList();
      if (naturesDECI.size() > 1 && naturesDECI.indexOf("PRIVE") != -1) {
        return false;
      } else {
        return true;
      }
    }
    return false;
  }

  public ArrayList<String> checkHydrantsDureeIndispo() {
    ArrayList<String> peis = new ArrayList<String>();
    Query query =
        entityManager
            .createNativeQuery(
                "SELECT numero "
                    + "   FROM "
                    + "     (SELECT h.numero, h.commune "
                    + "     FROM remocra.hydrant h  "
                    + "     JOIN "
                    + // Jointure pour s'assurer que les peis existent encore
                    "         (SELECT h.id_hydrant, MIN(h.date_operation) AS date_operation "
                    + // Sélection des PEIs ayant basculés de disponible à indisponible
                    "         FROM tracabilite.hydrant h "
                    + "         JOIN "
                    + "           (SELECT h.id_hydrant, max(h.date_operation) AS date_operation "
                    + "            FROM tracabilite.hydrant h "
                    + "            WHERE (dispo_terrestre <> 'INDISPO' OR dispo_terrestre IS NULL) AND (dispo_hbe <> 'INDISPO' OR dispo_hbe IS NULL) "
                    + "            GROUP BY h.id_hydrant) dispo "
                    + "         ON(dispo.id_hydrant = h.id_hydrant AND dispo.date_operation < h.date_operation) "
                    + "         WHERE dispo_terrestre = 'INDISPO' OR dispo_hbe = 'INDISPO' "
                    + "         GROUP BY h.id_hydrant, h.commune "
                    + "         UNION "
                    + "         SELECT h.id_hydrant, min(h.date_operation) AS date_operation "
                    + // Union avec les peis en indispo depuis leur déclaration
                    "         FROM tracabilite.hydrant h "
                    + "         LEFT JOIN "
                    + "           (SELECT h.id_hydrant "
                    + "            FROM tracabilite.hydrant h "
                    + "            WHERE (h.dispo_terrestre IS NULL OR h.dispo_terrestre!='INDISPO') AND (h.dispo_hbe IS NULL OR h.dispo_hbe!='INDISPO') "
                    + "            GROUP BY h.id_hydrant) AS R2 ON R2.id_hydrant=h.id_hydrant "
                    + "         WHERE R2.id_hydrant IS NULL "
                    + "         GROUP BY h.id_hydrant) AS R1 ON R1.id_hydrant=h.id "
                    + "     GROUP BY h.numero, R1.date_operation, h.commune "
                    + "     HAVING DATE_PART('day', NOW() - max(R1.date_operation)) > :dureeIndispo) as R2 "
                    + // PEIs indisponibles depuis plus d'un certain nombre de jours)
                    " WHERE commune in ( "
                    + "   SELECT zcc.commune_id "
                    + "       FROM remocra.zone_competence_commune zcc "
                    + "       WHERE zcc.zone_competence_id = :zoneCompetenceId)") // On garde les
            // PEIs contenus
            // dans la zone de
            // compétence de
            // l'utilisateur
            .setParameter(
                "dureeIndispo", parametreDataProvider.get().getHydrantLongueIndisponibiliteJours())
            .setParameter(
                "zoneCompetenceId",
                utilisateurService
                    .getCurrentUtilisateur()
                    .getOrganisme()
                    .getZoneCompetence()
                    .getId());

    peis = (ArrayList<String>) query.getResultList();
    return peis;
  }

  @Transactional
  public Map<Hydrant, String> checkReservation(String json) {
    ArrayList<Integer> items = new JSONDeserializer<ArrayList<Integer>>().deserialize(json);
    ArrayList<Long> ids = new ArrayList<Long>();
    Map<Hydrant, String> withReservation = new HashMap<Hydrant, String>();
    for (Integer item : items) {
      ids.add(Long.valueOf(item));
    }
    if (ids.size() > 0) {
      Long currentOrganisme = utilisateurService.getCurrentUtilisateur().getOrganisme().getId();
      Query query =
          entityManager
              .createNativeQuery(
                  "select t.reservation, t.nom, th.hydrant as id"
                      + " from remocra.tournee t"
                      + " join remocra.hydrant_tournees th"
                      + " on t.id = th.tournees"
                      + " where th.hydrant in (:ids) AND t.affectation =:affectation order by t.nom")
              .setParameter("ids", ids)
              .setParameter("affectation", currentOrganisme);
      List<Object[]> tournees = query.getResultList();
      for (Object[] t : tournees) {
        if (t[0] != null) {
          withReservation.put(
              Hydrant.findHydrant(Long.valueOf(t[2].toString())), String.valueOf(t[1]));
        }
      }
    }
    return withReservation;
  }

  @SuppressWarnings("unchecked")
  @Transactional
  public Integer affecter(String json) {
    HashMap<String, Object> items =
        new JSONDeserializer<HashMap<String, Object>>().deserialize(json);

    // id des hydrants
    ArrayList<Long> ids = new ArrayList<Long>();
    for (Integer item : ((ArrayList<Integer>) items.get("ids"))) {
      ids.add(Long.valueOf(item));
    }

    // id de la tournee
    Long tourneeId = null;
    Object obj = items.get("tournee");
    if (obj != null) {
      tourneeId = Long.valueOf(obj.toString());
    }
    String tourneeNom = null;
    Object nom = items.get("nom");
    if (nom != null) {
      tourneeNom = nom.toString();
    }

    Tournee tournee = null;
    if (tourneeId == null) {
      // il faut créer une tournee
      tournee = new Tournee();
      tournee.setVersion(1);
      Calendar today = Calendar.getInstance();
      today.set(Calendar.HOUR, 0);
      today.set(Calendar.MINUTE, 0);
      today.set(Calendar.SECOND, 0);
      today.set(Calendar.MILLISECOND, 0);
      tournee.setDebSync(today.getTime());
      tournee.setNom(tourneeNom);
      tournee.setEtat(0);
    } else {
      tournee = Tournee.findTournee(tourneeId);
    }

    // Si on ajoute l'hydrant à une tournée existante, on vérifie que la règle sur la nature DECI
    // est respectée
    if (tourneeId != null) {
      Query query =
          entityManager
              .createNativeQuery(
                  "select distinct h.id "
                      + " from remocra.hydrant_tournees ht"
                      + " join remocra.hydrant h on ht.hydrant=h.id"
                      + " where ht.tournees = :tourneeId")
              // .setParameter("ids", ids)
              .setParameter("tourneeId", tourneeId);
      ArrayList<Long> hydrants = (ArrayList<Long>) query.getResultList();
      hydrants.addAll(ids);
      JSONSerializer serializer = new JSONSerializer();
      boolean checkTypes = this.checkHydrantsNatureDeci(serializer.serialize(hydrants));
      if (!checkTypes) {
        return 0;
      }
    }

    Long organismeId = null;
    obj = items.get("organisme");
    if (obj != null) {
      organismeId = Long.valueOf(obj.toString());
    }

    if (organismeId != null) {
      tournee.setAffectation(Organisme.findOrganisme(organismeId));
    } else {
      tournee.setAffectation(utilisateurService.getCurrentUtilisateur().getOrganisme());
    }

    tournee.persist();
    // on a toute les infos, on crée et exécute la requête
    Query query;
    int result = 0;
    for (Long id : ids) {
      query =
          entityManager
              .createNativeQuery(
                  ("INSERT INTO remocra.hydrant_tournees(hydrant,tournees) SELECT :hydrant, :tournees"))
              .setParameter("hydrant", id)
              .setParameter("tournees", tournee.getId());
      result = result + query.executeUpdate();
    }
    return result;
  }

  public String checkDispo(
      Long id, Long nature, Long commune, Integer num, String geometrie, Long nature_deci) {
    if (num == null) {
      return null;
    }
    if (nature == null) {
      return "La nature est obligatoire";
    }
    if (commune == null) {
      return "La commune est obligatoire";
    }
    if (geometrie == null) {
      return "La geometrie est obligatoire";
    }

    // Zone spéciale
    ZoneSpeciale zs = null;
    try {

      String codeZS =
          (String)
              entityManager
                  .createNativeQuery(
                      "select code from remocra.zone_speciale "
                          + "where ST_GeomFromText(:geometrie,:srid) && geometrie and st_distance(ST_GeomFromText(:geometrie,:srid), geometrie)<=0")
                  .setParameter("geometrie", geometrie)
                  .setParameter("srid", GlobalConstants.SRID_PARAM)
                  .getSingleResult();
      zs = ZoneSpeciale.findZoneSpecialesByCode(codeZS).getSingleResult();

    } catch (Exception e) {
      //
    }

    TypeHydrantNature thn = entityManager.getReference(TypeHydrantNature.class, nature);
    String code = thn.getTypeHydrant().getCode();
    Commune c = entityManager.getReference(Commune.class, commune);
    TypeHydrantNatureDeci thnd =
        entityManager.getReference(TypeHydrantNatureDeci.class, nature_deci);

    fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant hydrantToCheckNumDispo =
        new fr.sdis83.remocra.db.model.remocra.tables.pojos.Hydrant();
    hydrantToCheckNumDispo.setZoneSpeciale((zs != null) ? zs.getId() : null);
    hydrantToCheckNumDispo.setCode(code);
    hydrantToCheckNumDispo.setNature(thn.getId());
    hydrantToCheckNumDispo.setCommune(c.getId());
    hydrantToCheckNumDispo.setNumeroInterne(num);
    hydrantToCheckNumDispo.setNatureDeci(thnd.getId());

    WKTReader fromText = new WKTReader();
    Geometry convertedGeometry = null;
    try {
      convertedGeometry = fromText.read(geometrie);
      convertedGeometry.setSRID(GlobalConstants.SRID_PARAM);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    hydrantToCheckNumDispo.setGeometrie((Geometry) convertedGeometry);

    String numero = NumeroUtilRepository.computeNumero(hydrantToCheckNumDispo);
    Long numeroUsageCount = countFindHydrantsByNumero(numero, id);
    return numeroUsageCount.longValue() > 0 ? "Le numéro " + numero + " est déjà attribué" : "";
  }

  public Long countFindHydrantsByNumero(String numero, Long id) {
    if (numero == null || numero.length() == 0)
      throw new IllegalArgumentException("The numero argument is required");
    if (id == null) throw new IllegalArgumentException("The id argument is required");
    String hQl = "SELECT COUNT(o) FROM Hydrant o WHERE o.numero = :numero";
    if (id != null) {
      hQl += " AND o.id != :id";
    }
    Query q = entityManager.createQuery(hQl);
    q.setParameter("numero", numero);
    if (id != null) {
      q.setParameter("id", id);
    }
    List<Long> results = q.getResultList();
    return results.get(0);
  }

  public List<Hydrant> findHydrantsByBBOX(String bbox) {
    TypedQuery<Hydrant> query =
        entityManager
            .createQuery(
                "SELECT o FROM Hydrant o where dwithin (geometrie, transform(:filter, :srid), 0) = true and dwithin (geometrie, :zoneCompetence, 0) = true",
                Hydrant.class)
            .setParameter("srid", GlobalConstants.SRID_PARAM)
            .setParameter("filter", GeometryUtil.geometryFromBBox(bbox))
            .setParameter(
                "zoneCompetence",
                utilisateurService
                    .getCurrentUtilisateur()
                    .getOrganisme()
                    .getZoneCompetence()
                    .getGeometrie());
    return query.getResultList();
  }

  public List<Hydrant> findAllHydrants() {
    TypedQuery<Hydrant> query =
        entityManager
            .createQuery(
                "SELECT o FROM Hydrant o where contains (:zoneCompetence, geometrie) = true",
                Hydrant.class)
            .setParameter(
                "zoneCompetence",
                utilisateurService
                    .getCurrentUtilisateur()
                    .getOrganisme()
                    .getZoneCompetence()
                    .getGeometrie());
    return query.getResultList();
  }

  /**
   * Transforme des coordonnées (tout système) en un Point géographique en Lambert 93
   *
   * @param json Un objet JSON contenant: - Le système de coordonnées (srid) - La latitude (ou X) -
   *     La longitude (ou Y) - Flag booléen "degres" indiquant si on souhaite des degrés décimaux
   *     (true) ou sexagésimaux (false)
   * @return Un point géographique
   */
  @Transactional
  public Point coordonneesToPoint(String json)
      throws BusinessException, IllegalCoordinateException, CRSException {
    HashMap<String, Object> items =
        new JSONDeserializer<HashMap<String, Object>>().deserialize(json);

    int srid = Integer.parseInt(items.get("systeme").toString());
    boolean degres = Boolean.parseBoolean(items.get("degres").toString());
    double longitude;
    double latitude;

    GeometryFactory geometryFactory =
        new GeometryFactory(new PrecisionModel(), GlobalConstants.SRID_PARAM);
    // Si les données sont déjà dans la projection du SDIS, on peut mettre à jour
    if (srid == GlobalConstants.SRID_PARAM) {
      longitude = Double.parseDouble(items.get("longitude").toString());
      latitude = Double.parseDouble(items.get("latitude").toString());

      Point p = geometryFactory.createPoint(new Coordinate(longitude, latitude));
      return p;
    } else {
      // Si les données sont en WSG84 en degrés sexagésiamaux, on les convertit d'abord en degrés
      // décimaux
      if (srid == 4326 && !degres) {
        longitude =
            GeometryUtil.convertDegresSexagesimauxToDecimaux(items.get("longitude").toString());
        latitude =
            GeometryUtil.convertDegresSexagesimauxToDecimaux(items.get("latitude").toString());
      } else {
        longitude = Double.parseDouble(items.get("longitude").toString());
        latitude = Double.parseDouble(items.get("latitude").toString());
      }
      double[] coordonneConvert =
          GeometryUtil.transformCordinate(
              longitude,
              latitude,
              items.get("systeme").toString(),
              GlobalConstants.SRID_PARAM.toString());
      longitude =
          BigDecimal.valueOf(coordonneConvert[0]).setScale(0, RoundingMode.HALF_UP).intValue();
      latitude =
          BigDecimal.valueOf(coordonneConvert[1]).setScale(0, RoundingMode.HALF_UP).intValue();
      Point p = geometryFactory.createPoint(new Coordinate(longitude, latitude));
      return p;
    }
  }

  public String getDesaffectationMesssage(String hydrants, ArrayList<Integer> organismes) {
    hydrants = hydrants.replaceAll("[\\[\\]]", "");

    StringBuilder sb = new StringBuilder();
    sb.append("<div class=\"listHydrant\">");

    if (hydrants.indexOf(',') > -1) {
      sb.append("Les points d'eau suivants vont être désaffectés de leurs tournées :");
    } else {
      sb.append("Le point d'eau suivant va être désaffecté de ses tournées :");
    }

    for (String s : hydrants.split(",")) {
      Hydrant h = Hydrant.findHydrant(Long.parseLong(s));
      ArrayList<String> nomTournees = new ArrayList<String>();
      for (Tournee t : h.getTournees()) {
        if (organismes.contains(t.getAffectation().getId().intValue())) {
          nomTournees.add(t.getNom());
        }
      }
      if (nomTournees.size() > 0) {
        String formattedString = nomTournees.toString().replace("[", "(").replace("]", ")");
        sb.append("<li>" + h.getNumero() + " <b>" + formattedString + "</b></li>");
      } else {
        sb.append("<li>" + h.getNumero() + " (<b>Aucune tournée</b>)</li>");
      }
    }
    sb.append("<br/> Pour quel(s) organisme(s) souhaitez-vous retirer ce PEI ?");
    sb.append("</div>");

    return sb.toString();
  }

  @Override
  public List<Order> makeOrders(
      Root<Hydrant> from, List<ItemSorting> itemSortings, List<ItemFilter> itemFilters) {
    ArrayList<Order> orders = new ArrayList<Order>();
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    boolean absOrderFieldName = false;
    if (itemSortings != null && !itemSortings.isEmpty()) {
      for (ItemSorting itemSorting : itemSortings) {
        if (!this.processItemSortings(orders, itemSorting, cBuilder, from)) {
          Path<?> field = from.get(itemSorting.getFieldName());
          if ("numero".equals(itemSorting.getFieldName())) {
            if (itemSorting.isDesc()) {
              orders.add(cBuilder.desc(cBuilder.length(from.get(itemSorting.getFieldName()))));
              orders.add(cBuilder.desc(field));
            } else {
              orders.add(cBuilder.asc(cBuilder.length(from.get(itemSorting.getFieldName()))));
              orders.add(cBuilder.asc(field));
            }
          }
          orders.add(itemSorting.isDesc() ? cBuilder.desc(field) : cBuilder.asc(field));
        }
        if (getAbsOrderFieldName()
            .equals(
                itemSorting
                    .getFieldName())) { // Flag si le numero ne fait pas partie des critères de tri
          absOrderFieldName = true;
        }
      }
    }
    if (!absOrderFieldName) { // Si aucun critère de tri fourni pour le numero, on trie par ordre
      // naturel
      orders.add(cBuilder.asc(cBuilder.length(from.get(getAbsOrderFieldName()))));
      orders.add(cBuilder.asc(from.get(getAbsOrderFieldName())));
    }
    return orders;
  }
}
