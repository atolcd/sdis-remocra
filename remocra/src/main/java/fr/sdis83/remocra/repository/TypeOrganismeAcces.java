package fr.sdis83.remocra.repository;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeOrganismeAcces {

    private Long id;
    private Boolean recuperer;
    private Boolean transmettre;
    private Boolean administrer;
    private Long organismeId;
    private String organismeNom;

    public TypeOrganismeAcces() {
    }

    public TypeOrganismeAcces(Long id, Boolean recuperer, Boolean transmettre, Boolean administrer, Long organismeId, String organismeNom) {
        this.id = id;
        this.recuperer = recuperer;
        this.transmettre = transmettre;
        this.administrer = administrer;
        this.organismeId = organismeId;
        this.organismeNom = organismeNom;
    }
    public Long getId() {
        return id;
    }

    public Boolean getRecuperer() {
        return recuperer;
    }

    public Boolean getTransmettre() {
        return transmettre;
    }

    public Boolean getAdministrer() {
        return administrer;
    }

    public Long getOrganismeId() {
        return organismeId;
    }

    public String getOrganismeNom() {
        return organismeNom;
    }
}
