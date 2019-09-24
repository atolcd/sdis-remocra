package fr.sdis83.remocra.domain.remocra;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

import com.vividsolutions.jts.geom.Point;

import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.util.Featurable;
import fr.sdis83.remocra.util.Feature;

@RooJavaBean
@RooJpaActiveRecord(versionField = "")
public class HydrantPrescrit implements Featurable {

    // identifiant / géométrie
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point geometrie;

    @Formula("ST_AsGeoJSON(geometrie)")
    private String jsonGeometrie;

    @ManyToOne
    private Organisme organisme;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = RemocraDateHourTransformer.FORMAT)
    private Date datePrescrit;

    @Column
    private Integer nbPoteaux;

    @Column
    private Integer debit;

    @Column
    private String agent;

    @Column
    private String commentaire;

    @Column(name="numdossier")
    private String numDossier;

    public Feature toFeature() {
        Feature feature = new Feature(this.id, this.getJsonGeometrie());
        feature.addProperty("Date ", this.getDatePrescrit());
        feature.addProperty("Nombre de poteaux", this.getNbPoteaux());
        feature.addProperty("debit", this.getDebit());
        feature.addProperty("agent", this.getAgent());
        feature.addProperty("commentaire", this.getCommentaire());
        feature.addProperty("Numéro de dossier", this.getNumDossier());
        return feature;
    }
}
