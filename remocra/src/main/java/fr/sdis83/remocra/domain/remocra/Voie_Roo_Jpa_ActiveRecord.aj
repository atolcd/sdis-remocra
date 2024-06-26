// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.Voie;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Voie_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Voie.entityManager;
    
    public static final List<String> Voie.fieldNames4OrderClauseFilter = java.util.Arrays.asList("id", "nom", "motClassant", "geometrie", "commune", "source");
    
    public static final EntityManager Voie.entityManager() {
        EntityManager em = new Voie().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Voie.countVoies() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Voie o", Long.class).getSingleResult();
    }
    
    public static List<Voie> Voie.findAllVoies() {
        return entityManager().createQuery("SELECT o FROM Voie o", Voie.class).getResultList();
    }
    
    public static List<Voie> Voie.findAllVoies(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Voie o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Voie.class).getResultList();
    }
    
    public static Voie Voie.findVoie(Long id) {
        if (id == null) return null;
        return entityManager().find(Voie.class, id);
    }
    
    public static List<Voie> Voie.findVoieEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Voie o", Voie.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<Voie> Voie.findVoieEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Voie o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Voie.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Voie.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Voie.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Voie attached = Voie.findVoie(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Voie.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Voie.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Voie Voie.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Voie merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
