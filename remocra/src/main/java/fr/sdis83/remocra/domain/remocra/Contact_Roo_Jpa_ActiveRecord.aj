// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.domain.remocra.Contact;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Contact_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Contact.entityManager;
    
    public static final List<String> Contact.fieldNames4OrderClauseFilter = java.util.Arrays.asList("roles");
    
    public static final EntityManager Contact.entityManager() {
        EntityManager em = new Contact().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Contact.countContacts() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Contact o", Long.class).getSingleResult();
    }
    
    public static List<Contact> Contact.findAllContacts() {
        return entityManager().createQuery("SELECT o FROM Contact o", Contact.class).getResultList();
    }

    public static List<Contact> Contact.findAllContactsByIdAppartenance(String idAppartenance) {
        return entityManager().createQuery("SELECT o FROM Contact o where id_appartenance = '" + idAppartenance+"'", Contact.class).getResultList();
    }
    public static List<Contact> Contact.findAllContacts(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Contact o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Contact.class).getResultList();
    }
    
    public static Contact Contact.findContact(Long id) {
        if (id == null) return null;
        return entityManager().find(Contact.class, id);
    }
    
    public static List<Contact> Contact.findContactEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Contact o", Contact.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<Contact> Contact.findContactEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Contact o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Contact.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Contact.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Contact.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Contact attached = Contact.findContact(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Contact.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Contact.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Contact Contact.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Contact merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
