// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.HydrantResume;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect HydrantResume_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager HydrantResume.entityManager;
    
    public static final List<String> HydrantResume.fieldNames4OrderClauseFilter = java.util.Arrays.asList("id", "version", "nom", "code", "actif");
    
    public static final EntityManager HydrantResume.entityManager() {
        EntityManager em = new HydrantResume().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long HydrantResume.countHydrantResumes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM HydrantResume o", Long.class).getSingleResult();
    }
    
    public static List<HydrantResume> HydrantResume.findAllHydrantResumes() {
        return entityManager().createQuery("SELECT o FROM HydrantResume o", HydrantResume.class).getResultList();
    }
    
    public static List<HydrantResume> HydrantResume.findAllHydrantResumes(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM HydrantResume o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, HydrantResume.class).getResultList();
    }
    
    public static HydrantResume HydrantResume.findHydrantResume(Long id) {
        if (id == null) return null;
        return entityManager().find(HydrantResume.class, id);
    }
    
    public static List<HydrantResume> HydrantResume.findHydrantResumeEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM HydrantResume o", HydrantResume.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<HydrantResume> HydrantResume.findHydrantResumeEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM HydrantResume o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, HydrantResume.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void HydrantResume.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void HydrantResume.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            HydrantResume attached = HydrantResume.findHydrantResume(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void HydrantResume.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void HydrantResume.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public HydrantResume HydrantResume.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        HydrantResume merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
