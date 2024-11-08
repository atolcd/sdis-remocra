// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.CourrierDocument;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect CourrierDocument_Roo_Finder {
    
    public static Long CourrierDocument.countFindCourrierDocumentsByCodeEquals(String code) {
        if (code == null || code.length() == 0) throw new IllegalArgumentException("The code argument is required");
        EntityManager em = CourrierDocument.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM CourrierDocument AS o WHERE o.code = :code", Long.class);
        q.setParameter("code", code);
        return ((Long) q.getSingleResult());
    }
    
    public static TypedQuery<CourrierDocument> CourrierDocument.findCourrierDocumentsByCodeEquals(String code) {
        if (code == null || code.length() == 0) throw new IllegalArgumentException("The code argument is required");
        EntityManager em = CourrierDocument.entityManager();
        TypedQuery<CourrierDocument> q = em.createQuery("SELECT o FROM CourrierDocument AS o WHERE o.code = :code", CourrierDocument.class);
        q.setParameter("code", code);
        return q;
    }
    
    public static TypedQuery<CourrierDocument> CourrierDocument.findCourrierDocumentsByCodeEquals(String code, String sortFieldName, String sortOrder) {
        if (code == null || code.length() == 0) throw new IllegalArgumentException("The code argument is required");
        EntityManager em = CourrierDocument.entityManager();
        String jpaQuery = "SELECT o FROM CourrierDocument AS o WHERE o.code = :code";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<CourrierDocument> q = em.createQuery(jpaQuery, CourrierDocument.class);
        q.setParameter("code", code);
        return q;
    }
    
}
