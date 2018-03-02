package fr.sdis83.remocra.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ITypeReference;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.TypeReferenceService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;

public abstract class AbstractTypeReferenceController<T extends ITypeReference> {

    private final Logger logger = Logger.getLogger(getClass());

    static protected final String ORDER_COLUMN_NAME = "code";

    @Autowired
    protected TypeReferenceService typeReferenceService;

    @PersistenceContext
    protected EntityManager entityManager;

    @SuppressWarnings("rawtypes")
    final Class cl;
    final String tableName;

    public AbstractTypeReferenceController(@SuppressWarnings("rawtypes") Class cl) {
        this.cl = cl;
        this.tableName = cl.getName();
    }

    /**
     * Nom de la colonne à utiliser pour le tri lors de la récupération des
     * données.
     * 
     * @return
     */
    public String getOrderColName() {
        return ORDER_COLUMN_NAME;
    }

    /**
     * Dans les classes filles, on ajoute les spécificités liées à la
     * sérialisation.
     * 
     * @param serializer
     * @return
     */
    public JSONSerializer decorateSerializer(JSONSerializer serializer) {
        return serializer.exclude("*.class");
    }

    /**
     * Dans les classes filles, on ajoute les spécificités liées à la
     * désérialisation.
     * 
     * @param deserializer
     * @return
     */
    public JSONDeserializer<T> decorateDeserializer(JSONDeserializer<T> deserializer) {
        return deserializer.use(null, this.cl);
    }

    @RequestMapping(headers = "Accept=application/json")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<java.lang.String> listJson(@RequestParam(value = "filter", required = false) String filters) {
        return listJsonNR(filters);
    }

    protected ResponseEntity<java.lang.String> listJsonNR(final @RequestParam(value = "filter", required = false) String filters) {

        return new AbstractExtListSerializer<T>(tableName + " retrieved.") {
            @SuppressWarnings("unchecked")
            @Override
            protected List<T> getRecords() {

                try {
                    CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
                    CriteriaQuery<T> itemQuery = cBuilder.createQuery(cl);
                    Root<T> from = itemQuery.from(cl);

                    itemQuery.select(from);

                    List<ItemSorting> sortList = new ArrayList<ItemSorting>(1);
                    sortList.add(new ItemSorting(getOrderColName(), "ACS"));
                    itemQuery.orderBy(makeOrders(from, sortList));

                    List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
                    itemQuery.where(makeFilterPredicates(from, itemFilterList));

                    TypedQuery<T> itemTypedQuery = entityManager.createQuery(itemQuery);
                    return itemTypedQuery.getResultList();

                } catch (Exception e) {
                    logger.error("Erreur lors de la lecture des " + cl.getCanonicalName(), e);
                    return null;
                }
            }

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return AbstractTypeReferenceController.this.decorateSerializer(serializer);
            }
        }.serialize();
    }

    /*
     * Boolean.parseBoolean() renvoi par défaut false. Or on ne veux pas ajouter
     * de valeur false si la valeur est mauvaise...
     */
    private Boolean parseBoolean(String value) throws ParseException {
        if ("true".equals(value.toLowerCase())) {
            return Boolean.TRUE;
        } else if ("false".equals(value.toLowerCase())) {
            return Boolean.FALSE;
        } else {
            throw new ParseException("Cannot parse '" + value + "' as boolean", 0);
        }
    }

    public Predicate[] makeFilterPredicates(Root<T> from, List<ItemFilter> itemFilters) throws ParseException {
        List<Predicate> predicateList = new ArrayList<Predicate>();
        if (itemFilters != null && !itemFilters.isEmpty()) {
            for (ItemFilter itemFilter : itemFilters) {
                // Utilisé par les ITypeReferenceNomActif (tout comme "actif")
                if ("nom".equals(itemFilter.getFieldName())) {
                    CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
                    Path<String> nom = from.get("nom");
                    Predicate like = cBuilder.like(cBuilder.lower(nom), "%" + itemFilter.getValue().toLowerCase() + "%");
                    predicateList.add(like);
                } else if ("actif".equals(itemFilter.getFieldName())) {
                    CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
                    Path<String> actif = from.get("actif");
                    Predicate equals = cBuilder.equal(actif, parseBoolean(itemFilter.getValue()));
                    predicateList.add(equals);
                } else if ("typeOrganismeId".equals(itemFilter.getFieldName())) {
                    CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
                    Path<String> typeOrganisme = from.get("typeOrganisme").get("id");
                    Predicate equals = cBuilder.equal(typeOrganisme, itemFilter.getValue());
                    predicateList.add(equals);
                }
            }
        }

        Predicate[] predicates = new Predicate[0];
        predicates = predicateList.toArray(predicates);

        return predicates;
    }

    public List<Order> makeOrders(Root<T> from, List<ItemSorting> itemSortings) {
        ArrayList<Order> orders = new ArrayList<Order>();
        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        if (itemSortings != null && !itemSortings.isEmpty()) {
            for (ItemSorting itemSorting : itemSortings) {
                orders.add("DESC".equals(itemSorting.getDirection()) ? cBuilder.desc(from.get(itemSorting.getFieldName())) : cBuilder.asc(from.get(itemSorting.getFieldName())));
            }
        }
        return orders;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS', 'CREATE')")
    public ResponseEntity<java.lang.String> updateFromJson(final @PathVariable("id") Long id, final @RequestBody String json) {
        return new AbstractExtObjectSerializer<T>(tableName + " updated.") {
            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return AbstractTypeReferenceController.this.decorateSerializer(serializer).transform(RemocraDateHourTransformer.getInstance(), Date.class);
            }

            @SuppressWarnings("unchecked")
            @Override
            protected T getRecord() throws BusinessException {
                T record = decorateDeserializer(new JSONDeserializer<T>()).use(Date.class, RemocraDateHourTransformer.getInstance()).deserialize(json);
                return (T) typeReferenceService.update(id, record);
            }
        }.serialize();
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS', 'CREATE')")
    public ResponseEntity<java.lang.String> createFromJson(final @RequestBody String json) {
        return new AbstractExtObjectSerializer<T>(tableName + " created.") {
            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return AbstractTypeReferenceController.this.decorateSerializer(serializer).transform(RemocraDateHourTransformer.getInstance(), Date.class);
            }

            @SuppressWarnings("unchecked")
            @Override
            protected T getRecord() throws BusinessException {
                T record = decorateDeserializer(new JSONDeserializer<T>()).use(Date.class, RemocraDateHourTransformer.getInstance()).deserialize(json);
                return (T) record.merge();
            }
        }.serialize();
    }
}
