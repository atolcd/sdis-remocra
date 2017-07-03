package fr.sdis83.remocra.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Point;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.remocra.HydrantPena;
import fr.sdis83.remocra.domain.remocra.Tournee;
import fr.sdis83.remocra.domain.remocra.TypeHydrantNature;
import fr.sdis83.remocra.domain.remocra.ZoneSpeciale;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.web.message.ItemSorting;

@Configuration
public class HydrantService extends AbstractHydrantService<Hydrant> {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    DataSource dataSource;

    // private final Logger logger = Logger.getLogger(getClass());

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

    protected boolean processItemSortings(ArrayList<Order> orders, ItemSorting itemSorting, CriteriaBuilder cBuilder, Root<Hydrant> from) {
        if ("tourneeId".equals(itemSorting.getFieldName())) {
            Expression<String> cpPath = from.join("tournee", JoinType.LEFT).get("id");
            orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
            return true;
        } else if ("natureNom".equals(itemSorting.getFieldName())) {
            Expression<String> cpPath = from.join("nature").get("nom");
            orders.add(itemSorting.isDesc() ? cBuilder.desc(cpPath) : cBuilder.asc(cpPath));
            return true;
        } else {
            return super.processItemSortings(orders, itemSorting, cBuilder, from);
        }
    }

    @Transactional
    public int desaffecter(String json) {
        ArrayList<Integer> items = new JSONDeserializer<ArrayList<Integer>>().deserialize(json);
        ArrayList<Long> ids = new ArrayList<Long>();
        for (Integer item : items) {
            ids.add(Long.valueOf(item));
        }
        if (ids.size() > 0) {
            String hqlUpdate = "update Hydrant h set h.tournee = null where h.id in (:ids)";
            return entityManager.createQuery(hqlUpdate).setParameter("ids", ids).executeUpdate();
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public Integer affecter(String json) {
        HashMap<String, Object> items = new JSONDeserializer<HashMap<String, Object>>().deserialize(json);

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
        } else {
            tournee = Tournee.findTournee(tourneeId);
        }
        tournee.setAffectation(utilisateurService.getCurrentUtilisateur().getOrganisme());
        tournee.persist();

        // on a toute les infos, on crée et exécute la requête
        String hqlUpdate = "update Hydrant h set h.tournee = :tournee where h.id in (:ids)";
        return entityManager.createQuery(hqlUpdate).setParameter("tournee", tournee).setParameter("ids", ids).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public String checkDispo(Long id, Long nature, Long commune, Integer num, String geometrie) {
        if (num == null) {
            return null;
        }
        if (nature == null || commune == null) {
            return "La commune, la nature sont obligatoires";
        }

        // Zone spéciale
        ZoneSpeciale zs = null;
        try {

            String codeZS = (String) entityManager
                    .createNativeQuery(
                            "select code from remocra.zone_speciale "
                                    + "where ST_GeomFromText(:geometrie,2154) && geometrie and st_distance(ST_GeomFromText(:geometrie,2154), geometrie)<=0")
                    .setParameter("geometrie", geometrie).getSingleResult();
            zs = ZoneSpeciale.findZoneSpecialesByCode(codeZS).getSingleResult();

        } catch (Exception e) {
            //
        }

        String hQl = "select h from Hydrant h where h.code = :code and h.numeroInterne = :num";
        if (id != null) {
            hQl += " and h.id != :id";
        }

        if (zs != null) {
            hQl += " and h.zoneSpeciale = :zs";
        } else {
            hQl += " and h.commune = :commune and h.zoneSpeciale is null";
        }

        Query q = entityManager.createQuery(hQl);
        Query query = q.setParameter("code", entityManager.getReference(TypeHydrantNature.class, nature).getTypeHydrant().getCode()).setParameter("num", num);

        if (id != null) {
            query.setParameter("id", id);
        }

        if (zs != null) {
            query.setParameter("zs", zs);
        } else {
            query.setParameter("commune", entityManager.getReference(Commune.class, commune));
        }

        List<Hydrant> results = query.getResultList();
        if (results.size() > 0) {
            if (zs != null) {
                return "Le numéro " + num + " est déjà attribué pour cette zone spéciale et ce type de point d'eau";
            }
            return "Le numéro " + num + " est déjà attribué pour cette commune et ce type de point d'eau";
        }
        return ""; // le numéro " + num + " est déjà affecté";
    }

    public List<Hydrant> findHydrantsByBBOX(String bbox) {
        TypedQuery<Hydrant> query = entityManager
                .createQuery("SELECT o FROM Hydrant o where dwithin (geometrie, transform(:filter, 2154), 0) = true and dwithin (geometrie, :zoneCompetence, 0) = true",
                        Hydrant.class).setParameter("filter", GeometryUtil.geometryFromBBox(bbox))
                .setParameter("zoneCompetence", utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence().getGeometrie());
        return query.getResultList();
    }

    public List<Hydrant> findAllHydrants() {
        TypedQuery<Hydrant> query = entityManager.createQuery("SELECT o FROM Hydrant o where dwithin (geometrie, :zoneCompetence, 0) = true", Hydrant.class).setParameter(
                "zoneCompetence", utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence().getGeometrie());
        return query.getResultList();
    }

    // Point
    @Transactional
    public void deplacer(Long id, Point point, Integer srid) throws CRSException, IllegalCoordinateException, BusinessException {
        Hydrant h = Hydrant.findHydrant(id);
        if (h == null) {
            BusinessException e = new BusinessException("L'hydrant n'existe pas en base");
            logger.error(e.getMessage());
            throw e;
        }
        h.setDateGps(null);
        h.setDateModification(new Date());
        point.setSRID(srid);
        h.setGeometrie(point);
        h.persist();

        if (h instanceof HydrantPena) {
            HydrantPena hp = (HydrantPena) h;
            try {
                String coordDFCI = GeometryUtil.findCoordDFCIFromGeom(dataSource, point);
                hp.setCoordDFCI(coordDFCI);
                hp.merge();
            } catch (Exception e) {
                logger.debug("Problème lors de la requête sur la table remocra_referentiel.carro_dfci", e);
            }
        }
    }

}
