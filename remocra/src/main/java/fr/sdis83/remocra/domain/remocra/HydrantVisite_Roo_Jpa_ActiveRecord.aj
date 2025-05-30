// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.HydrantVisite;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect HydrantVisite_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager HydrantVisite.entityManager;
    
    public static final List<String> HydrantVisite.fieldNames4OrderClauseFilter = java.util.Arrays.asList("id", "version", "nom", "code", "actif");
    
    public static final EntityManager HydrantVisite.entityManager() {
        EntityManager em = new HydrantVisite().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long HydrantVisite.countHydrantVisites() {
        return entityManager().createQuery("SELECT COUNT(o) FROM HydrantVisite o", Long.class).getSingleResult();
    }
    
    public static List<HydrantVisite> HydrantVisite.findAllHydrantVisites() {
        return entityManager().createQuery("SELECT o FROM HydrantVisite o", HydrantVisite.class).getResultList();
    }
    
    public static List<HydrantVisite> HydrantVisite.findAllHydrantVisites(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM HydrantVisite o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, HydrantVisite.class).getResultList();
    }
    
    public static HydrantVisite HydrantVisite.findHydrantVisite(Long id) {
        if (id == null) return null;
        return entityManager().find(HydrantVisite.class, id);
    }
    
    public static List<HydrantVisite> HydrantVisite.findHydrantVisiteEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM HydrantVisite o", HydrantVisite.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<HydrantVisite> HydrantVisite.findHydrantVisiteEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM HydrantVisite o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, HydrantVisite.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<HydrantVisite> HydrantVisite.findHydrantVisitesByHydrant(Long id) {
        if (id == null) return null;
        return entityManager().createQuery("SELECT o FROM HydrantVisite o WHERE hydrant="+id, HydrantVisite.class).getResultList();
    }

    
    @Transactional
    public void HydrantVisite.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void HydrantVisite.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            HydrantVisite attached = HydrantVisite.findHydrantVisite(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void HydrantVisite.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void HydrantVisite.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public HydrantVisite HydrantVisite.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        HydrantVisite merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
