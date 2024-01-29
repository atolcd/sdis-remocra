package fr.sdis83.remocra.domain.remocra;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

privileged aspect Commune_Remocra_Finder {

    private static final Logger log = Logger.getLogger(Commune.class);

    @SuppressWarnings("unchecked")
    public static List<Commune> Commune.findCommunesByPoint(int sridSource, String wktPoint, int sridCible) {
        return findCommunesByPointWithDelta(sridSource, wktPoint, sridCible, 0L);
    }

    @SuppressWarnings("unchecked")
    public static List<Commune> Commune.findCommunesByPointWithDelta(int sridSource, String wktPoint, int sridCible, Long delta) {
        WKTReader fromText = new WKTReader();
        Geometry filter = null;
        try {
            filter = fromText.read(wktPoint);
            filter.setSRID(sridSource);
        } catch (ParseException e) {
            throw new RuntimeException("Not a WKT string:" + wktPoint);
        }

        // Communes filtrées si besoin (exemple pour le VAR par défaut)
        String dep = null;
        String cle = ParamConf.ParamConfParam.COMMUNES_INSEE_LIKE_FILTRE_SQL.getCle();
        try {
            ParamConf pc = ParamConf.findParamConfsByCleEquals(cle).getSingleResult();
            dep = pc.getValeur();
        } catch (Exception e) {
            log.error("Paramètre " + cle + ", restitution de la valeur par défaut", e);
        }
        if (dep == null) {
            dep = "83%";
        }
        if (dep.trim().isEmpty()) {
            dep = "%"; // tout
        }
        String depClause = "c.insee like '" + dep + "' and ";

        EntityManager em = Commune.entityManager();
        Query query = em.createQuery("select c from Commune c where "
            + depClause
            + " dwithin(c.geometrie, transform(:filter, "+sridCible+"), :delta) = true order by st_distance(c.geometrie, transform(:filter,  "+sridCible+")) asc",
            Commune.class);
        query.setParameter("filter", filter);
        query.setParameter("delta", delta);

        return query.getResultList();
    }
}