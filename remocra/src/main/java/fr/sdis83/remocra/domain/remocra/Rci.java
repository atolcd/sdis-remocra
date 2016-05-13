package fr.sdis83.remocra.domain.remocra;

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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.utils.RemocraDateHourFormat;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.util.Featurable;
import fr.sdis83.remocra.util.Feature;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(/* versionField = "version" */)
public class Rci implements Featurable {

    public static enum DirectionVent {
        N, S, E, O, NE, NO, SE, SO
    }

    static JSONSerializer SERIALIZER = new JSONSerializer();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // @Version
    // @Column(name = "version", columnDefinition = "INTEGER default 1")
    // private Integer version;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateModification;

    @NotNull
    @ManyToOne
    private Utilisateur utilisateur;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date dateIncendie;

    @NotNull
    @ManyToOne
    TypeRciOrigineAlerte origineAlerte;

    @ManyToOne
    private Commune commune;

    private String voie;

    private String complement;

    @NotNull
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point geometrie;

    @Formula("ST_AsGeoJSON(geometrie)")
    private String jsonGeometrie;

    private String coordDFCI;

    @ManyToOne
    private Utilisateur arriveeDdtmOnf;

    @ManyToOne
    private Utilisateur arriveeSdis;

    @ManyToOne
    private Utilisateur arriveeGendarmerie;

    @ManyToOne
    private Utilisateur arriveePolice;

    @NotNull
    private String pointEclosion;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date gdh;

    private Boolean ventLocal;

    private Integer hygrometrie;

    private DirectionVent directionVent;

    private Double temperature;

    /**
     * 0 à 12
     */
    private Integer forceVent;

    /**
     * 1 à 11
     */
    private Integer indiceRothermel;

    private Double superficieSecours;
    private Double superficieReferent;
    private Double superficieFinale;

    private String premierEngin;

    private String premierCos;

    private String forcesOrdre;

    private Boolean gelLieux;

    @ManyToOne
    private TypeRciPromCategorie categoriePromethee;

    @ManyToOne
    private TypeRciDegreCertitude degreCertitude;

    private String commentaireConclusions;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rci", orphanRemoval = true)
    private Set<RciDocument> rciDocuments = new HashSet<RciDocument>();

    public static Rci fromJsonToRci(String json) {
        return new JSONDeserializer<Rci>().use(null, Rci.class).use(Date.class, RemocraDateHourTransformer.getInstance()).use(Geometry.class, new GeometryFactory())
                .deserialize(json);
    }

    public Feature toFeature() {
        Feature feature = new Feature(this.id, this.getJsonGeometrie());
        feature.addProperty("dateIncendie", new RemocraDateHourFormat().format(getDateIncendie()));
        feature.addProperty("coordDFCI", this.getCoordDFCI());
        return feature;
    }
}
