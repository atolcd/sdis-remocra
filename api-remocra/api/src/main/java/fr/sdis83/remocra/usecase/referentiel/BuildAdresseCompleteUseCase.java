package fr.sdis83.remocra.usecase.referentiel;

import fr.sdis83.remocra.web.model.referentiel.HydrantModel;
import java.util.List;

public class BuildAdresseCompleteUseCase {

  public List<HydrantModel> execute(List<HydrantModel> listeHydrant) {
    for (HydrantModel hydrant : listeHydrant) {
      String adresse = "<div>";
      adresse += ensureData(hydrant.getNumeroVoie(), " ");
      adresse += ensureData(hydrant.getSuffixeVoie(), " ");
      adresse += ensureData(hydrant.getVoie(), "<br/>");
      adresse += ensureData(hydrant.getVoie2(), "<br/>");
      adresse += ensureData(hydrant.getComplement(), "<br/>");
      adresse += ensureData(hydrant.getCodeCommune(), " ");
      adresse += ensureData(hydrant.getNomCommune(), "</div>");

      hydrant.setAdresseComplete(adresse);
    }

    return listeHydrant;
  }

  private String ensureData(String data, String caractereDeFin) {
    if (data != null) {
      return data + caractereDeFin;
    }
    return "";
  }
}
