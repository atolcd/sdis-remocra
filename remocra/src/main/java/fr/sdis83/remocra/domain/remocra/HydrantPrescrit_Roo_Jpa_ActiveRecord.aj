// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.HydrantPrescrit;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect HydrantPrescrit_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager HydrantPrescrit.entityManager;
    
    public static final List<String> HydrantPrescrit.fieldNames4OrderClauseFilter = java.util.Arrays.asList("id", "geometrie", "jsonGeometrie", "organisme", "datePrescrit", "nbPoteaux", "debit", "agent", "commentaire", "numDossier");
    
    public static final EntityManager HydrantPrescrit.entityManager() {
        EntityManager em = new HydrantPrescrit().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long HydrantPrescrit.countHydrantPrescrits() {
        return entityManager().createQuery("SELECT COUNT(o) FROM HydrantPrescrit o", Long.class).getSingleResult();
    }
    
    public static List<HydrantPrescrit> HydrantPrescrit.findAllHydrantPrescrits() {
        return entityManager().createQuery("SELECT o FROM HydrantPrescrit o", HydrantPrescrit.class).getResultList();
    }
    
    public static List<HydrantPrescrit> HydrantPrescrit.findAllHydrantPrescrits(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM HydrantPrescrit o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, HydrantPrescrit.class).getResultList();
    }
    
    public static HydrantPrescrit HydrantPrescrit.findHydrantPrescrit(Long id) {
        if (id == null) return null;
        return entityManager().find(HydrantPrescrit.class, id);
    }
    
    public static List<HydrantPrescrit> HydrantPrescrit.findHydrantPrescritEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM HydrantPrescrit o", HydrantPrescrit.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<HydrantPrescrit> HydrantPrescrit.findHydrantPrescritEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM HydrantPrescrit o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, HydrantPrescrit.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void HydrantPrescrit.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void HydrantPrescrit.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            HydrantPrescrit attached = HydrantPrescrit.findHydrantPrescrit(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void HydrantPrescrit.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void HydrantPrescrit.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public HydrantPrescrit HydrantPrescrit.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        HydrantPrescrit merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
