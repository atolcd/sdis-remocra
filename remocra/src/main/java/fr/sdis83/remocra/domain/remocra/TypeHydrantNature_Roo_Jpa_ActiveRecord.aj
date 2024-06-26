// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.TypeHydrantNature;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect TypeHydrantNature_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager TypeHydrantNature.entityManager;
    
    public static final List<String> TypeHydrantNature.fieldNames4OrderClauseFilter = java.util.Arrays.asList("id", "nom", "code", "actif", "typeHydrant");
    
    public static final EntityManager TypeHydrantNature.entityManager() {
        EntityManager em = new TypeHydrantNature().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long TypeHydrantNature.countTypeHydrantNatures() {
        return entityManager().createQuery("SELECT COUNT(o) FROM TypeHydrantNature o", Long.class).getSingleResult();
    }
    
    public static List<TypeHydrantNature> TypeHydrantNature.findAllTypeHydrantNatures() {
        return entityManager().createQuery("SELECT o FROM TypeHydrantNature o", TypeHydrantNature.class).getResultList();
    }
    
    public static List<TypeHydrantNature> TypeHydrantNature.findAllTypeHydrantNatures(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TypeHydrantNature o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TypeHydrantNature.class).getResultList();
    }
    
    public static TypeHydrantNature TypeHydrantNature.findTypeHydrantNature(Long id) {
        if (id == null) return null;
        return entityManager().find(TypeHydrantNature.class, id);
    }
    
    public static List<TypeHydrantNature> TypeHydrantNature.findTypeHydrantNatureEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM TypeHydrantNature o", TypeHydrantNature.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<TypeHydrantNature> TypeHydrantNature.findTypeHydrantNatureEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TypeHydrantNature o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TypeHydrantNature.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void TypeHydrantNature.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void TypeHydrantNature.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            TypeHydrantNature attached = TypeHydrantNature.findTypeHydrantNature(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void TypeHydrantNature.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void TypeHydrantNature.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public TypeHydrantNature TypeHydrantNature.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        TypeHydrantNature merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
