package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Formula;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import fr.sdis83.remocra.util.Feature;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class HydrantPena extends Hydrant {

    /*
     * private static String typeHydrant = "PENA";
     * 
     * public HydrantPena() { this.code = typeHydrant; }
     * 
     * @Override public String getCode() { this.code = typeHydrant; return
     * super.getCode(); }
     * 
     * @Override public void setCode(String arg0) { super.setCode(typeHydrant);
     * }
     */

    @Column
    private String coordDFCI;

    @Column
    private String capacite;

    @Column
    private Double qAppoint;

    @ManyToOne
    private TypeHydrantVolConstate volConstate;

    @ManyToOne
    private TypeHydrantPositionnement positionnement;

    @ManyToOne
    private TypeHydrantMateriau materiau;

    @Column(columnDefinition = "BOOLEAN default false")
    private Boolean hbe;

    @OneToOne(mappedBy = "pena")
    private HydrantPibi pibiAssocie;

    @Formula("(select count(*) from remocra.hydrant_aspiration ha where ha.pena = id)")
    private Integer aspirations;


    @Column
    private Boolean illimitee;

    @Column
    private Boolean incertaine;

    public Feature toFeature() {
        Feature feature = super.toFeature();
        if (this.hbe != null && this.hbe.booleanValue() && this.getDispoHbe() != Disponibilite.INDISPO) {
            feature.addProperty("hbe", true);
        }
        return feature;
    }

}
