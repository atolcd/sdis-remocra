package fr.sdis83.remocra.service;

import static java.lang.Boolean.TRUE;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Document.TypeDocument;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.remocra.HydrantDocument;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.util.NumeroUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public abstract class AbstractHydrantService<T extends Hydrant> extends AbstractService<T> {

  private final Logger logger = Logger.getLogger(getClass());

  @Autowired protected ParametreDataProvider parametreProvider;

  @Autowired private UtilisateurService utilisateurService;

  @Autowired private IndisponibiliteTemporaireService indisponibiliteTemporaireService;

  // private final Logger logger = Logger.getLogger(getClass());

  public AbstractHydrantService(Class<T> cls) {
    super(cls);
  }

  @Override
  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<T> from,
      ItemFilter itemFilter) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    if ("tournee".equals(itemFilter.getFieldName())) {
      Expression<Integer> cpPath = from.join("tournees").get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());

    } else if ("zoneCompetenceIdCom".equals(itemFilter.getFieldName())) {
      // On passe par la FK commune et on retient les communes incluses
      // dans la zone de compétence
      Subquery<Long> sqCommuneIds = itemQuery.subquery(Long.class);
      Root<Commune> sqCommuneIdsFrom = sqCommuneIds.from(Commune.class);

      ParameterExpression<Geometry> zoneCompetence =
          cBuilder.parameter(Geometry.class, "zoneCompetenceIdCom");
      ParameterExpression<Double> distanceZone =
          cBuilder.parameter(Double.class, "distanceZoneIdCom");
      Predicate commZCDwithinPred =
          cBuilder.equal(
              cBuilder.function(
                  "st_dwithin",
                  Boolean.class,
                  sqCommuneIdsFrom.get("geometrie"),
                  zoneCompetence,
                  distanceZone),
              TRUE);
      sqCommuneIds.select(sqCommuneIdsFrom.<Long>get("id"));
      sqCommuneIds.where(commZCDwithinPred);

      predicat = cBuilder.in(from.get("commune")).value(sqCommuneIds);

    } else if ("zoneCompetence".equals(itemFilter.getFieldName())) {
      Expression<Geometry> cpPath = from.get("geometrie");
      ParameterExpression<Geometry> zoneCompetence =
          cBuilder.parameter(Geometry.class, "zoneCompetence");
      predicat =
          cBuilder.equal(
              cBuilder.function("st_contains", Boolean.class, zoneCompetence, cpPath), TRUE);
    } else if ("zoneCompetenceSimplified".equals(itemFilter.getFieldName())) {
      Expression<Geometry> cpPath = from.get("geometrie");
      ParameterExpression<Geometry> zoneCompetence =
          cBuilder.parameter(Geometry.class, "zoneCompetenceSimplified");
      predicat =
          cBuilder.equal(
              cBuilder.function("st_contains", Boolean.class, zoneCompetence, cpPath), TRUE);

    } else if ("id".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("numero".equals(itemFilter.getFieldName())
        || "query".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("numero");
      predicat =
          cBuilder.like(cpPath, "%" + itemFilter.getValue().toUpperCase(Locale.FRANCE) + "%");
    } else if ("numeroEqual".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("numero");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue().toUpperCase(Locale.FRANCE));
    } else if ("dateReco".equals(itemFilter.getFieldName())) {
      Expression<Date> cpPath = from.get("dateReco");
      Integer nbMonths = Integer.valueOf(itemFilter.getValue());
      Expression<Character> cpNatureDECI = from.get("natureDeci");
      DateTime datePrive =
          new DateTime()
              .minus(Period.days(parametreProvider.get().getHydrantRenouvellementRecoPrive()));
      DateTime datePublic =
          new DateTime()
              .minus(Period.days(parametreProvider.get().getHydrantRenouvellementRecoPublic()));
      predicat = this.getDatePredicat(cpPath, nbMonths, cpNatureDECI, datePrive, datePublic);
    } else if ("dateContr".equals(itemFilter.getFieldName())) {
      Expression<Date> cpPath = from.get("dateContr");
      Integer nbMonths = Integer.valueOf(itemFilter.getValue());
      Expression<Character> cpNatureDECI = from.get("natureDeci");
      DateTime datePrive =
          new DateTime()
              .minus(Period.days(parametreProvider.get().getHydrantRenouvellementCtrlPrive()));
      DateTime datePublic =
          new DateTime()
              .minus(Period.days(parametreProvider.get().getHydrantRenouvellementCtrlPublic()));
      predicat = this.getDatePredicat(cpPath, nbMonths, cpNatureDECI, datePrive, datePublic);
    } else if ("nature".equals(itemFilter.getFieldName())) {
      Expression<Integer> cpPath = from.join("nature").get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("naturecode".equals(itemFilter.getFieldName())) {
      // Exemples de valeur : 'PI,PA' ou 'PI' ou ''
      List<String> codes = Arrays.asList(itemFilter.getValue().split(","));
      Expression<Integer> cpPath = from.join("nature").get("code");
      predicat = cpPath.in(codes);
    } else if ("dispoTerrestre".equals(itemFilter.getFieldName())) {
      Expression<Character> cpPath = from.get("dispoTerrestre");
      predicat = cBuilder.equal(cpPath, Hydrant.Disponibilite.valueOf(itemFilter.getValue()));
    } else if ("dispoHbe".equals(itemFilter.getFieldName())) {
      TypedQuery<Long> itemTypedQuery =
          this.entityManager.createQuery(
              "SELECT distinct(id) FROM HydrantPena WHERE hbe=true", Long.class);
      List<Long> resultList = itemTypedQuery.getResultList();
      Expression<Integer> cpID = from.get("id");
      Expression<Character> cpDispo = from.get("dispoHbe");
      predicat =
          !resultList.isEmpty()
              ? cBuilder.and(
                  cpID.in(resultList),
                  cBuilder.equal(cpDispo, Hydrant.Disponibilite.valueOf(itemFilter.getValue())))
              : predicat;
    } else if ("nomCommune".equals(itemFilter.getFieldName())) {
      /**
       * A cause de l'utilisation hybride du champ commune, le serveur peut récupérer deux données
       * différentes sur le même path selon le type de donnée fourni : - Un nombre entier
       * (identifiant) lors d'une sélection dans la combo - Une chaîne de caratères (nom) lors de la
       * saisie de texte dans la combo
       */
      if (StringUtils.isNumeric(itemFilter.getValue())) {
        Expression<Integer> cpPath = from.join("commune").get("id");
        predicat = cBuilder.equal(cpPath, itemFilter.getValue());
      } else {
        Expression<String> cpPath = from.join("commune", JoinType.LEFT).get("nom");
        predicat =
            cBuilder.like(
                cBuilder.upper(cpPath),
                "%" + itemFilter.getValue().toUpperCase(Locale.FRANCE) + "%");
      }
    } else if ("nomNatureDeci".equals(itemFilter.getFieldName())) {
      Expression<Integer> cpPath = from.join("natureDeci").get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("codeNatureDeci".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("natureDeci").get("code");
      predicat =
          cBuilder.like(cBuilder.upper(cpPath), itemFilter.getValue().toUpperCase(Locale.FRANCE));
    } else if ("adresse".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("adresse");
      predicat =
          cBuilder.like(
              cBuilder.upper(cpPath), "%" + itemFilter.getValue().toUpperCase(Locale.FRANCE) + "%");
    } else if ("numeroInterne".equals(itemFilter.getFieldName())) {
      Expression<String> cpPath = from.get("numeroInterne");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("site".equals(itemFilter.getFieldName())) {
      Expression<Integer> cpPath = from.join("site").get("id");
      predicat = cBuilder.equal(cpPath, itemFilter.getValue());
    } else if ("near".equals(itemFilter.getFieldName())) {
      TypedQuery<Long> itemTypedQuery =
          this.entityManager.createQuery(
              "select distinct(h.id) "
                  + "from Hydrant h "
                  + "where ST_Distance(h.geometrie, st_pointFromText('"
                  + itemFilter.getValue().toString()
                  + "',"
                  + parametreProvider.get().getSridInt()
                  + ")) < 500",
              Long.class);
      List<Long> resultList = itemTypedQuery.getResultList();
      Expression<Integer> idHydrant = from.get("id");
      predicat = !resultList.isEmpty() ? idHydrant.in(resultList) : predicat;
    } else if ("debitSimultane".equals(itemFilter.getFieldName())) {
      TypedQuery<Long> itemTypedQuery =
          this.entityManager.createQuery(
              "select distinct(h.id) "
                  + "from Hydrant h where h.id not in (select dsh.hydrant from DebitSimultaneHydrant dsh) ",
              Long.class);
      List<Long> resultList = itemTypedQuery.getResultList();
      Expression<Integer> idHydrant = from.get("id");
      predicat = !resultList.isEmpty() ? idHydrant.in(resultList) : predicat;
    } else {
      return super.processFilterItem(itemQuery, parameters, from, itemFilter);
    }
    return predicat;
  }

  @Override
  protected void processQuery(
      TypedQuery<?> itemTypedQuery,
      List<ItemSorting> itemSortings,
      List<ItemFilter> itemFilters,
      List<Order> orders,
      Predicate[] predicates) {
    if (ItemFilter.getFilter(itemFilters, "zoneCompetence") != null) {
      itemTypedQuery.setParameter(
          "zoneCompetence",
          utilisateurService
              .getCurrentUtilisateur()
              .getOrganisme()
              .getZoneCompetence()
              .getGeometrie());
    }
    if (ItemFilter.getFilter(itemFilters, "zoneCompetenceSimplified") != null) {
      itemTypedQuery.setParameter(
          "zoneCompetenceSimplified",
          GeometryUtil.simplifyGeometry(
              utilisateurService
                  .getCurrentUtilisateur()
                  .getOrganisme()
                  .getZoneCompetence()
                  .getGeometrie()));
    }
    if (ItemFilter.getFilter(itemFilters, "zoneCompetenceIdCom") != null) {
      itemTypedQuery.setParameter(
          "zoneCompetenceIdCom",
          utilisateurService
              .getCurrentUtilisateur()
              .getOrganisme()
              .getZoneCompetence()
              .getGeometrie());
      itemTypedQuery.setParameter("distanceZoneIdCom", Double.valueOf(0));
    }
  }

  @Transactional
  @Override
  public T setUpInformation(T attached, Map<String, MultipartFile> files, Object... params)
      throws Exception {
    // traitement géométrie
    attached.getGeometrie().setSRID(parametreProvider.get().getSridInt());

    // date de modification
    attached.setDateModification(new Date());

    // traitement des fichiers
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files.values()) {
        if (!file.isEmpty()) {
          Document d =
              DocumentUtil.getInstance()
                  .createNonPersistedDocument(
                      TypeDocument.HYDRANT, file, parametreProvider.get().getDossierDocHydrant());
          HydrantDocument hd = new HydrantDocument();
          hd.setHydrant(attached);
          hd.setDocument(this.entityManager.merge(d));
          // Si on est sur un élément photo
          if (Hydrant.TITRE_PHOTO.equals(hd.getTitre())) {
            // Suppression de l'ancienne
            HydrantDocument toDetach = attached.getPhoto();
            if (toDetach != null) {
              attached.getHydrantDocuments().remove(toDetach);
            }
          }
          attached.getHydrantDocuments().add(hd);
        }
      }
    }

    // On redéfinit le code, la zone spéciale éventuelle, le numéro interne et le numéro si
    // nécessaire (valable dans le cas de mise à jour)
    boolean hasNumero =
        attached.getNumeroInterne() != null && attached.getNumeroInterne().intValue() > 0;
    Boolean numeroter = !hasNumero || parametreProvider.get().getHydrantRenumerotationActivation();
    if (numeroter || attached.getId() == null) {
      NumeroUtil.setCodeZoneSpecAndNumeros(attached);
    }
    // On attache l'organisme de l'utilisateur courant
    attached.setOrganisme(utilisateurService.getCurrentUtilisateur().getOrganisme());
    attached.setUtilisateurModification(utilisateurService.getCurrentUtilisateur());

    return attached;
  }

  @Override
  protected void beforeDelete(T attached) {

    try {
      indisponibiliteTemporaireService.deleteIfEmpty(attached.getId());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    for (HydrantDocument hydrD : attached.getHydrantDocuments()) {
      try {
        deleteDocument(hydrD.getId());
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
    attached.setUtilisateurModification(utilisateurService.getCurrentUtilisateur());
    attached.merge();
    attached.flush();
    super.beforeDelete(attached);
  }

  public void deleteDocument(Long id) throws Exception {
    HydrantDocument attached = entityManager.find(HydrantDocument.class, id);
    Document d = attached.getDocument();

    // Ici, le fait de supprimer le Document provoque la suppression du
    // HydrantDocument en cascade
    entityManager.remove(d);
    entityManager.flush();

    // Nettoyage du disque
    DocumentUtil.getInstance().deleteHDFile(d);
  }

  private Predicate getDatePredicat(
      Expression<Date> cpPath,
      int nbMonths,
      Expression<Character> cpNatureDECI,
      DateTime datePrive,
      DateTime datePublic) {
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    Predicate predicat = null;
    TypedQuery<Long> itemTypedQuery =
        this.entityManager.createQuery(
            "SELECT distinct(id) FROM TypeHydrantNatureDeci WHERE code='PRIVE'", Long.class);
    List<Long> resultList = itemTypedQuery.getResultList();

    if (nbMonths > 0) {
      datePrive = datePrive.plus(Period.months(nbMonths));
      datePublic = datePublic.plus(Period.months(nbMonths));
    }

    if (nbMonths != 0) {
      predicat =
          !resultList.isEmpty()
              ? cBuilder.or(
                  cBuilder.and(
                      cpNatureDECI.in(resultList),
                      cBuilder.or(
                          cBuilder.isNull(cpPath),
                          cBuilder.lessThanOrEqualTo(cpPath, datePrive.toDate()))),
                  cBuilder.and(
                      cpNatureDECI.in(resultList).not(),
                      cBuilder.or(
                          cBuilder.isNull(cpPath),
                          cBuilder.lessThanOrEqualTo(cpPath, datePublic.toDate()))))
              : cBuilder.or(
                  cBuilder.isNull(cpPath), cBuilder.lessThanOrEqualTo(cpPath, datePublic.toDate()));
    }
    return predicat;
  }
}
