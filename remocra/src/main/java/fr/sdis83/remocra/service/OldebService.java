package fr.sdis83.remocra.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.log4j.Logger;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.vividsolutions.jts.geom.Geometry;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Document.TypeDocument;
import fr.sdis83.remocra.domain.remocra.Oldeb;
import fr.sdis83.remocra.domain.remocra.OldebLocataire;
import fr.sdis83.remocra.domain.remocra.OldebProprietaire;
import fr.sdis83.remocra.domain.remocra.OldebPropriete;
import fr.sdis83.remocra.domain.remocra.OldebVisite;
import fr.sdis83.remocra.domain.remocra.OldebVisiteDocument;
import fr.sdis83.remocra.domain.remocra.OldebVisiteSuite;
import fr.sdis83.remocra.domain.remocra.TypeOldebResidence;
import fr.sdis83.remocra.domain.remocra.Utilisateur;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.util.DocumentUtil;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.deserialize.RemocraBeanObjectFactory;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;

@Configuration
public class OldebService extends AbstractService<Oldeb> {

    private final Logger logger = Logger.getLogger(getClass());
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private TypeOldebResidenceService residenceService;
    @Autowired
    protected ParamConfService paramConfService;

    public OldebService() {
        super(Oldeb.class);
    }

    @Override
    protected boolean isDistinct() {
        return true;
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<Oldeb> from, ItemFilter itemFilter) {
        // Filtrage par section
        if ("section".equals(itemFilter.getFieldName())) {
            return sectionFieldPredicate(from, itemFilter);
        } else if ("communeId".equals(itemFilter.getFieldName())) {
            return communeFieldPredicate(from, itemFilter);
        } else if ("zoneUrbanismeId".equals(itemFilter.getFieldName())) {
            return zoneUrbanismeFieldPredicate(from, itemFilter);
        } else if ("debroussaillementId".equals(itemFilter.getFieldName())) {
            return debroussaillementIdFieldPredicate(from, itemFilter);
        } else if ("avisId".equals(itemFilter.getFieldName())) {
            return avisIdFieldPredicate(from, itemFilter);
        } else if ("parcelle".equals(itemFilter.getFieldName())) {
            // Filtrage des parcelles (provient de la QUERY de l'url)
            if (itemFilter.getValue() != null) {
                return parcelleFieldPredicate(from, itemFilter);
            }
        } else if ("zoneCompetenceIdCom".equals(itemFilter.getFieldName())) {
            return zoneCompetenceWithCommunePredicate(itemQuery, from);
        } else if ("zoneCompetence".equals(itemFilter.getFieldName())) {
            return zoneCompetencePredicate(from);
        } else if ("actif".equals(itemFilter.getFieldName())) {
            return actifPredicate(from, itemFilter);
        }
        return super.processFilterItem(itemQuery, parameters, from, itemFilter);
    }

    @Override
    protected void processQuery(TypedQuery<?> itemTypedQuery, List<ItemSorting> itemSortings, List<ItemFilter> itemFilters, List<Order> orders, Predicate[] predicates) {
        ItemFilter wktFilter = ItemFilter.getFilter(itemFilters, "zoneCompetence");
        if (wktFilter != null) {
            itemTypedQuery.setParameter("zoneCompetence", utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence().getGeometrie());
            itemTypedQuery.setParameter("distanceZone", Double.valueOf(0));
        }
        if (ItemFilter.getFilter(itemFilters, "zoneCompetenceIdCom") != null) {
            itemTypedQuery.setParameter("zoneCompetenceIdCom", utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence().getGeometrie());
            itemTypedQuery.setParameter("distanceZoneIdCom", Double.valueOf(0));
        }

    }

    /**
     * Filtre suivant la zone de compétence de l'utilisateur connecté
     *
     * @param from
     * @return
     */
    private Predicate zoneCompetencePredicate(Root<Oldeb> from) {
        CriteriaBuilder cBuilder = Oldeb.entityManager().getCriteriaBuilder();
        Expression<Geometry> cpPath = from.get("geometrie");
        ParameterExpression<Geometry> zoneCompetence = cBuilder.parameter(Geometry.class, "zoneCompetence");
        ParameterExpression<Double> distanceZone = cBuilder.parameter(Double.class, "distanceZone");
        return cBuilder.equal(cBuilder.function("st_dwithin", Boolean.class, cpPath, zoneCompetence, distanceZone), Boolean.TRUE);
    }

    /**
     * Filtre suivant l'ensemble des communes incluses dans la zone de
     * compétence de l'utilisateur connecté en utilisant la commune de l'old
     *
     * @param itemQuery
     * @param from
     * @return
     */
    private Predicate zoneCompetenceWithCommunePredicate(CriteriaQuery<?> itemQuery, Root<Oldeb> from) {
        CriteriaBuilder cBuilder = this.getCriteriaBuilder();
        // On passe par la FK commune et on retient les communes incluses
        // dans la zone de compétence
        Subquery<Long> sqCommuneIds = itemQuery.subquery(Long.class);
        Root<Commune> sqCommuneIdsFrom = sqCommuneIds.from(Commune.class);

        ParameterExpression<Geometry> zoneCompetence = cBuilder.parameter(Geometry.class, "zoneCompetenceIdCom");
        ParameterExpression<Double> distanceZone = cBuilder.parameter(Double.class, "distanceZoneIdCom");
        Predicate commZCDwithinPred = cBuilder.equal(cBuilder.function("st_dwithin", Boolean.class, sqCommuneIdsFrom.get("geometrie"), zoneCompetence, distanceZone), Boolean.TRUE);
        sqCommuneIds.select(sqCommuneIdsFrom.<Long> get("id"));
        sqCommuneIds.where(commZCDwithinPred);

        return cBuilder.in(from.get("commune")).value(sqCommuneIds);
    }

    /**
     * Filtre suivant l'état
     *
     * @param from
     * @param itemFilter
     * @return
     */
    private Predicate actifPredicate(Root<Oldeb> from, ItemFilter itemFilter) {
        // Création du prédicat de recherche
        CriteriaBuilder cBuilder = Oldeb.entityManager().getCriteriaBuilder();
        Path<String> actif = from.get("actif");
        return cBuilder.equal(actif, Boolean.TRUE);
    }

    /**
     * Filtre suivant le numéro de la section
     *
     * @param from
     * @param itemFilter
     * @return
     */
    private Predicate sectionFieldPredicate(Root<Oldeb> from, ItemFilter itemFilter) {
        // Création du prédicat de recherche
        CriteriaBuilder cBuilder = Oldeb.entityManager().getCriteriaBuilder();
        Path<String> section = from.get("section");
        return cBuilder.equal(section, itemFilter.getValue());
    }

    /**
     * Filtre suivant l'id de la commune
     *
     * @param from
     * @param itemFilter
     * @return
     */
    private Predicate communeFieldPredicate(Root<Oldeb> from, ItemFilter itemFilter) {
        // Création du prédicat de recherche
        CriteriaBuilder cBuilder = Oldeb.entityManager().getCriteriaBuilder();
        Path<String> section = from.get("commune").get("id");
        Predicate equal = cBuilder.equal(section, itemFilter.getValue());
        return equal;
    }

    /**
     * Filtre d'un numéro (ou d'une partie de numéro de parcelle)
     *
     * @param from
     * @param itemFilter
     * @return
     */
    private Predicate parcelleFieldPredicate(Root<Oldeb> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = Oldeb.entityManager().getCriteriaBuilder();
        String numeroParcelle = itemFilter.getValue();
        if (numeroParcelle == null) {
            return null;
        }
        Path<String> numeroPath = from.get("parcelle");
        return cBuilder.like(cBuilder.lower(numeroPath), numeroParcelle.toLowerCase() + "%");
    }

    /**
     * Filtre sur la zone d'urbanisme via son id
     *
     * @param from
     * @param itemFilter
     * @return
     */
    private Predicate zoneUrbanismeFieldPredicate(Root<Oldeb> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = Oldeb.entityManager().getCriteriaBuilder();
        String zoneUrbanismeId = itemFilter.getValue();
        if (zoneUrbanismeId == null) {
            return null;
        }
        Path<String> zoneUrbanisme = from.get("zoneUrbanisme").get("id");
        return cBuilder.equal(zoneUrbanisme, zoneUrbanismeId);
    }

    /**
     * Filtre sur le debroussaillement via son id
     *
     * @param from
     * @param itemFilter
     * @return
     */
    private Predicate debroussaillementIdFieldPredicate(Root<Oldeb> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = Oldeb.entityManager().getCriteriaBuilder();
        String debroussaillementId = itemFilter.getValue();
        if (debroussaillementId == null) {
            return null;
        }
        Path<String> debroussaillement = from.join("oldebVisites", JoinType.LEFT).join("debroussaillementParcelle", JoinType.LEFT).get("id");
        return cBuilder.equal(debroussaillement, debroussaillementId);
    }

    /**
     * Filtre sur l'avis via son id
     *
     * @param from
     * @param itemFilter
     * @return
     */
    private Predicate avisIdFieldPredicate(Root<Oldeb> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = Oldeb.entityManager().getCriteriaBuilder();
        String avisId = itemFilter.getValue();
        if (avisId == null) {
            return null;
        }
        Path<String> avis = from.join("oldebVisites", JoinType.LEFT).join("avis", JoinType.LEFT).get("id");
        return cBuilder.equal(avis, avisId);
    }

    @Transactional
    public Oldeb create(String json, Map<String, MultipartFile> files) throws Exception {
        JSONDeserializer<Oldeb> deserializer = new JSONDeserializer<Oldeb>();
        deserializer.use(null, this.cls).use(Date.class, RemocraDateHourTransformer.getInstance()).use(Geometry.class, new GeometryFactory())
                .use("oldebProprietes.values", OldebPropriete.class).use("oldebProprietes.values.proprietaire", OldebProprietaire.class)
                .use("oldebLocataires.values", OldebLocataire.class).use("oldebVisites.values", OldebVisite.class)
                .use("oldebVisites.values.oldebVisiteSuites.values", OldebVisiteSuite.class).use(Object.class, new RemocraBeanObjectFactory(this.entityManager));

        Oldeb attached = deserializer.deserialize(json);
        this.setUpInformation(attached, files);
        OldebLocataire locataire = null;
        Utilisateur u = utilisateurService.getCurrentUtilisateur();
        OldebProprietaire proprietaire = (attached.getOldebProprietes().iterator().next().getProprietaire());
        if (!attached.getOldebLocataires().isEmpty()) {
            locataire = attached.getOldebLocataires().iterator().next();
        }

        TypeOldebResidence residence = (attached.getOldebProprietes().iterator().next().getResidence());
        ArrayList<OldebVisite> oldebVisites = new ArrayList<OldebVisite>();

        if (!attached.getOldebVisites().isEmpty()) {
            for (OldebVisite visite : attached.getOldebVisites()) {
                oldebVisites.add(visite);

            }
        }

        this.entityManager.persist(attached);
        if (attached.getId() != null) {
            if (locataire != null) {
                if (locataire.getId() == null) {
                    locataire.setOldeb(attached);
                    locataire.persist();
                }
            }
            OldebPropriete propriete = new OldebPropriete();
            propriete.setOldeb(attached);
            propriete.setProprietaire(proprietaire);
            propriete.setResidence(residence);
            propriete.persist();
            // on parcourt la liste des visites
            for (int i = 0; i < oldebVisites.size(); i++) {
                OldebVisite visite = oldebVisites.get(i);
                ArrayList<OldebVisiteSuite> oldebVisiteSuites = new ArrayList<OldebVisiteSuite>();
                // on vérifie s'il y a des suites pour la visite
                if (!visite.getOldebVisiteSuites().isEmpty()) {
                    for (OldebVisiteSuite suite : visite.getOldebVisiteSuites()) {
                        oldebVisiteSuites.add(suite);
                    }
                }
                visite.setOldeb(attached);
                if (u != null) {
                    visite.setUtilisateur(u);
                }
                // on crée la visite
                visite.persist();
                // on parcourt la liste des suites pour leur setter la visite
                // qu'on vient de créer
                if (visite.getId() != null) {
                    for (int j = 0; j < oldebVisiteSuites.size(); j++) {
                        OldebVisiteSuite oldebVisiteSuite = oldebVisiteSuites.get(j);
                        oldebVisiteSuite.setVisite(visite);
                        oldebVisiteSuite.persist();
                    }
                }
                // Ajout des nouveaux documents
                manageNewDocuments(visite, files);
                visite.merge();
            }

        }

        return attached;
    }

    @Transactional
    public Oldeb update(Long id, String json, Integer[] visiteDocumentsToDelete, Map<String, MultipartFile> files, Object... params) throws Exception {
        if (id == null) {
            return null;
        }
        Oldeb attached = this.entityManager.find(Oldeb.class, id);
        if (attached == null) {
            return null;
        }

        // Conservation des anciens documents pour les restituer si nécessaire
        // (ils ne sont pas re-postés et on a un deserializeInto juste
        // au-dessous)
        Map<Long, Set<OldebVisiteDocument>> visitesToDocuments = new TreeMap<Long, Set<OldebVisiteDocument>>();
        for (OldebVisite oldebVisite : attached.getOldebVisites()) {
            Set<OldebVisiteDocument> documents = new HashSet<OldebVisiteDocument>();
            Iterator<OldebVisiteDocument> docIterator = oldebVisite.getOldebVisiteDocuments().iterator();
            while (docIterator.hasNext()) {
                OldebVisiteDocument doc = docIterator.next();
                boolean docToDelete = false;
                for (Integer intIdDocToDelete : visiteDocumentsToDelete) {
                    if (intIdDocToDelete.equals((int) (long) doc.getId())) {
                        docToDelete = true;
                        break;
                    }
                }
                if (docToDelete) {
                    docIterator.remove();
                    deleteDocument(doc);
                } else {
                    // On conserve
                    documents.add(doc);
                }
            }
            visitesToDocuments.put(oldebVisite.getId(), documents);
        }

        JSONDeserializer<Oldeb> deserializer = new JSONDeserializer<Oldeb>();
        deserializer.use(null, this.getClass()).use(Date.class, RemocraDateHourTransformer.getInstance()).use(Geometry.class, new GeometryFactory())
                .use("oldebProprietes.values", OldebPropriete.class).use("oldebLocataires.values", OldebLocataire.class)
                .use("oldebProprietes.values.proprietaire", OldebProprietaire.class).use("oldebVisites.values", OldebVisite.class)
                .use("oldebVisites.values.oldebVisiteSuites.values", OldebVisiteSuite.class).use(Object.class, new RemocraBeanObjectFactory(this.entityManager));
        deserializer.deserializeInto(json, attached);

        /*
         * "hack" pour gérer les valeurs "null" dans le json. Flexjson les
         * ignore, et ce bug connu sera corriger dans la v3.0 cf :
         * http://sourceforge.net/p/flexjson/bugs/32/
         */

        JSONDeserializer<Map<String, Object>> deserializer2 = new JSONDeserializer<Map<String, Object>>();
        Map<String, Object> data = deserializer2.deserialize(json);
        Object nullObject = null;
        if (data != null && data.size() > 0) {
            for (String key : data.keySet()) {
                Object value = data.get(key);
                if (value == null) {
                    Method method = findSetter(attached, key);
                    if (method != null) {
                        method.invoke(attached, nullObject);
                    }
                }

            }
        }
        // Fin "hack"

        this.setUpInformation(attached, files, params);
        OldebLocataire locataire = null;
        TypeOldebResidence residence = null;
        OldebProprietaire proprietaire = null;
        OldebPropriete propriete = null;

        if (!attached.getOldebProprietes().isEmpty()) {
            residence = (attached.getOldebProprietes().iterator().next().getResidence());
            proprietaire = attached.getOldebProprietes().iterator().next().getProprietaire();
            propriete = attached.getOldebProprietes().iterator().next();

        }

        if (!attached.getOldebLocataires().isEmpty()) {
            locataire = attached.getOldebLocataires().iterator().next();
        }
        // liste des visites à mettre à jour
        ArrayList<OldebVisite> oldebVisites = new ArrayList<OldebVisite>();

        Utilisateur u = utilisateurService.getCurrentUtilisateur();
        if (!attached.getOldebVisites().isEmpty()) {
            for (OldebVisite visite : attached.getOldebVisites()) {
                oldebVisites.add(visite);

            }
        }

        Oldeb merged = this.entityManager.merge(attached);
        this.entityManager.flush();

        // mise à jour du locataire
        if (locataire != null) {
            // s'il existe dans la base
            if (locataire.getId() != null) {
                locataire.setOldeb(merged);
                locataire.merge();
            } else {
                // s'il n'existe pas
                locataire.setOldeb(merged);
                locataire.persist();
            }
        } else {
            // on le supprime
            if (attached.getOldebLocataires().size() == 0 && this.findLocatairesByOldeb(attached).size() != 0) {
                locataire = this.findLocatairesByOldeb(attached).get(0);
                locataire.remove();
            }

        }
        // mise à jour de la proprieté
        if (propriete.getId() != null) {
            propriete.setOldeb(merged);
            propriete.merge();
        } else {
            propriete.setOldeb(merged);
            propriete.persist();
        }

        // on parcourt la liste des visites à mettre à jour
        for (int i = 0; i < oldebVisites.size(); i++) {
            ArrayList<OldebVisiteSuite> oldebVisiteSuites = new ArrayList<OldebVisiteSuite>();
            // on vérifie s'il y a des suites pour la visite
            if (!oldebVisites.get(i).getOldebVisiteSuites().isEmpty()) {
                for (OldebVisiteSuite suite : oldebVisites.get(i).getOldebVisiteSuites()) {
                    // on les met dans une liste
                    oldebVisiteSuites.add(suite);
                }
            }

            oldebVisites.get(i).setOldeb(attached);
            if (u != null) {
                oldebVisites.get(i).setUtilisateur(u);
            }

            // on crée la visite ou on la met à jour
            if (oldebVisites.get(i).getId() != null) {
                oldebVisites.get(i).merge();
            } else {
                oldebVisites.get(i).persist();
            }

            // on parcourt la liste des suites à mettre à jour pour définir
            // la visite qu'on vient de créer
            for (int j = 0; j < oldebVisiteSuites.size(); j++) {
                oldebVisiteSuites.get(j).setVisite(oldebVisites.get(i));
                if (oldebVisiteSuites.get(j).getId() != null) {
                    oldebVisiteSuites.get(j).merge();
                } else {
                    oldebVisiteSuites.get(j).persist();
                }
            }

            // on compare avec les suites existantes pour enlever les suites
            // supprimées
            if (oldebVisites.get(i).getId() != null) {
                ArrayList<OldebVisiteSuite> suitesDb = (ArrayList<OldebVisiteSuite>) this.findSuitesByVisite(oldebVisites.get(i));
                for (int j = 0; j < suitesDb.size(); j++) {
                    boolean toDelete = true;
                    for (int k = 0; k < oldebVisiteSuites.size(); k++) {
                        if (suitesDb.get(j).getId().longValue() == oldebVisiteSuites.get(k).getId().longValue()) {
                            toDelete = false;
                        }
                    }
                    if (toDelete == true) {
                        suitesDb.get(j).remove();
                    }
                }
            }

            // Restitution des anciens documents
            OldebVisite visite = oldebVisites.get(i);
            if (visite.getId() != null) {
                Set<OldebVisiteDocument> docs = visitesToDocuments.get(visite.getId());
                if (docs != null) {
                    visite.getOldebVisiteDocuments().addAll(docs);
                }
            }
            // Ajout des nouveaux documents
            manageNewDocuments(visite, files);
            visite.merge();
        }

        // On supprime les visites qui n'ont plus lieu d'être et les données
        // liées
        ArrayList<OldebVisite> visitesDb = (ArrayList<OldebVisite>) this.findVisitesByOldeb(merged);
        for (int i = 0; i < visitesDb.size(); i++) {
            boolean toDelete = true;
            OldebVisite visiteDb = visitesDb.get(i);
            for (int j = 0; j < oldebVisites.size(); j++) {
                if (visiteDb.getId().longValue() == oldebVisites.get(j).getId().longValue()) {
                    toDelete = false;
                    break;
                }
            }
            if (toDelete) {
                // Suppression des suites
                Iterator<OldebVisiteSuite> suites = visiteDb.getOldebVisiteSuites().iterator();
                while (suites.hasNext()) {
                    suites.next().remove();
                }
                // Suppression des documents
                Iterator<OldebVisiteDocument> docIterator = visiteDb.getOldebVisiteDocuments().iterator();
                while (docIterator.hasNext()) {
                    OldebVisiteDocument doc = docIterator.next();
                    docIterator.remove();
                    deleteDocument(doc);
                }
                // Suppression de la visite
                visiteDb.remove();
            }
        }

        return merged;
    }

    private Method findSetter(Object obj, String fieldName) {
        String methodName = "set" + StringUtils.capitalize(fieldName);
        Method[] methods = obj.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methodName.equals(methods[i].getName())) {
                return methods[i];
            }
        }
        return null;
    }

    @Override
    @Transactional
    public Oldeb setUpInformation(Oldeb attached, Map<String, MultipartFile> files, Object... params) throws Exception {

        // Géométrie
        attached.getGeometrie().setSRID(2154);
        Geometry geom = attached.getGeometrie();
        geom = GeometryUtil.getMultiGeometry(geom);
        attached.setGeometrie(geom);
        // mise à jour du proprietaire
        if (!attached.getOldebProprietes().isEmpty()) {

            OldebProprietaire proprio = attached.getOldebProprietes().iterator().next().getProprietaire();
            if (proprio.getId() != null) {
                proprio.merge();
                attached.getOldebProprietes().iterator().next().setProprietaire(proprio);
            } else {
                proprio.persist();
                proprio.flush();
                attached.getOldebProprietes().iterator().next().setProprietaire(proprio);
            }
            // mise à jour de la residence

            if (!attached.getOldebProprietes().isEmpty()) {

                TypeOldebResidence residence = residenceService.entityManager.find(TypeOldebResidence.class,
                        attached.getOldebProprietes().iterator().next().getResidence().getId());
                if (residence.getId() != null) {
                    attached.getOldebProprietes().iterator().next().setResidence(residence);
                }

            }

        }
        return super.setUpInformation(attached, files, params);
    }

    @Override
    @Transactional
    public boolean delete(Long id) throws Exception {
        // La suppression d'une obligation est une désactivation
        Oldeb oldeb = Oldeb.findOldeb(id);
        if (oldeb.getOldebProprietes().isEmpty() && oldeb.getOldebVisites().isEmpty()) {
            oldeb.remove();
        } else {
            oldeb.setActif(false);
            oldeb.persist();
        }

        return true;
    }

    public List<Oldeb> findOldebsByPoint(String point, String projection) {
        String qlString = "select o FROM Oldeb o "
                // Si le point fait partie de la géometrie d'une Oldeb
                + "where ST_Contains(geometrie,ST_Transform(ST_SetSRID(ST_makePoint(" + point + ")," + projection + "),2154)) = true " + "and o.actif = true "
                // Et que l'oldeb est dans la zone de compétence de
                // l'utilisateur connecté
                + "and dwithin(geometrie, :zoneCompetence, 0) = true";
        TypedQuery<Oldeb> query = entityManager.createQuery(qlString, Oldeb.class).setParameter("zoneCompetence",
                utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence().getGeometrie());
        return query.getResultList();
    }

    @Transactional
    public void updateGeom(Long id, Geometry geom, int srid) throws CRSException, IllegalCoordinateException, BusinessException {
        Oldeb oldeb = Oldeb.findOldeb(id);
        if (oldeb == null) {
            BusinessException e = new BusinessException("L'obligation de débroussaillement n'existe pas en base");
            logger.error(e.getMessage());
            throw e;
        }
        geom.setSRID(srid);
        oldeb.setGeometrie(geom);
        oldeb.persist();
    }

    public List<OldebLocataire> findLocatairesByOldeb(Oldeb oldeb) {
        TypedQuery<OldebLocataire> query = entityManager.createQuery("SELECT o FROM OldebLocataire o where o.oldeb = :idOldeb", OldebLocataire.class).setParameter("idOldeb",
                oldeb);
        return query.getResultList();
    }

    public List<OldebVisite> findVisitesByOldeb(Oldeb oldeb) {
        TypedQuery<OldebVisite> query = entityManager.createQuery("SELECT o FROM OldebVisite o where o.oldeb = :idOldeb", OldebVisite.class).setParameter("idOldeb", oldeb);
        return query.getResultList();
    }

    public List<OldebVisiteSuite> findSuitesByVisite(OldebVisite visite) {
        TypedQuery<OldebVisiteSuite> query = entityManager.createQuery("SELECT o FROM OldebVisiteSuite o where o.visite = :idVisite", OldebVisiteSuite.class)
                .setParameter("idVisite", visite);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public String checkDispo(Long id, Long commune, String section, String parcelle) {
        if (commune == null || section == null || parcelle == null) {
            return "La commune, la section et la parcelle sont obligatoires";
        }

        String hQl = "select o from Oldeb o where o.commune = :commune and o.section = :section and o.parcelle = :parcelle";
        if (id != null) {
            hQl += " and o.id != :id";
        }

        Query q = entityManager.createQuery(hQl);
        Query query = q.setParameter("commune", entityManager.getReference(Commune.class, commune));
        query.setParameter("section", section);
        query.setParameter("parcelle", parcelle);
        if (id != null) {
            query.setParameter("id", id);
        }

        List<Oldeb> results = query.getResultList();
        if (results.size() > 0) {
            return "Une obligation de débroussaillement existe déjà pour cette commune, cette section et cette parcelle.";
        }
        return "";

    }

    protected void manageNewDocuments(OldebVisite visite, Map<String, MultipartFile> files) throws Exception {
        // Ajout des nouveaux documents
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files.values()) {
                if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty() || file.getName() == null || file.getName().isEmpty()) {
                    continue;
                }
                String[] groupAndFileCodes = file.getName().split("%");
                String groupCode = groupAndFileCodes.length > 0 ? groupAndFileCodes[0] : "";
                if (groupCode.equals(visite.getCode())) {
                    // le document concerne cette visite : enregistrement sur
                    // disque et base)
                    Document d = DocumentUtil.getInstance().createNonPersistedDocument(TypeDocument.OLDEBVISITE, file, paramConfService.getDossierDocOldebVisite());
                    OldebVisiteDocument ovd = new OldebVisiteDocument();
                    ovd.setVisite(visite);
                    ovd.setDocument(this.entityManager.merge(d));
                    visite.getOldebVisiteDocuments().add(ovd);
                }
            }
        }
    }

    public void deleteDocument(OldebVisiteDocument attached) throws Exception {
        // DB
        Document d = attached.getDocument();
        entityManager.remove(attached);
        entityManager.remove(d);
        entityManager.flush();
        // HD
        DocumentUtil.getInstance().deleteHDFile(d);
    }
}
