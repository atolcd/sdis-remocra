package fr.sdis83.remocra.domain.remocra;

import fr.sdis83.remocra.util.Feature;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

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

  @ManyToOne private TypeHydrantDiametre diametre;

  @Column private Integer debit;

  @Column private Integer debitNominal;

  @Column private Integer debitMax;

  @Column private Double pression;

  @Column private Double pressionDyn;

  @Column private Double pressionDynDeb;

  @Column private String gestReseau;

  @Column private String numeroSCP;

  @Column private Boolean renversable;

  @ManyToOne private TypeHydrantMarque marque;

  @ManyToOne private TypeHydrantModele modele;

  @OneToOne() private HydrantPena pena;

  @ManyToOne private HydrantReservoir reservoir;

  @OneToOne(optional = true)
  private HydrantPibi jumele;

  @Column private Boolean dispositif_inviolabilite;

  @ManyToOne private Organisme serviceEaux;

  @Column(name = "debit_renforce")
  private Boolean debitRenforce;

  @ManyToOne private TypeReseauCanalisation typeReseauCanalisation;

  @ManyToOne private TypeReseauAlimentation typeReseauAlimentation;

  @Column private Integer diametreCanalisation;

  @Column private Boolean surpresse;

  @Column private Boolean additive;

  public Feature toFeature() {
    Feature feature = super.toFeature();
    feature.addProperty("debit", this.getDebit());
    feature.addProperty("debitMax", this.getDebitMax());
    feature.addProperty("debitNominal", this.getDebitNominal());
    feature.addProperty("typeReseau", this.getTypeReseauId());
    feature.addProperty("typeReseauNom", this.getTypeReseauNom());
    feature.addProperty("diametreCanalisation", this.diametreCanalisation);
    feature.addProperty("diametreNom", this.getDiametreNom());
    feature.addProperty("diametreId", this.getDiametreId());
    return feature;
  }

  public Long getTypeReseauId() {
    if (this.getTypeReseauAlimentation() != null) {
      return this.getTypeReseauAlimentation().getId();
    }
    return null;
  }

  public String getDiametreNom() {
    if (this.getDiametre() != null) {
      return this.getDiametre().getNom();
    }
    return null;
  }

  public Long getDiametreId() {
    if (this.getDiametre() != null) {
      return this.getDiametre().getId();
    }
    return null;
  }

  public String getTypeReseauNom() {
    if (this.getTypeReseauAlimentation() != null) {
      return this.getTypeReseauAlimentation().getNom();
    }
    return null;
  }
}
