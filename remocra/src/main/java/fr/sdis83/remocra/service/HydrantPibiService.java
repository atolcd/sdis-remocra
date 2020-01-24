package fr.sdis83.remocra.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import flexjson.JSONDeserializer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.vividsolutions.jts.geom.Geometry;

import fr.sdis83.remocra.domain.remocra.HistoVerificationHydraulique;
import fr.sdis83.remocra.domain.remocra.HydrantPena;
import fr.sdis83.remocra.domain.remocra.HydrantPibi;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;

@Configuration
public class HydrantPibiService extends AbstractHydrantService<HydrantPibi> {

    // private final Logger logger = Logger.getLogger(getClass());

    public HydrantPibiService() {
        super(HydrantPibi.class);
    }

    @Bean
    public HydrantPibiService hydrantPibiService() {
        return new HydrantPibiService();
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<HydrantPibi> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = this.getCriteriaBuilder();
        if ("wkt".equals(itemFilter.getFieldName())) {
            Path<String> geometrie = from.get("geometrie");
            // Pour les appels à des fonctions, on remonte les instances
            // d'Expression car on en a besoin pour définir les valeurs au
            // niveau de la requête.
            ParameterExpression<Geometry> wktParam = cBuilder.parameter(Geometry.class);
            parameters.put("WKT_PARAM", wktParam);
            ParameterExpression<Integer> distParam = cBuilder.parameter(Integer.class);
            parameters.put("DIST_PARAM", distParam);
            return cBuilder.isTrue(cBuilder.function("st_dwithin", Boolean.class, geometrie, wktParam, distParam));
        } else if ("temporaire".equals(itemFilter.getFieldName())) {
            Expression<Integer> cpPath = from.get("numeroInterne");
            return cBuilder.greaterThanOrEqualTo(cpPath, 90000);
        }
        return super.processFilterItem(itemQuery, parameters, from, itemFilter);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void manageParameters(Query query, Map<String, Object> parameters, List<ItemFilter> itemFilters) {
        ItemFilter wktFilter = ItemFilter.getFilter(itemFilters, "wkt");
        if (wktFilter != null) {
            String wktValue = "SRID=2154;" + wktFilter.getValue();
            query.setParameter((Parameter) parameters.get("WKT_PARAM"), wktValue);
            query.setParameter((Parameter) parameters.get("DIST_PARAM"), paramConfService.getToleranceAssociationCiternePIMetres());
        }
    }

    @Override
    public List<Order> makeOrders(Root<HydrantPibi> from, List<ItemSorting> itemSortings, List<ItemFilter> itemFilters) {
        List<Order> orders = super.makeOrders(from, itemSortings, itemFilters);
        CriteriaBuilder cBuilder = this.getCriteriaBuilder();
        ItemFilter wktFilter = ItemFilter.getFilter(itemFilters, "wkt");
        if (wktFilter != null) {
            orders.add(cBuilder.asc(cBuilder.function("st_distance", Double.class, from.get("geometrie"), cBuilder.parameter(String.class, "wkt"))));
        }
        return orders;
    }

    @Override
    protected void processQuery(TypedQuery<?> itemTypedQuery, List<ItemSorting> itemSortings, List<ItemFilter> itemFilters, List<Order> orders, Predicate[] predicates) {
        ItemFilter wktFilter = ItemFilter.getFilter(itemFilters, "wkt");
        if (orders != null && wktFilter != null) {
            String wktValue = "SRID=2154;" + wktFilter.getValue();
            itemTypedQuery.setParameter("wkt", wktValue);
        }

    }

    @Override
    @Transactional
    public HydrantPibi setUpInformation(HydrantPibi attached, Map<String, MultipartFile> files, Object... params) throws Exception {
        if (params.length == 1 && params[0] instanceof HydrantPena) {
            HydrantPena pena = (HydrantPena) params[0];
            attached.setPena(pena);
            attached.setNumeroInterne(pena.getNumeroInterne());
            attached.setCommune(pena.getCommune());
        }
        return super.setUpInformation(attached, files, params);
    }

    @Transactional
    public HydrantPibi create(String json, Map<String, MultipartFile> files) throws Exception {
        HydrantPibi hp = super.create(json, files);
        // Pour déclencher le calcul des anomalies via trigger
        entityManager.createNativeQuery("update remocra.hydrant_pibi set debit=debit where id=:id")
                .setParameter("id", hp.getId())
                .executeUpdate();
        return hp;
    }

    @Transactional
    public HydrantPibi update(Long id, String json, Map<String, MultipartFile> files, Object... params) throws Exception {
        // Hydrant jumele actuel (peut valoir null si pas jumelé)
        HydrantPibi hydrantJumele = HydrantPibi.findHydrantPibi(id).getJumele();

        HydrantPibi hp = super.update(id, json, files, params);
        // Pour déclencher le calcul des anomalies via trigger
        entityManager.createNativeQuery("update remocra.hydrant_pibi set debit=debit where id=:id")
                .setParameter("id", id)
                .executeUpdate();

        // On supprime un jumelage
        if(hp.getJumele() == null && hydrantJumele != null)
        {
            hydrantJumele.setJumele(null);
        }
        // On passe d'un jumelage à un autre
        else if(hp.getJumele() != null && hydrantJumele != null && hp.getJumele().getId() != hydrantJumele.getId()){
            hydrantJumele.setJumele(null);
            hp.getJumele().setJumele(hp);
        }
        // Mise en place d'un jumelage
        else if(hp.getJumele() != null){
            hp.getJumele().setJumele(hp);
        }
        return hp;
    }

    public HistoVerificationHydraulique getHistoVerifHydrau(Long id) {
        try {
            Object[] result = (Object[]) entityManager.createNativeQuery("select numero, debit, debit_max, pression, pression_dyn, pression_dyn_deb, date_terrain"
            +" from (select *, greatest(date_contr, date_verif) as date_terrain from tracabilite.hydrant) as hydrant_dateterrain"
            +" where id_hydrant = "+id+" and date_terrain = ("
            +"  select max(date_terrain)"
            +"  from (select *, greatest(date_contr, date_verif) as date_terrain from tracabilite.hydrant) as hydrant_dateterrain_sr"
            +"  where id_hydrant = "+id+ "and extract(YEAR from date_terrain) < extract(YEAR from now())"
            +")"
            +"order by date_operation desc limit 1").getSingleResult();
        return new HistoVerificationHydraulique((String)result[0], (Integer)result[1] , (Integer)result[2] , (Double)result[3], (Double)result[4], (Double)result[5], (Date)result[6]);
        } catch(Exception ex) {
            //Pas d'historique
            return null;
        }
    }
    public List<Object> getHistoVerifHydrauForChart(Long id) {
        Integer limit = paramConfService.getHydrantNombreHistorique();
        if (limit != 0) {
            try {
                List<Object> l = entityManager.createNativeQuery("select * from (select distinct on (cast (date_contr as date)) date_contr as dc, debit as d, debit_max as dm, pression as p, pression_dyn as pd, pression_dyn_deb as pdd" +
                    "                    from tracabilite.hydrant h WHERE h.id_hydrant = "+id+" AND (cast (date_contr as date)IS NOT NULL) order by  (cast (date_contr as date)) desc) t  limit " + limit).getResultList();
                return l;
            } catch (Exception ex) {
                //Pas d'historique
                return null;
            }
        } else {
            return null;
        }
    }

    @Transactional
    public boolean launchTrigger(Long id) throws Exception {
        // Pour déclencher le calcul des anomalies via trigger
        entityManager.createNativeQuery("update remocra.hydrant_pibi set diametre=diametre where id=:id")
            .setParameter("id", id)
            .executeUpdate();
        return true;
   }

    /**
     * Permet de trouver tous les hydrants éligibles pour un jumelage
     * Le jumelage est possible si deux hydrants de type BI se trouvent à moins de 2 mètres l'un de l'autre
     * @param geometrie La géométrie du PEI dont on recherche les jumelages possibles
     * @param nature La nature du PEI (le jumelage requiert que les deux hydrants soient de même nature
     * @param numeroInterne Le numéro interne du PEI, afin d'exclure le PEI actuel de la recherche
     *                        On ne passe pas par l'ID car le PEI peut ne pas en avoir à ce stade (création)
     * @return Un objet JSON contenant l'identifiant et le numéro des hydrant pouvant être utilisés pour le jumelage
     */
    public JSONObject findJumelage(String geometrie, Integer nature, Integer numeroInterne){
        JSONArray data = new JSONArray();
        List<Object[]> liste = entityManager.createNativeQuery(
                    "SELECT h.id, h.numero " +
                            "FROM remocra.hydrant h " +
                            "JOIN remocra.type_hydrant_nature tn ON tn.id = h.nature " +
                            "WHERE ST_Distance(h.geometrie, :geometrie) < 2  " +
                            "AND tn.nom = 'BI' AND h.nature = :nature and h.numero_interne != :numeroInterne")
            .setParameter("geometrie", "SRID=2154;"+geometrie.toString())
            .setParameter("nature", nature)
            .setParameter("numeroInterne", numeroInterne)
            .getResultList();
        for(Object[] o : liste){
            JSONObject obj = new JSONObject();
            obj.put("id", Long.parseLong(o[0].toString()));
            obj.put("numero", o[1].toString());
            data.put(obj);
        }
        JSONObject json = new JSONObject();
        json.put("data", data);
        return json;
    }
}
