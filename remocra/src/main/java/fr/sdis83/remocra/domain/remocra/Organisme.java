package fr.sdis83.remocra.domain.remocra;


import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = { "findOrganismesByCode" })
public class Organisme {

    /**
     * L'inheritenceType SINGLE_TABLE et le GenerationType.Identity sont
     * incompatible avec le driver Postgresql > 8.3 Du coup on reste en
     * 8.3...pour l'instant :
     * 
     * http://stackoverflow.com/questions/1333596/postgresql-identity-in-jpa-
     * single-table-hierarchy
     * 
     * On utilise maintenant JOINED => On peut passer en postgres 9.x. A
     * tester...
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version", columnDefinition = "INTEGER default 1")
    private Integer version;

    @NotNull
    private String code;

    private String nom;

    private String emailContact;

    @NotNull
    @Column(columnDefinition = "boolean default true")
    private Boolean actif;

    @NotNull
    @ManyToOne
    private TypeOrganisme typeOrganisme;

    @NotNull
    @ManyToOne
    private ProfilOrganisme profilOrganisme;

    @ManyToOne
    private ZoneCompetence zoneCompetence;

    @ManyToOne(optional = true)
    private Organisme organismeParent;

}
