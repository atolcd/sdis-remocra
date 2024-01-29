package fr.sdis83.remocra.domain.remocra;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.vividsolutions.jts.geom.Geometry;

import fr.sdis83.remocra.util.GeometryUtil;

privileged aspect Permis_Remocra_Finder {

    /**
     * Recherche autour d'un point.
     * 
     * @param srid
     *            srid du système de projection dans lequel sont exprimés x et
     *            y. Par exemple : 900913
     * @param x
     *            exemple : 661097.64023239
     * @param y
     *            exemple : 5330785.0355438
     * @param toleranceMeters
     *            tolérance en mètres. Exemple 5km : 5000
     * @param firstResult
     * @param maxResults
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Permis> Permis.findPermisByXYTolerance(int sridSource, float x, float y, int sridCible, int toleranceMeters, int firstResult, int maxResults, Geometry zoneInclusion) {
        EntityManager em = Permis.entityManager();

        Geometry point = GeometryUtil.toGeometry("POINT(" + x + " " + y + ")", sridSource);

        String sql = "SELECT o.* FROM remocra.Permis AS o WHERE ST_DWithin(st_transform(:point,  "+sridCible+"), geometrie, :tolerance) ";
        if (zoneInclusion != null) {
            sql += " AND (st_transform(:point,  "+sridCible+") && :zoneInclusion AND ST_Within(st_transform(:point,  "+sridCible+"), :zoneInclusion) )";
        }
        sql += " order by ST_Distance(st_transform(:point,  "+sridCible+"), geometrie)";

        Query q = em.createNativeQuery(sql, Permis.class);

        q.setParameter("point", point);
        q.setParameter("tolerance", toleranceMeters);
        if (zoneInclusion != null) {
            q.setParameter("zoneInclusion", zoneInclusion);
        }

        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        return q.getResultList();
    }

    public static List<Permis> Permis.findPermisByNomCommEtc(int srid, String nom, Integer commune, String numero, String sectionCadastrale, String parcelleCadastrale, Long avis,
            int firstResult, int maxResults, Geometry zoneInclusion) {
        EntityManager em = Permis.entityManager();

        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Permis> itemQuery = cBuilder.createQuery(Permis.class);
        Root<Permis> from = itemQuery.from(Permis.class);
        itemQuery.select(from);

        ArrayList<Predicate> andPredicates = new ArrayList<Predicate>(8);

        Path<String> path;

        if (nom != null) {
            path = from.get("nom");
            Predicate p = cBuilder.like(cBuilder.lower(path), "%" + nom.toLowerCase() + "%");
            andPredicates.add(p);
        }

        Path<String> pathCommune = null;
        if (commune != null) {
            pathCommune = from.get("commune");
            path = pathCommune.get("id");
            Predicate p = cBuilder.equal(path, commune);
            andPredicates.add(p);
        }

        if (numero != null) {
            path = from.get("numero");
            Predicate p = cBuilder.like(cBuilder.lower(path), "%" + numero.toLowerCase() + "%");
            andPredicates.add(p);
        }

        if (sectionCadastrale != null) {
            path = from.get("sectionCadastrale");
            Predicate p = cBuilder.like(cBuilder.lower(path), "%" + sectionCadastrale.toLowerCase() + "%");
            andPredicates.add(p);
        }

        if (parcelleCadastrale != null) {
            path = from.get("parcelleCadastrale");
            Predicate p = cBuilder.like(cBuilder.lower(path), "%" + parcelleCadastrale.toLowerCase() + "%");
            andPredicates.add(p);
        }

        if (zoneInclusion != null) {
            path = from.get("geometrie");
            ParameterExpression<Geometry> pGeom = cBuilder.parameter(Geometry.class, "zoneInclusion");
            Predicate p = cBuilder.isTrue(cBuilder.function("st_within", Boolean.class, path, pGeom));
            andPredicates.add(p);
        }

        if (avis != null) {
            // Cohérence commune.pprif / avis.pprif OU ALORS avis = En attente
            if (pathCommune==null) {
                pathCommune = from.get("commune");
            }
            Path<String> pathAvis = from.get("avis");
            andPredicates.add(cBuilder.or(
                    cBuilder.equal(pathAvis.get("pprif"), pathCommune.get("pprif")),
                    cBuilder.equal(pathAvis.get("code"), "ATTENTE")
                )
            );

            // Avis
            path = pathAvis.get("id");
            Predicate p = cBuilder.equal(path, avis);
            andPredicates.add(p);
        }

        Predicate andPredicate = cBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
        itemQuery.where(andPredicate);

        TypedQuery<Permis> itemTypedQuery = em.createQuery(itemQuery);
        if (zoneInclusion != null) {
            itemTypedQuery.setParameter("zoneInclusion", zoneInclusion);
        }
        return itemTypedQuery.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

}