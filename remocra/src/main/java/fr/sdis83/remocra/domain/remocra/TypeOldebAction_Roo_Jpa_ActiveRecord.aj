// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

privileged aspect TypeOldebAction_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager TypeOldebAction.entityManager;
    
    public static final List<String> TypeOldebAction.fieldNames4OrderClauseFilter = java.util.Arrays.asList("");
    
    public static final EntityManager TypeOldebAction.entityManager() {
        EntityManager em = new TypeOldebAction().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long TypeOldebAction.countTypeOldebActions() {
        return entityManager().createQuery("SELECT COUNT(o) FROM TypeOldebAction o", Long.class).getSingleResult();
    }
    
    public static List<TypeOldebAction> TypeOldebAction.findAllTypeOldebActions() {
        return entityManager().createQuery("SELECT o FROM TypeOldebAction o", TypeOldebAction.class).getResultList();
    }
    
    public static List<TypeOldebAction> TypeOldebAction.findAllTypeOldebActions(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TypeOldebAction o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TypeOldebAction.class).getResultList();
    }
    
    public static TypeOldebAction TypeOldebAction.findTypeOldebAction(Long id) {
        if (id == null) return null;
        return entityManager().find(TypeOldebAction.class, id);
    }
    
    public static List<TypeOldebAction> TypeOldebAction.findTypeOldebActionEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM TypeOldebAction o", TypeOldebAction.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<TypeOldebAction> TypeOldebAction.findTypeOldebActionEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TypeOldebAction o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TypeOldebAction.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void TypeOldebAction.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void TypeOldebAction.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            TypeOldebAction attached = TypeOldebAction.findTypeOldebAction(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void TypeOldebAction.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void TypeOldebAction.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public TypeOldebAction TypeOldebAction.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        TypeOldebAction merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
