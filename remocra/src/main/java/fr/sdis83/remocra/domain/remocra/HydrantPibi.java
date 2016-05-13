package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import fr.sdis83.remocra.util.Feature;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class HydrantPibi extends Hydrant {

    /*
     * private static String typeHydrant = "PIBI";
     * 
     * public HydrantPibi() { this.code = typeHydrant; }
     * 
     * @Override public String getCode() { this.code = typeHydrant; return
     * super.getCode(); }
     * 
     * @Override public void setCode(String arg0) { super.setCode(typeHydrant);
     * }
     */

    @ManyToOne
    private TypeHydrantDiametre diametre;

    @Column
    private Integer debit;

    @Column
    private Integer debitMax;

    @Column
    private Double pression;

    @Column
    private Double pressionDyn;

    @Column
    private String gestReseau;

    @Column
    private String numeroSCP;

    @Column
    private Boolean choc;

    @ManyToOne
    private TypeHydrantMarque marque;

    @ManyToOne
    private TypeHydrantModele modele;

    @OneToOne()
    private HydrantPena pena;

    public Feature toFeature() {
        Feature feature = super.toFeature();
        feature.addProperty("debit", this.getDebit());
        return feature;
    }

}
