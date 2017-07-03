package fr.sdis83.remocra.domain.remocra;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;

@RooJavaBean
@RooJpaActiveRecord(versionField = "", table = "oldeb_visite", schema = "remocra")
@RooJson
@RooToString
public class OldebVisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    private String code;

    @ManyToMany
    @JoinTable(name = "oldeb_visite_anomalie", joinColumns = { @JoinColumn(name = "visite", nullable = false) }, inverseJoinColumns = {
            @JoinColumn(name = "anomalie", nullable = false) })
    private Set<TypeOldebAnomalie> typeOldebAnomalies;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "visite", orphanRemoval = true)
    private Set<OldebVisiteDocument> oldebVisiteDocuments = new HashSet<OldebVisiteDocument>();

    @OneToMany(mappedBy = "visite")
    private Set<OldebVisiteSuite> oldebVisiteSuites;

    @ManyToOne
    @JoinColumn(name = "oldeb", referencedColumnName = "id", nullable = false)
    private Oldeb oldeb;

    @ManyToOne
    @JoinColumn(name = "action", referencedColumnName = "id", nullable = false)
    private TypeOldebAction action;

    @ManyToOne
    @JoinColumn(name = "avis", referencedColumnName = "id", nullable = false)
    private TypeOldebAvis avis;

    @ManyToOne
    @JoinColumn(name = "debroussaillement_parcelle", referencedColumnName = "id", nullable = false)
    private TypeOldebDebroussaillement debroussaillementParcelle;

    @ManyToOne
    @JoinColumn(name = "debroussaillement_acces", referencedColumnName = "id", nullable = false)
    private TypeOldebDebroussaillement debroussaillementAcces;

    @ManyToOne
    @JoinColumn(name = "utilisateur", referencedColumnName = "id")
    private Utilisateur utilisateur;

    @Column(name = "date_visite")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateVisite;

    @Column(name = "agent")
    @NotNull
    private String agent;

    @Column(name = "observation")
    private String observation;

    @Formula("(select av.nom from remocra.type_oldeb_avis av where av.id = avis)")
    private String nomAvis;

    @Formula("(select ac.nom from remocra.type_oldeb_action ac where ac.id = action)")
    private String nomAction;

    @Formula("(select tod.nom from remocra.type_oldeb_debroussaillement tod where tod.id = debroussaillement_acces)")
    private String nomDebAcces;

    @Formula("(select tod.nom from remocra.type_oldeb_debroussaillement tod where tod.id = debroussaillement_parcelle)")
    private String nomDebParcelle;

    @Formula("(select count(*) from remocra.oldeb_visite_anomalie ova where ova.visite = id)")
    private String totalAnomalies;
}
