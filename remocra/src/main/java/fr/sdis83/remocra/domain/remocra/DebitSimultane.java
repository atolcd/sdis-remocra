package fr.sdis83.remocra.domain.remocra;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.vividsolutions.jts.geom.Geometry;
import fr.sdis83.remocra.util.Featurable;
import fr.sdis83.remocra.util.Feature;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
public class DebitSimultane implements Featurable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    private Site site;

    @Type(type = "org.hibernate.spatial.GeometryType")
    private Geometry geometrie;

    @Formula("ST_AsGeoJSON(geometrie)")
    private String jsonGeometrie;

    @NotNull
    @Column(name = "numdossier")
    private String numDossier;

    @OneToMany(mappedBy = "debitSimultane")
    private Set<DebitSimultaneMesure> mesures;

    @Override
    public Feature toFeature() {
        Feature feature = new Feature(this.id, this.getJsonGeometrie());
        feature.addProperty("id", this.getId());
        feature.addProperty("geometrie", this.getGeometrie());
        feature.addProperty("numDossier", this.getNumDossier());
        return feature;
    }

}
