// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.RequeteFiche;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect RequeteFiche_Roo_Finder {

    public static Long RequeteFiche.countFindRequeteFichesByCode(String code) {
        if (code == null || code.length() == 0) throw new IllegalArgumentException("The code argument is required");
        EntityManager em = RequeteFiche.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM RequeteFiche AS o WHERE o.code = :code", Long.class);
        q.setParameter("code", code);
        return ((Long) q.getSingleResult());
    }

    public static TypedQuery<RequeteFiche> RequeteFiche.findRequeteFichesByCode(String code) {
        if (code == null || code.length() == 0) throw new IllegalArgumentException("The code argument is required");
        EntityManager em = RequeteFiche.entityManager();
        TypedQuery<RequeteFiche> q = em.createQuery("SELECT o FROM RequeteFiche AS o WHERE o.code = :code", RequeteFiche.class);
        q.setParameter("code", code);
        return q;
    }

    public static TypedQuery<RequeteFiche> RequeteFiche.findRequeteFichesByCode(String code, String sortFieldName, String sortOrder) {
        if (code == null || code.length() == 0) throw new IllegalArgumentException("The code argument is required");
        EntityManager em = RequeteFiche.entityManager();
        String jpaQuery = "SELECT o FROM RequeteFiche AS o WHERE o.code = :code";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<RequeteFiche> q = em.createQuery(jpaQuery, RequeteFiche.class);
        q.setParameter("code", code);
        return q;
    }

}