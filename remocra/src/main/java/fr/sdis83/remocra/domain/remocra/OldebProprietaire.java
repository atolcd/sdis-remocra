package fr.sdis83.remocra.domain.remocra;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "oldeb_proprietaire", schema = "remocra")
@RooToString

public class OldebProprietaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "organisme")
    private Boolean organisme;

    @Column(name = "raison_sociale")
    private String raisonSociale;

    @Column(name = "civilite")
    private String civilite;

    @Column(name = "nom")
    @NotNull
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Column(name = "code_postal")
    private String codePostal;

    @Column(name = "ville")
    private String ville;

    @Column(name = "num_voie")
    private String numVoie;

    @Column(name = "voie")
    private String voie;

    @Column(name = "lieu_dit")
    private String lieuDit;

    @Column(name = "pays")
    private String pays;

    @OneToMany(mappedBy = "proprietaire")
    private Set<OldebPropriete> oldebProprietes;

}
