// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.TypeHydrantNiveau;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect TypeHydrantNiveau_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager TypeHydrantNiveau.entityManager;
    
    public static final List<String> TypeHydrantNiveau.fieldNames4OrderClauseFilter = java.util.Arrays.asList("id", "version", "nom", "code", "actif");
    
    public static final EntityManager TypeHydrantNiveau.entityManager() {
        EntityManager em = new TypeHydrantNiveau().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long TypeHydrantNiveau.countTypeHydrantNiveaus() {
        return entityManager().createQuery("SELECT COUNT(o) FROM TypeHydrantNiveau o", Long.class).getSingleResult();
    }
    
    public static List<TypeHydrantNiveau> TypeHydrantNiveau.findAllTypeHydrantNiveaus() {
        return entityManager().createQuery("SELECT o FROM TypeHydrantNiveau o", TypeHydrantNiveau.class).getResultList();
    }
    
    public static List<TypeHydrantNiveau> TypeHydrantNiveau.findAllTypeHydrantNiveaus(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TypeHydrantNiveau o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TypeHydrantNiveau.class).getResultList();
    }
    
    public static TypeHydrantNiveau TypeHydrantNiveau.findTypeHydrantNiveau(Long id) {
        if (id == null) return null;
        return entityManager().find(TypeHydrantNiveau.class, id);
    }
    
    public static List<TypeHydrantNiveau> TypeHydrantNiveau.findTypeHydrantNiveauEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM TypeHydrantNiveau o", TypeHydrantNiveau.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<TypeHydrantNiveau> TypeHydrantNiveau.findTypeHydrantNiveauEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TypeHydrantNiveau o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TypeHydrantNiveau.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void TypeHydrantNiveau.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void TypeHydrantNiveau.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            TypeHydrantNiveau attached = TypeHydrantNiveau.findTypeHydrantNiveau(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void TypeHydrantNiveau.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void TypeHydrantNiveau.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public TypeHydrantNiveau TypeHydrantNiveau.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        TypeHydrantNiveau merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}