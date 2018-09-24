package fr.sdis83.remocra.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import fr.sdis83.remocra.domain.remocra.Organisme;
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
import fr.sdis83.remocra.domain.remocra.HydrantPibi;
import fr.sdis83.remocra.domain.remocra.Tournee;
import fr.sdis83.remocra.domain.remocra.TypeHydrantNature;
import fr.sdis83.remocra.domain.remocra.ZoneSpeciale;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.util.GeometryUtil;
import fr.sdis83.remocra.util.NumeroUtil;
import fr.sdis83.remocra.web.message.ItemSorting;

@Configuration
public class HydrantService extends AbstractHydrantService<Hydrant> {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    DataSource dataSource;

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

    protected boolean processItemSortings(ArrayList<Order> orders, ItemSorting itemSorting, CriteriaBuilder cBuilder,
            Root<Hydrant> from) {
        if ("tourneeId".equals(itemSorting.getFieldName())) {
            Expression<String> cpPath = from.join("tournees", JoinType.LEFT).get("id");
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
            Query query;
            Long organisme = utilisateurService.getCurrentUtilisateur().getOrganisme().getId();
            query = entityManager
                .createNativeQuery(
                    ("DELETE FROM remocra.hydrant_tournees WHERE hydrant in (:ids) AND tournees in (select t.id from remocra.tournee t where t.affectation=:organisme)"))
                .setParameter("ids", ids)
                .setParameter("organisme", organisme);
            return query.executeUpdate();
        }
        return 0;
    }

    @Transactional
    public Map<Hydrant,String> checkTournee(String json) {
        ArrayList<Integer> items = new JSONDeserializer<ArrayList<Integer>>().deserialize(json);
        ArrayList<Long> ids = new ArrayList<Long>();
        Map<Hydrant,String> withSameOrganism = new HashMap<Hydrant, String>();
        for (Integer item : items) {
            ids.add(Long.valueOf(item));
        }
        if (ids.size() > 0) {
            Long currentOrganisme = utilisateurService.getCurrentUtilisateur().getOrganisme().getId();
                Query query = entityManager.createNativeQuery("select (CAST (t.affectation AS INTEGER )) as affectation, t.nom as nom, th.hydrant as id" +
                    " from remocra.tournee t" +
                    " join remocra.hydrant_tournees th" +
                    " on t.id = th.tournees" +
                    " where (th.hydrant in (:ids)) order by nom")
                    .setParameter("ids", ids);
                List<Object[]> tournees = query.getResultList();
                for (Object[] t : tournees) {
                    if(Long.valueOf(t[0].toString()).longValue() == currentOrganisme.longValue()){
                        withSameOrganism.put(Hydrant.findHydrant(Long.valueOf(t[2].toString())),String.valueOf(t[1]));
                    }
                }

        }
        return withSameOrganism;
    }

    @Transactional
    public Map<Hydrant,String> checkReservation(String json) {
        ArrayList<Integer> items = new JSONDeserializer<ArrayList<Integer>>().deserialize(json);
        ArrayList<Long> ids = new ArrayList<Long>();
        Map<Hydrant, String> withReservation = new HashMap<Hydrant, String>();
        for (Integer item : items) {
            ids.add(Long.valueOf(item));
        }
        if (ids.size() > 0) {
            Long currentOrganisme = utilisateurService.getCurrentUtilisateur().getOrganisme().getId();
                Query query = entityManager.createNativeQuery("select t.reservation, t.nom, th.hydrant as id" +
                    " from remocra.tournee t" +
                    " join remocra.hydrant_tournees th" +
                    " on t.id = th.tournees" +
                    " where th.hydrant in (:ids) AND t.affectation =:affectation order by t.nom")
                    .setParameter("ids", ids)
                    .setParameter("affectation", currentOrganisme);
                List<Object[]> tournees = query.getResultList();
                for (Object[] t : tournees) {
                    if(t[0]!=null){
                        withReservation.put(Hydrant.findHydrant(Long.valueOf(t[2].toString())),String.valueOf(t[1]));
                    }
                }

        }
        return withReservation;
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
        tournee.setAffectation(utilisateurService.getCurrentUtilisateur().getOrganisme());
        tournee.persist();
        // on a toute les infos, on crée et exécute la requête
        Query query;
        int result = 0;
        for (Long id : ids) {
            query = entityManager
                .createNativeQuery(
                    ("INSERT INTO remocra.hydrant_tournees(hydrant,tournees) SELECT :hydrant, :tournees"))
                .setParameter("hydrant", id)
                .setParameter("tournees", tournee.getId());
            result = result + query.executeUpdate();
        }
        return result;
    }

    public String checkDispo(Long id, Long nature, Long commune, Integer num, String geometrie) {
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

            String codeZS = (String) entityManager
                    .createNativeQuery("select code from remocra.zone_speciale "
                            + "where ST_GeomFromText(:geometrie,2154) && geometrie and st_distance(ST_GeomFromText(:geometrie,2154), geometrie)<=0")
                    .setParameter("geometrie", geometrie).getSingleResult();
            zs = ZoneSpeciale.findZoneSpecialesByCode(codeZS).getSingleResult();

        } catch (Exception e) {
            //
        }

        TypeHydrantNature thn = entityManager.getReference(TypeHydrantNature.class, nature);
        String code = thn.getTypeHydrant().getCode();
        Commune c = entityManager.getReference(Commune.class, commune);

        // Hydrant et données nécessaires a minima
        Hydrant hydrantToCheckNumDispo = "PIBI".equals(code) ? new HydrantPibi() : new HydrantPena();
        hydrantToCheckNumDispo.setZoneSpeciale(zs);
        hydrantToCheckNumDispo.setCode(code);
        hydrantToCheckNumDispo.setNature(thn);
        hydrantToCheckNumDispo.setCommune(c);
        hydrantToCheckNumDispo.setNumeroInterne(num);

        String numero = NumeroUtil.computeNumero(hydrantToCheckNumDispo);

        Long numeroUsageCount = Hydrant.countFindHydrantsByNumero(numero);

        return numeroUsageCount > 0 ? "Le numéro " + numero + " est déjà attribué" : "";
    }

    public List<Hydrant> findHydrantsByBBOX(String bbox) {
        TypedQuery<Hydrant> query = entityManager
                .createQuery(
                        "SELECT o FROM Hydrant o where dwithin (geometrie, transform(:filter, 2154), 0) = true and dwithin (geometrie, :zoneCompetence, 0) = true",
                        Hydrant.class)
                .setParameter("filter", GeometryUtil.geometryFromBBox(bbox)).setParameter("zoneCompetence",
                        utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence().getGeometrie());
        return query.getResultList();
    }

    public List<Hydrant> findAllHydrants() {
        TypedQuery<Hydrant> query = entityManager
                .createQuery("SELECT o FROM Hydrant o where contains (:zoneCompetence, geometrie) = true",
                        Hydrant.class)
                .setParameter("zoneCompetence",
                        utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence().getGeometrie());
        return query.getResultList();
    }

    // Point
    @Transactional
    public void deplacer(Long id, Point point, Integer srid)
            throws CRSException, IllegalCoordinateException, BusinessException {
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
