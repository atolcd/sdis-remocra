package fr.sdis83.remocra.domain.remocra;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

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
    private Double pressionDynDeb;

    @Column
    private String gestReseau;

    @Column
    private String numeroSCP;

    @Column
    private Boolean renversable;

    @ManyToOne
    private TypeHydrantMarque marque;

    @ManyToOne
    private TypeHydrantModele modele;

    @OneToOne()
    private HydrantPena pena;

    @ManyToOne
    private HydrantReservoir reservoir;

    @ManyToOne(optional = true)
    private HydrantPibi jumele;

    @Column
    private Boolean dispositif_inviolabilite;

    @ManyToOne
    private Organisme serviceEaux;

    @Column(name= "debit_renforce")
    private Boolean debitRenforce;

    @ManyToOne
    private TypeReseauCanalisation typeReseauCanalisation;

    @ManyToOne
    private TypeReseauAlimentation typeReseauAlimentation;

    @Column
    private Integer diametreCanalisation;

    @Column
    private Boolean surpresse;

    @Column
    private Boolean additive;

    public Feature toFeature() {
        Feature feature = super.toFeature();
        feature.addProperty("debit", this.getDebit());
        feature.addProperty("debitMax", this.getDebitMax());
        return feature;
    }

}
