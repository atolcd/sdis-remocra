package fr.sdis83.remocra.domain.datasource;

import fr.sdis83.remocra.enums.PeiCaracteristique;
import java.io.Serializable;
import java.util.List;

public class CaracteristiquePeiData implements Serializable {

  private PeiCaracteristique.TypeCaracteristique code;
  private List<CodeLibelleOrdreData> caracteristiques;

  public PeiCaracteristique.TypeCaracteristique getCode() {
    return code;
  }

  public void setCode(PeiCaracteristique.TypeCaracteristique code) {
    this.code = code;
  }

  public List<CodeLibelleOrdreData> getCaracteristiques() {
    return caracteristiques;
  }

  public void setCaracteristiques(List<CodeLibelleOrdreData> caracteristiques) {
    this.caracteristiques = caracteristiques;
  }
}
