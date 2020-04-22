package fr.sdis83.remocra.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import fr.sdis83.remocra.domain.remocra.DebitSimultane;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.remocra.Tournee;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.message.ItemFilter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;


@Configuration
public class DebitSimultaneService extends AbstractService<DebitSimultane>{

    @Autowired
    private UtilisateurService utilisateurService;

    @PersistenceContext
    protected EntityManager entityManager;

    private final Logger logger = Logger.getLogger(getClass());

    public DebitSimultaneService() {
        super(DebitSimultane.class);
    }

    @Bean
    public DebitSimultaneService DebitSimultaneService() {
        return new DebitSimultaneService();
    }

    @Override
    protected Predicate processFilterItem(CriteriaQuery<?> itemQuery, Map<String, Object> parameters, Root<DebitSimultane> from, ItemFilter itemFilter) {
        CriteriaBuilder cBuilder = this.getCriteriaBuilder();
        Predicate predicat = null;
        if ("id".equals(itemFilter.getFieldName())) {
            Expression<String> cpPath = from.get("id");
            predicat = cBuilder.equal(cpPath, itemFilter.getValue());
        } else if ("numDossier".equals(itemFilter.getFieldName())) {
            Expression<String> cpPath = from.get("numDossier");
            predicat = cBuilder.like(cBuilder.concat("", cpPath), itemFilter.getValue());
        } else {
            logger.info("processFilterItem non traité " + itemFilter.getFieldName() + " (" + itemFilter.getValue() + ")");
        }
        return predicat;
    }

    public List<DebitSimultane> getDebitSimultaneFromLonLat(Double lon, Double lat, Integer srid, Integer distance){
        TypedQuery<DebitSimultane> query = entityManager
                .createQuery("SELECT o FROM DebitSimultane o where st_distance(geometrie, ST_Transform(ST_SetSRID(ST_makePoint(:lon, :lat), :srid), 2154)) < :distance",
                        DebitSimultane.class)
                .setParameter("lon", lon)
                .setParameter("lat", lat)
                .setParameter("srid", srid)
                .setParameter("distance", distance);
        return query.getResultList();
    }

    @Transactional
    public void updateGeometry(Long id) {
        //DebitSimultane mesure = this.entityManager.find(this.cls, Long.valueOf(id));
        Query query = entityManager
                .createNativeQuery(
                        ("UPDATE remocra.debit_simultane SET geometrie = " +
                            "(SELECT ST_SETSRID((ST_CENTROID(ST_UNION(geometrie)),2154) " +
                            "FROM remocra.debit_simultane_hydrant dsh " +
                            "JOIN remocra.hydrant h ON h.id=dsh.hydrant " +
                            "WHERE debit = " +
                                "(SELECT dsm.id " +
                                "FROM remocra.debit_simultane ds " +
                                "JOIN remocra.debit_simultane_mesure dsm ON dsm.debit_simultane=ds.id " +
                                "WHERE ds.id=:idDebitSimultane " +
                                "ORDER BY dsm.date_mesure DESC " +
                                "LIMIT 1)) " +
                            "WHERE id=:idDebitSimultane"))
                .setParameter("idDebitSimultane", id);
        query.executeUpdate();
    }

    @Transactional
    public String checkDs(String hydrants) {
        ArrayList<Integer> ids = new JSONDeserializer<ArrayList<Integer>>().deserialize(hydrants);
        StringBuilder sb = new StringBuilder();
        List<String> hasDebitSimultane = new ArrayList<String>();
        for(Integer id : ids) {
            List<String>  numeros = entityManager.createNativeQuery("select h.numero from remocra.hydrant h join remocra.debit_simultane_hydrant" +
                " dsh on dsh.hydrant = h.id where dsh.hydrant=:id").setParameter("id",Long.valueOf(id)).getResultList();

            if(numeros.size() > 0) {
                sb.append("<li>"+ numeros.get(0)+"</li>");
                hasDebitSimultane.add(numeros.get(0));
            }
        }

        if(hasDebitSimultane.size()> 0){
            StringBuilder sf = new StringBuilder();
            if(hasDebitSimultane.size() > 1) {
                sf.append("Les points d'eau suivants sont impliqués dans un débit simultané :");
            } else {
                sf.append("Le point d'eau suivant est impliqué dans un débit simultané :");
            }
            return sf.append(sb).toString();
        }

        return null ;
    }

}
