// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.RequeteModele;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect RequeteModele_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager RequeteModele.entityManager;
    
    public static final List<String> RequeteModele.fieldNames4OrderClauseFilter = java.util.Arrays.asList("id", "categorie", "code", "libelle", "description", "sourceSql");
    
    public static final EntityManager RequeteModele.entityManager() {
        EntityManager em = new RequeteModele().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long RequeteModele.countRequeteModeles() {
        return entityManager().createQuery("SELECT COUNT(o) FROM RequeteModele o", Long.class).getSingleResult();
    }
    
    public static List<RequeteModele> RequeteModele.findAllRequeteModeles() {
        return entityManager().createQuery("SELECT o FROM RequeteModele o", RequeteModele.class).getResultList();
    }
    
    public static List<RequeteModele> RequeteModele.findAllRequeteModeles(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM RequeteModele o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, RequeteModele.class).getResultList();
    }
    
    public static RequeteModele RequeteModele.findRequeteModele(Long id) {
        if (id == null) return null;
        return entityManager().find(RequeteModele.class, id);
    }
    
    public static List<RequeteModele> RequeteModele.findRequeteModeleEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM RequeteModele o", RequeteModele.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<RequeteModele> RequeteModele.findRequeteModeleEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM RequeteModele o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, RequeteModele.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void RequeteModele.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void RequeteModele.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            RequeteModele attached = RequeteModele.findRequeteModele(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void RequeteModele.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void RequeteModele.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public RequeteModele RequeteModele.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        RequeteModele merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
