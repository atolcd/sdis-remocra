// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.SuiviPatches;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect SuiviPatches_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager SuiviPatches.entityManager;
    
    public static final List<String> SuiviPatches.fieldNames4OrderClauseFilter = java.util.Arrays.asList("numero", "description", "application");
    
    public static final EntityManager SuiviPatches.entityManager() {
        EntityManager em = new SuiviPatches().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long SuiviPatches.countSuiviPatcheses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM SuiviPatches o", Long.class).getSingleResult();
    }
    
    public static List<SuiviPatches> SuiviPatches.findAllSuiviPatcheses() {
        return entityManager().createQuery("SELECT o FROM SuiviPatches o", SuiviPatches.class).getResultList();
    }
    
    public static List<SuiviPatches> SuiviPatches.findAllSuiviPatcheses(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM SuiviPatches o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, SuiviPatches.class).getResultList();
    }
    
    public static SuiviPatches SuiviPatches.findSuiviPatches(Long numero) {
        if (numero == null) return null;
        return entityManager().find(SuiviPatches.class, numero);
    }
    
    public static List<SuiviPatches> SuiviPatches.findSuiviPatchesEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM SuiviPatches o", SuiviPatches.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<SuiviPatches> SuiviPatches.findSuiviPatchesEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM SuiviPatches o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, SuiviPatches.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void SuiviPatches.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void SuiviPatches.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SuiviPatches attached = SuiviPatches.findSuiviPatches(this.numero);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void SuiviPatches.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void SuiviPatches.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public SuiviPatches SuiviPatches.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SuiviPatches merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
