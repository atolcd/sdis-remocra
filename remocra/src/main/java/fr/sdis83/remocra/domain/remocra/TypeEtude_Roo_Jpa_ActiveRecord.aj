// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.TypeEtude;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect TypeEtude_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager TypeEtude.entityManager;
    
    public static final List<String> TypeEtude.fieldNames4OrderClauseFilter = java.util.Arrays.asList("id", "version", "nom", "code", "actif");
    
    public static final EntityManager TypeEtude.entityManager() {
        EntityManager em = new TypeEtude().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long TypeEtude.countTypeEtudes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM TypeEtude o", Long.class).getSingleResult();
    }
    
    public static List<TypeEtude> TypeEtude.findAllTypeEtudes() {
        return entityManager().createQuery("SELECT o FROM TypeEtude o", TypeEtude.class).getResultList();
    }
    
    public static List<TypeEtude> TypeEtude.findAllTypeEtudes(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TypeEtude o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TypeEtude.class).getResultList();
    }
    
    public static TypeEtude TypeEtude.findTypeEtude(Long id) {
        if (id == null) return null;
        return entityManager().find(TypeEtude.class, id);
    }
    
    public static List<TypeEtude> TypeEtude.findTypeEtudeEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM TypeEtude o", TypeEtude.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<TypeEtude> TypeEtude.findTypeEtudeEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM TypeEtude o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, TypeEtude.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void TypeEtude.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void TypeEtude.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            TypeEtude attached = TypeEtude.findTypeEtude(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void TypeEtude.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void TypeEtude.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public TypeEtude TypeEtude.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        TypeEtude merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
