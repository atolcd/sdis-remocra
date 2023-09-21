package fr.sdis83.remocra.domain.remocra;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.utils.RemocraDateHourFormat;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.util.Featurable;
import fr.sdis83.remocra.util.Feature;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(finders = {"findAlertesByRapporteurEquals"})
public class Alerte implements Featurable {

  static JSONSerializer SERIALIZER = new JSONSerializer();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version", columnDefinition = "INTEGER default 1")
  private Integer version;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  private Date dateModification;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
  private Date dateConstat;

  @NotNull private String commentaire;

  private Boolean etat;

  @NotNull @ManyToOne private Utilisateur rapporteur;

  @NotNull
  @Type(type = "org.hibernate.spatial.GeometryType")
  private Point geometrie;

  @OnDelete(action = OnDeleteAction.CASCADE)
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "alerte", orphanRemoval = true)
  private Set<AlerteDocument> alerteDocuments = new HashSet<AlerteDocument>();

  @OnDelete(action = OnDeleteAction.CASCADE)
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "alerte", orphanRemoval = true)
  private Set<AlerteElt> alerteElts = new HashSet<AlerteElt>();

  public static Alerte fromJsonToAlerte(String json) {
    return new JSONDeserializer<Alerte>()
        .use(null, Alerte.class)
        .use(Date.class, RemocraDateHourTransformer.getInstance())
        .use(Geometry.class, new GeometryFactory())
        .deserialize(json);
  }

  @Generated(GenerationTime.ALWAYS)
  @Formula("ST_AsGeoJSON(geometrie)")
  private String jsonGeometrie;

  @Override
  public Feature toFeature() {
    Feature feature = new Feature(this.id, this.getJsonGeometrie());
    feature.addProperty("etat", getEtat());
    feature.addProperty("commentaire", getCommentaire());
    // feature.addProperty("modification", getDateModification());
    feature.addProperty("modification", new RemocraDateHourFormat().format(getDateModification()));
    feature.addProperty("rapporteur", getRapporteur().getPrenom() + " " + getRapporteur().getNom());
    feature.addProperty("id", getId());
    feature.addProperty("documents", getAlerteDocuments());
    return feature;
  }
}
