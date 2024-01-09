package fr.sdis83.remocra.service;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.usecase.parametre.ParametreDataProvider;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.deserialize.RemocraBeanObjectFactory;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractService<T> {

  private final Logger logger = Logger.getLogger(getClass());

  @Autowired private ParametreDataProvider parametreProvider;

  protected Class<T> cls;

  public AbstractService(Class<T> cls) {
    this.cls = cls;
  }

  /**
   * Nom de la colonne utilisée pour un tri absolu.
   *
   * @return
   */
  public String getAbsOrderFieldName() {
    return "id";
  }

  protected Predicate processFilterItem(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<T> from,
      ItemFilter itemFilter) {
    logger.info(
        "processFilterItem non traité "
            + itemFilter.getFieldName()
            + " ("
            + itemFilter.getValue()
            + ")");
    return null;
  }

  @PersistenceContext protected EntityManager entityManager;

  protected CriteriaBuilder getCriteriaBuilder() {
    return this.entityManager.getCriteriaBuilder();
  }

  protected boolean processItemSortings(
      ArrayList<Order> orders, ItemSorting itemSorting, CriteriaBuilder cBuilder, Root<T> from) {
    return false;
  }

  protected void beforeDelete(T attached) {
    //
  }

  public T getById(Long id) {
    return this.entityManager.find(this.cls, id);
  }

  /**
   * Cherche les entités répondant aux critères de filtrages.
   *
   * @param firstResult
   * @param maxResults
   * @param itemSortings
   * @param itemFilters
   * @return
   */
  public List<T> find(
      Integer firstResult,
      Integer maxResults,
      List<ItemSorting> itemSortings,
      List<ItemFilter> itemFilters) {
    EntityManager em = this.entityManager;
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    CriteriaQuery<T> itemQuery = cBuilder.createQuery(this.cls);
    Root<T> from = itemQuery.from(this.cls);
    // make predicates
    Map<String, Object> parameters = new HashMap<String, Object>();
    Predicate[] predicates = this.makeFilterPredicates(itemQuery, parameters, from, itemFilters);
    // make orders
    List<Order> orders = this.makeOrders(from, itemSortings, itemFilters);
    // get the items
    itemQuery.select(from).where(predicates).orderBy(orders);
    itemQuery.distinct(isDistinct());
    TypedQuery<T> itemTypedQuery = em.createQuery(itemQuery);
    // parameters
    manageParameters(itemTypedQuery, parameters, itemFilters);
    if (firstResult != null) {
      itemTypedQuery.setFirstResult(firstResult);
    }
    if (maxResults != null) {
      itemTypedQuery.setMaxResults(maxResults);
    }
    this.processQuery(itemTypedQuery, itemSortings, itemFilters, orders, predicates);
    List<T> resultList = itemTypedQuery.getResultList();
    return resultList;
  }

  /**
   * Faire un distinct au moment de l'exécution du find
   *
   * @return false par défaut
   */
  protected boolean isDistinct() {
    return false;
  }

  protected void manageParameters(
      Query itemTypedQuery, Map<String, Object> parameters, List<ItemFilter> itemFilters) {
    // Rien à faire par défaut
  }

  protected void processQuery(
      TypedQuery<?> itemTypedQuery,
      List<ItemSorting> itemSortings,
      List<ItemFilter> itemFilters,
      List<Order> orders,
      Predicate[] predicates) {}

  /**
   * Compte les entités répondant aux critères de filtrage.
   *
   * @param itemFilters
   * @return
   */
  public Long count(List<ItemFilter> itemFilters) {
    EntityManager em = this.entityManager;
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    CriteriaQuery<Long> totalQuery = cBuilder.createQuery(Long.class);
    Root<T> from = totalQuery.from(this.cls);
    // make predicates
    Map<String, Object> parameters = new HashMap<String, Object>();
    Predicate[] predicates = this.makeFilterPredicates(totalQuery, parameters, from, itemFilters);
    // get total number of items.
    totalQuery.select(isDistinct() ? cBuilder.countDistinct(from) : cBuilder.count(from));
    totalQuery.where(predicates);
    TypedQuery<Long> longTypedQuery = em.createQuery(totalQuery);
    // parameters
    manageParameters(longTypedQuery, parameters, itemFilters);
    this.processQuery(longTypedQuery, null, itemFilters, null, predicates);
    return longTypedQuery.getSingleResult();
  }

  public List<Order> makeOrders(
      Root<T> from, List<ItemSorting> itemSortings, List<ItemFilter> itemFilters) {
    ArrayList<Order> orders = new ArrayList<Order>();
    CriteriaBuilder cBuilder = this.getCriteriaBuilder();
    boolean absOrderFieldName = false;
    if (itemSortings != null && !itemSortings.isEmpty()) {
      for (ItemSorting itemSorting : itemSortings) {
        if (!this.processItemSortings(orders, itemSorting, cBuilder, from)) {
          Path<?> field = from.get(itemSorting.getFieldName());
          orders.add(itemSorting.isDesc() ? cBuilder.desc(field) : cBuilder.asc(field));
        }
        if (getAbsOrderFieldName().equals(itemSorting.getFieldName())) {
          absOrderFieldName = true;
        }
      }
    }
    if (!absOrderFieldName) {
      orders.add(cBuilder.asc(from.get(getAbsOrderFieldName())));
    }
    return orders;
  }

  public Predicate[] makeFilterPredicates(
      CriteriaQuery<?> itemQuery,
      Map<String, Object> parameters,
      Root<T> from,
      List<ItemFilter> itemFilters) {
    List<Predicate> predicateList = new ArrayList<Predicate>();
    if (itemFilters != null && !itemFilters.isEmpty()) {
      for (ItemFilter itemFilter : itemFilters) {
        Predicate predicat = this.processFilterItem(itemQuery, parameters, from, itemFilter);
        if (predicat != null) {
          predicateList.add(predicat);
        }
      }
    }
    return predicateList.toArray(new Predicate[0]);
  }

  /**
   * Méthode générique de création de prédicat via une close "like" sur un fied d'une entité
   *
   * @param from
   * @param itemFilter
   * @param fieldName
   * @return
   */
  protected Predicate makeLikeFilterPredicate(
      Root<T> from, ItemFilter itemFilter, String fieldName) {
    CriteriaBuilder cBuilder = getCriteriaBuilder();
    String filter = itemFilter.getValue();
    if (filter == null) {
      return null;
    }
    Path<String> fieldPath = from.get(fieldName);
    return cBuilder.like(cBuilder.lower(fieldPath), "%" + filter.toLowerCase() + "%");
  }

  @Transactional
  public T create(String json, Map<String, MultipartFile> files) throws Exception {
    JSONDeserializer<T> deserializer = new JSONDeserializer<T>();
    deserializer
        .use(null, this.cls)
        .use(Date.class, RemocraDateHourTransformer.getInstance())
        .use(Geometry.class, new GeometryFactory())
        .use(Object.class, new RemocraBeanObjectFactory(this.entityManager));

    T attached = deserializer.deserialize(json);
    this.setUpInformation(attached, files, parametreProvider.get().getSridInt());
    this.entityManager.persist(attached);
    return attached;
  }

  @Transactional
  public T update(Long id, String json, Map<String, MultipartFile> files, Object... params)
      throws Exception {
    if (id == null) {
      return null;
    }
    T attached = this.entityManager.find(this.cls, id);
    if (attached == null) {
      return null;
    }
    JSONDeserializer<T> deserializer = new JSONDeserializer<T>();
    deserializer
        .use(null, this.cls)
        .use(Date.class, RemocraDateHourTransformer.getInstance())
        .use(Geometry.class, new GeometryFactory())
        .use(Object.class, new RemocraBeanObjectFactory(this.entityManager));
    deserializer.deserializeInto(json, attached);

    /*
     * "hack" pour gérer les valeurs "null" dans le json. Flexjson les
     * ignore, et ce bug connu sera corriger dans la v3.0 cf :
     * http://sourceforge.net/p/flexjson/bugs/32/
     */
    JSONDeserializer<Map<String, Object>> deserializer2 =
        new JSONDeserializer<Map<String, Object>>();
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

    this.setUpInformation(attached, files, parametreProvider.get().getSridInt(), params);
    T merged = this.entityManager.merge(attached);
    this.entityManager.flush();
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

  @Transactional
  public T setUpInformation(T attached, Map<String, MultipartFile> files, Object... params)
      throws Exception {
    return attached;
  }

  @Transactional
  public boolean delete(Long id) throws Exception {
    T attached = this.entityManager.find(this.cls, id);
    this.beforeDelete(attached);
    this.entityManager.remove(attached);
    this.entityManager.flush();
    return true;
  }
}
