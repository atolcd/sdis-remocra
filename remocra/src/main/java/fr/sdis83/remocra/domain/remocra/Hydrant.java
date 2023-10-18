package fr.sdis83.remocra.domain.remocra;

import com.vividsolutions.jts.geom.Point;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.util.Featurable;
import fr.sdis83.remocra.util.Feature;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(
    inheritanceType = "JOINED",
    finders = {"findHydrantsByNumero"})
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"numero"})})
public class Hydrant implements Featurable {

  /** Titre associé à la photo de l'hydrant */
  public static String TITRE_PHOTO = "hydrant.jpg";

  public static enum Disponibilite {
    DISPO,
    INDISPO,
    NON_CONFORME
  }

  public static enum TYPE_SAISIE {
    LECT,
    CREA,
    RECEP,
    RECO,
    CTRL,
    VERIF
  }

  // identifiant / géométrie
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version", columnDefinition = "INTEGER default 1")
  private Integer version;

  @Column protected String code;

  @NotNull
  @Type(type = "org.hibernate.spatial.GeometryType")
  private Point geometrie;

  @Formula("ST_AsGeoJSON(geometrie)")
  private String jsonGeometrie;

  @Formula(
      "(select count(*) from remocra.hydrant_anomalies ha where ha.hydrant = id AND ha.anomalies = (select tha.id from remocra.type_hydrant_anomalie tha where tha.code = 'INDISPONIBILITE_TEMP'))")
  private Integer indispoTemp;

  @ManyToMany private Set<Tournee> tournees;

  // Identification

  @ManyToOne private TypeHydrantNature nature;

  @Column private String numero;

  @Column private Integer numeroInterne;

  @ManyToOne private ZoneSpeciale zoneSpeciale;

  @ManyToOne private TypeHydrantNatureDeci natureDeci;

  // Tracabilité

  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  private Date dateRecep;

  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  private Date dateReco;

  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  private Date dateContr;

  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  private Date dateCrea;

  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  private Date dateVerif;

  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  private Date dateModification;

  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  private Date dateGps;

  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  private Date dateAttestation;

  @Column private String agent1;

  @Column private String agent2;

  @ManyToOne private Organisme organisme;

  @ManyToOne private Utilisateur utilisateurModification;

  @Column private String auteurModificationFlag;

  // Localisation

  @Column private String lieuDit;

  @ManyToOne private Commune commune;

  @Column private String voie;

  @Column private String voie2;

  @Column private String complement;

  // Element MCO

  @Column private Integer anneeFabrication;

  @Formula(
      "(select case when char_length(voie)>0 and char_length(voie2)>0 then voie || ' - ' || voie2 else voie end from remocra.hydrant h where h.id=id and char_length(voie)>0)")
  private String adresse;

  @OnDelete(action = OnDeleteAction.CASCADE)
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "hydrant", orphanRemoval = true)
  private Set<HydrantDocument> hydrantDocuments = new HashSet<HydrantDocument>();

  @Column private String courrier;

  // Gestionnaire

  @ManyToOne private TypeHydrantDomaine domaine;

  @Column private String gestPointEau;
  // Point d'attention

  @Column private String observation;

  @ManyToMany private Set<TypeHydrantAnomalie> anomalies;

  // Disponibilité

  @Enumerated(EnumType.STRING)
  private Disponibilite dispoTerrestre;

  @Enumerated(EnumType.STRING)
  private Disponibilite dispoHbe;

  @Enumerated(EnumType.STRING)
  private Disponibilite dispoAdmin;

  @Column(name = "numero_voie")
  private Integer numeroVoie;

  @Column(name = "suffixe_voie")
  private String suffixeVoie;

  @ManyToOne private TypeHydrantNiveau niveau;

  @ManyToOne private Gestionnaire gestionnaire;

  @ManyToOne private Organisme spDeci;

  @ManyToOne private GestionnaireSite gestionnaire_site;

  @ManyToOne private Organisme autoriteDeci;

  @Column(name = "en_face")
  private Boolean enFace;

  @ManyToOne private Organisme maintenanceDeci;

  @OneToMany(mappedBy = "hydrant", cascade = CascadeType.ALL)
  @OrderBy("date desc")
  private Set<HydrantVisite> visites;

  @Column(name = "date_changement_dispo_terrestre")
  private Date dateChangementDispoTerrestre;

  // Autre
  @Override
  public Feature toFeature() {
    Feature feature = new Feature(this.id, this.getJsonGeometrie());
    feature.addProperty("typeHydrantCode", this.getCode());
    feature.addProperty("dispo", this.getDispoTerrestre());
    feature.addProperty("nature", this.getNature().getCode());
    feature.addProperty("numero", this.getNumero());
    feature.addProperty("commune", this.getCommuneId());
    feature.addProperty("nomCommune", this.getNomCommune());
    feature.addProperty("internalId", this.getId());
    feature.addProperty("tournees", this.getTourneesId());
    feature.addProperty("nom tournées", this.getTourneesNom());
    feature.addProperty("nomNatureDeci", this.getNomNatureDeci());
    feature.addProperty("codeNatureDeci", this.getCodeNatureDeci());
    feature.addProperty("gestionnaireSiteId", this.getGestionnaireSiteId());
    feature.addProperty("gestionnaireSiteNom", this.getGestionnaireSiteNom());
    feature.addProperty("gestionnaireNom", this.getGestionnaireNom());
    feature.addProperty("dateChangementDispoTerrestre", this.getDateChangementDispoTerrestre());

    // PIBI
    String diametreCode = null;
    Double pression = null;
    if (this instanceof HydrantPibi) {
      // Diametre
      TypeHydrantDiametre thd = ((HydrantPibi) this).getDiametre();
      if (thd != null) {
        diametreCode = thd.getCode();
      }
      // Pression
      pression = ((HydrantPibi) this).getPression();
    }
    feature.addProperty("pression", pression);
    feature.addProperty("diametre", diametreCode);
    // PENA
    String positionnementCode = null;
    if (this instanceof HydrantPena) {
      TypeHydrantPositionnement thp = ((HydrantPena) this).getPositionnement();
      if (thp != null) {
        positionnementCode = thp.getCode();
      }
    }
    feature.addProperty("positionnement", positionnementCode);
    return feature;
  }

  public Long getCommuneId() {
    if (this.getCommune() != null) {
      return this.getCommune().getId();
    }
    return null;
  }

  public String getNomCommune() {
    if (this.getCommune() != null) {
      return this.getCommune().getNom();
    }
    return null;
  }

  public String getNatureNom() {
    if (this.getNature() != null) {
      return this.getNature().getNom();
    }
    return null;
  }

  public String getCISCommune() {
    if (this.getOrganisme() != null) {
      return this.getOrganisme().getNom();
    }
    return null;
  }

  public String getTourneesId() {
    if (this.getTournees() != null && !this.getTournees().isEmpty()) {
      ArrayList liste = new ArrayList<String>();
      for (Tournee t : this.getTournees()) liste.add(t.getId().toString());
      return liste.toString();
    }
    return null;
  }

  public String getTourneesNom() {
    if (this.getTournees() != null && !this.getTournees().isEmpty()) {
      ArrayList liste = new ArrayList<String>();
      for (Tournee t : this.getTournees()) liste.add(t.getNom().toString());
      return liste.toString();
    }
    return null;
  }

  public HydrantDocument getPhoto() {
    HydrantDocument photo = null;
    for (HydrantDocument hydrantDocument : hydrantDocuments) {
      if (TITRE_PHOTO.equals(hydrantDocument.getTitre())) {
        return hydrantDocument;
      }
    }
    return photo;
  }

  public String getNomNatureDeci() {
    if (this.getNatureDeci() != null) {
      return this.getNatureDeci().getNom();
    }
    return null;
  }

  public String getCodeNatureDeci() {
    if (this.getNatureDeci() != null) {
      return this.getNatureDeci().getCode();
    }
    return null;
  }

  public Long getGestionnaireSiteId() {
    if (this.getGestionnaireSite() != null) {
      return this.getGestionnaireSite().getId();
    }
    return null;
  }

  public String getGestionnaireSiteNom() {
    if (this.getGestionnaireSite() != null) {
      return this.getGestionnaireSite().getNom();
    }
    return null;
  }

  public String getGestionnaireNom() {
    if (this.getGestionnaire() != null) {
      return this.getGestionnaire().getNom();
    }
    return null;
  }

  public void setPhoto(HydrantDocument photo) {
    HydrantDocument toRemove = null;
    for (HydrantDocument hydrantDocument : hydrantDocuments) {
      if (TITRE_PHOTO.equals(hydrantDocument.getTitre())) {
        toRemove = hydrantDocument;
      }
      break;
    }
    if (toRemove != null) {
      hydrantDocuments.remove(toRemove);
    }
    hydrantDocuments.add(photo);
  }

  @Transient private String nomTournee;
}
