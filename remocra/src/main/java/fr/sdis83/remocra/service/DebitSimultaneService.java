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
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import flexjson.JSONSerializer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import fr.sdis83.remocra.domain.remocra.DebitSimultane;
import fr.sdis83.remocra.util.GeometryUtil;
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

    public DebitSimultaneService() {
        super(DebitSimultane.class);
    }

    @Bean
    public DebitSimultaneService DebitSimultaneService() {
        return new DebitSimultaneService();
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
                            "(SELECT ST_CENTROID(ST_UNION(geometrie)) " +
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

}