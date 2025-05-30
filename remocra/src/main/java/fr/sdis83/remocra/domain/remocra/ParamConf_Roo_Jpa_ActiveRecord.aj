// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.ParamConf;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect ParamConf_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager ParamConf.entityManager;
    
    public static final List<String> ParamConf.fieldNames4OrderClauseFilter = java.util.Arrays.asList("cle", "valeur", "description", "nomgroupe");
    
    public static final EntityManager ParamConf.entityManager() {
        EntityManager em = new ParamConf().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long ParamConf.countParamConfs() {
        return entityManager().createQuery("SELECT COUNT(o) FROM ParamConf o", Long.class).getSingleResult();
    }
    
    public static List<ParamConf> ParamConf.findAllParamConfs() {
        return entityManager().createQuery("SELECT o FROM ParamConf o", ParamConf.class).getResultList();
    }
    
    public static List<ParamConf> ParamConf.findAllParamConfs(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM ParamConf o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, ParamConf.class).getResultList();
    }
    
    public static ParamConf ParamConf.findParamConf(String cle) {
        if (cle == null || cle.length() == 0) return null;
        return entityManager().find(ParamConf.class, cle);
    }
    
    public static List<ParamConf> ParamConf.findParamConfEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM ParamConf o", ParamConf.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<ParamConf> ParamConf.findParamConfEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM ParamConf o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, ParamConf.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void ParamConf.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void ParamConf.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            ParamConf attached = ParamConf.findParamConf(this.cle);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void ParamConf.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void ParamConf.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public ParamConf ParamConf.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ParamConf merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
