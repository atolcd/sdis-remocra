// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.OldebVisiteSuite;
import fr.sdis83.remocra.domain.remocra.OldebVisiteSuitePK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect OldebVisiteSuite_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager OldebVisiteSuite.entityManager;
    
    public static final List<String> OldebVisiteSuite.fieldNames4OrderClauseFilter = java.util.Arrays.asList("");
    
    public static final EntityManager OldebVisiteSuite.entityManager() {
        EntityManager em = new OldebVisiteSuite().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long OldebVisiteSuite.countOldebVisiteSuites() {
        return entityManager().createQuery("SELECT COUNT(o) FROM OldebVisiteSuite o", Long.class).getSingleResult();
    }
    
    public static List<OldebVisiteSuite> OldebVisiteSuite.findAllOldebVisiteSuites() {
        return entityManager().createQuery("SELECT o FROM OldebVisiteSuite o", OldebVisiteSuite.class).getResultList();
    }
    
    public static List<OldebVisiteSuite> OldebVisiteSuite.findAllOldebVisiteSuites(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM OldebVisiteSuite o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, OldebVisiteSuite.class).getResultList();
    }
    
    public static OldebVisiteSuite OldebVisiteSuite.findOldebVisiteSuite(Long id) {
        if (id == null) return null;
        return entityManager().find(OldebVisiteSuite.class, id);
    }
    
    public static List<OldebVisiteSuite> OldebVisiteSuite.findOldebVisiteSuiteEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM OldebVisiteSuite o", OldebVisiteSuite.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<OldebVisiteSuite> OldebVisiteSuite.findOldebVisiteSuiteEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM OldebVisiteSuite o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, OldebVisiteSuite.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void OldebVisiteSuite.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void OldebVisiteSuite.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            OldebVisiteSuite attached = OldebVisiteSuite.findOldebVisiteSuite(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void OldebVisiteSuite.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void OldebVisiteSuite.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public OldebVisiteSuite OldebVisiteSuite.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        OldebVisiteSuite merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
