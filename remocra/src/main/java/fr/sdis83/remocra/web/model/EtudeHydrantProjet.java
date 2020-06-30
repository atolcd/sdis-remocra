package fr.sdis83.remocra.web.model;

import fr.sdis83.remocra.domain.remocra.TypeHydrantDiametre;
import fr.sdis83.remocra.domain.remocra.TypeHydrantNatureDeci;

public class EtudeHydrantProjet {

  private Long id;

  private Etude etude;

  private TypeHydrantNatureDeci type_deci;

  private TypeHydrantDiametre diametre_nominal;

  private String type;

  private Integer diametre_canalisation;

  private Integer capacite;

  private Integer debit;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Etude getEtude() {
    return etude;
  }

  public void setEtude(Etude etude) {
    this.etude = etude;
  }

  public TypeHydrantNatureDeci getType_deci() {
    return type_deci;
  }

  public void setType_deci(TypeHydrantNatureDeci type_deci) {
    this.type_deci = type_deci;
  }

  public TypeHydrantDiametre getDiametre_nominal() {
    return diametre_nominal;
  }

  public void setDiametre_nominal(TypeHydrantDiametre diametre_nominal) {
    this.diametre_nominal = diametre_nominal;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getDiametre_canalisation() {
    return diametre_canalisation;
  }

  public void setDiametre_canalisation(Integer diametre_canalisation) {
    this.diametre_canalisation = diametre_canalisation;
  }

  public Integer getCapacite() {
    return capacite;
  }

  public void setCapacite(Integer capacite) {
    this.capacite = capacite;
  }

  public Integer getDebit() {
    return debit;
  }

  public void setDebit(Integer debit) {
    this.debit = debit;
  }
}
