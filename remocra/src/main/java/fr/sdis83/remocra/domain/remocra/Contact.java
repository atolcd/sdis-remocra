package fr.sdis83.remocra.domain.remocra;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(finders={"findContactsById", "findContactsByCode","findContactsByIdAppartenance"})
@RooToString
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "appartenance")
    private String appartenance;

    @Column(name = "id_appartenance")
    private String idAppartenance;

    @Column(name = "fonction")
    private String fonction;

    @Column(name = "civilite")
    private String civilite;

    @Column(name = "nom")
    @NotNull
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "numero_voie")
    private String numeroVoie;

    @Column(name = "suffixe_voie")
    private String suffixeVoie;

    @Column(name = "lieu_dit")
    private String lieuDit;

    @Column(name = "voie")
    private String voie;

    @Column(name = "code_postal")
    private String codePostal;

    @Column(name = "ville")
    private String ville;

    @Column(name = "pays")
    private String pays;


    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @ManyToMany
    private Set<Role> roles;

}
