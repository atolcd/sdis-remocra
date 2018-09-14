package fr.sdis83.remocra.xml;

import java.util.Date;

public class Tournee {

    private Long id;

    private String nom;

    private Date debSync;

    private Date lastSync;

    private LstHydrants hydrants;

    private Integer pourcent;

    public Tournee() {
        //
    }

    public Date getDebSync() {
        return debSync;
    }

    public void setDebSync(Date debSync) {
        this.debSync = debSync;
    }

    public Date getLastSync() {
        return lastSync;
    }

    public void setLastSync(Date lastSync) {
        this.lastSync = lastSync;
    }

    public LstHydrants getHydrants() {
        return hydrants;
    }

    public void setHydrants(LstHydrants hydrants) {
        this.hydrants = hydrants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }


    public Integer getPourcent() {
        return pourcent;
    }

    public void setPourcent(Integer pourcent) {
        this.pourcent = pourcent;
    }
}
