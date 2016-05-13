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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

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
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(versionField = "version")
public class Permis {

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
    private Date datePermis;

    @NotNull
    private String nom;

    @NotNull
    @ManyToOne
    private Commune commune;

    @NotNull
    private String voie;

    @NotNull
    private String complement;

    @NotNull
    private String numero;

    @NotNull
    @ManyToOne
    private TypePermisAvis avis;

    @NotNull
    @ManyToOne
    private TypePermisInterservice interservice;

    @NotNull
    private String sectionCadastrale;

    @NotNull
    private String parcelleCadastrale;

    @NotNull
    private String observations;

    @NotNull
    @ManyToOne
    private TypeOrganisme serviceInstructeur;

    @NotNull
    private Integer annee;

    @NotNull
    @ManyToOne
    private Utilisateur instructeur;

    @NotNull
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point geometrie;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "permis", orphanRemoval = true)
    private Set<PermisDocument> permisDocuments = new HashSet<PermisDocument>();

    public static Permis fromJsonToPermis(String json) {
        return new JSONDeserializer<Permis>().use(null, Permis.class).use(Date.class, RemocraDateHourTransformer.getInstance()).use(Geometry.class, new GeometryFactory())
                .deserialize(json);
    }

}
