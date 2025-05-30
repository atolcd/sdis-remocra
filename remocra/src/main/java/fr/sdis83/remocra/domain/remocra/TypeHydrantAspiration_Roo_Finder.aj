// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.TypeHydrantAspiration;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect TypeHydrantAspiration_Roo_Finder {

    public static Long TypeHydrantAspiration.countFindTypeHydrantAspirationsByActif(Boolean actif) {
        if (actif == null) throw new IllegalArgumentException("The actif argument is required");
        EntityManager em = TypeHydrantAspiration.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM TypeHydrantAspiration AS o WHERE o.actif = :actif", Long.class);
        q.setParameter("actif", actif);
        return ((Long) q.getSingleResult());
    }

    public static Long TypeHydrantAspiration.countFindTypeHydrantAspirationsByCode(String code) {
        if (code == null || code.length() == 0) throw new IllegalArgumentException("The code argument is required");
        EntityManager em = TypeHydrantAspiration.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM TypeHydrantAspiration AS o WHERE o.code = :code", Long.class);
        q.setParameter("code", code);
        return ((Long) q.getSingleResult());
    }

    public static TypedQuery<TypeHydrantAspiration> TypeHydrantAspiration.findTypeHydrantAspirationsByActif(Boolean actif) {
        if (actif == null) throw new IllegalArgumentException("The actif argument is required");
        EntityManager em = TypeHydrantAspiration.entityManager();
        TypedQuery<TypeHydrantAspiration> q = em.createQuery("SELECT o FROM TypeHydrantAspiration AS o WHERE o.actif = :actif", TypeHydrantAspiration.class);
        q.setParameter("actif", actif);
        return q;
    }

    public static TypedQuery<TypeHydrantAspiration> TypeHydrantAspiration.findTypeHydrantAspirationsByActif(Boolean actif, String sortFieldName, String sortOrder) {
        if (actif == null) throw new IllegalArgumentException("The actif argument is required");
        EntityManager em = TypeHydrantAspiration.entityManager();
        String jpaQuery = "SELECT o FROM TypeHydrantAspiration AS o WHERE o.actif = :actif";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<TypeHydrantAspiration> q = em.createQuery(jpaQuery, TypeHydrantAspiration.class);
        q.setParameter("actif", actif);
        return q;
    }

    public static TypedQuery<TypeHydrantAspiration> TypeHydrantAspiration.findTypeHydrantAspirationsByCode(String code) {
        if (code == null || code.length() == 0) throw new IllegalArgumentException("The code argument is required");
        EntityManager em = TypeHydrantAspiration.entityManager();
        TypedQuery<TypeHydrantAspiration> q = em.createQuery("SELECT o FROM TypeHydrantAspiration AS o WHERE o.code = :code", TypeHydrantAspiration.class);
        q.setParameter("code", code);
        return q;
    }

    public static TypedQuery<TypeHydrantAspiration> TypeHydrantAspiration.findTypeHydrantAspirationsByCode(String code, String sortFieldName, String sortOrder) {
        if (code == null || code.length() == 0) throw new IllegalArgumentException("The code argument is required");
        EntityManager em = TypeHydrantAspiration.entityManager();
        String jpaQuery = "SELECT o FROM TypeHydrantAspiration AS o WHERE o.code = :code";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<TypeHydrantAspiration> q = em.createQuery(jpaQuery, TypeHydrantAspiration.class);
        q.setParameter("code", code);
        return q;
    }

}
