package fr.sdis83.remocra.domain.remocra;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.util.Featurable;
import fr.sdis83.remocra.util.Feature;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(
    versionField = "",
    finders = {"findOldebsByCommune"})
@RooJson
public class Oldeb implements Featurable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToMany
  @JoinTable(
      name = "oldeb_caracteristique",
      joinColumns = {@JoinColumn(name = "oldeb", nullable = false)},
      inverseJoinColumns = {@JoinColumn(name = "caracteristique", nullable = false)})
  private Set<TypeOldebCaracteristique> typeOldebCaracteristiques;

  @OneToMany(mappedBy = "oldeb")
  private Set<OldebLocataire> oldebLocataires;

  @OneToMany(mappedBy = "oldeb")
  private Set<OldebPropriete> oldebProprietes;

  @OneToMany(mappedBy = "oldeb")
  @OrderBy("dateVisite DESC")
  private Set<OldebVisite> oldebVisites;

  @ManyToOne
  @JoinColumn(name = "commune", referencedColumnName = "id", nullable = false)
  private Commune commune;

  @ManyToOne
  @JoinColumn(name = "acces", referencedColumnName = "id")
  private TypeOldebAcces acces;

  @ManyToOne
  @JoinColumn(name = "zone_urbanisme", referencedColumnName = "id")
  private TypeOldebZoneUrbanisme zoneUrbanisme;

  @NotNull
  @Type(type = "org.hibernate.spatial.GeometryType")
  private Geometry geometrie;

  @Formula("ST_AsGeoJSON(geometrie)")
  private String jsonGeometrie;

  @Column(name = "section", unique = true)
  @NotNull
  private String section;

  @Column(name = "parcelle", unique = true)
  @NotNull
  private String parcelle;

  @Column(name = "num_voie")
  private String numVoie;

  @Column(name = "voie")
  private String voie;

  @Column(name = "lieu_dit")
  private String lieuDit;

  @Column(name = "volume")
  @NotNull
  private Integer volume;

  @Column(name = "largeur_acces")
  private Integer largeurAcces;

  @Column(name = "portail_electrique")
  @NotNull
  private boolean portailElectrique;

  @Column(name = "code_portail")
  private String codePortail;

  @Column(name = "actif")
  private Boolean actif;

  // Cas quand :
  @Formula(
      value =
          " CASE BTRIM(voie)"
              // voie est null
              + " WHEN null THEN null"
              // voie contient une chaine vide
              + " WHEN '' THEN null"
              // retour de la concaténation des valeurs
              + " ELSE BTRIM(COALESCE(num_voie, '') || ' ' || voie || ' ' || COALESCE(lieu_dit, '')) "
              + "END")
  private String adresse;

  // ov.date_visite desc limit 1 utilisé pour avoir la date dernière visite
  @Formula(
      "(select ov.date_visite from remocra.oldeb_visite ov where ov.oldeb = id order by ov.date_visite desc limit 1)")
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  private Date dateDerniereVisite;

  @Formula("(select zu.nom from remocra.type_oldeb_zone_urbanisme zu where zu.id = zone_urbanisme)")
  private String nomZoneUrbanisme;

  @Formula(
      " (select tod.nom from remocra.oldeb_visite ov inner join remocra.type_oldeb_debroussaillement tod on (ov.debroussaillement_parcelle = tod.id)"
          + "where ov.oldeb = id order by ov.date_visite desc limit 1) ")
  private String debroussaillement;

  @Formula(
      "(select toc.nom from remocra.oldeb_visite ov inner join remocra.type_oldeb_avis toc on (ov.avis = toc.id) "
          + "where ov.oldeb = id order by ov.date_visite desc limit 1)")
  private String avis;

  @Override
  public Feature toFeature() {
    Feature feature = new Feature(this.id, this.getJsonGeometrie());
    feature.addProperty("section", this.getSection());
    feature.addProperty("parcelle", this.getParcelle());
    return feature;
  }
}
