package fr.sdis83.remocra.usecase.referentiel;

import fr.sdis83.remocra.repository.TypeDroitRepository;
import fr.sdis83.remocra.util.GlobalConstants;
import fr.sdis83.remocra.web.model.referentiel.TypeHydrantSaisieModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/** Va chercher les type visites que l'utilisateur peut réaliser */
public class GetTypeVisiteUtilisateur {
  @Inject TypeDroitRepository typeDroitRepository;

  public List<TypeHydrantSaisieModel> execute(
      Long idUtilisateur, List<TypeHydrantSaisieModel> typeHydrantSaisies) {
    List<String> codesTypeDroitsVisite =
        typeDroitRepository.getDroitsUtilisateurVisite(idUtilisateur);

    List<TypeHydrantSaisieModel> typeVisiteUtilisateur = new ArrayList<>();
    for (TypeHydrantSaisieModel typeSaisie : typeHydrantSaisies) {

      checkDroit(
          typeVisiteUtilisateur,
          typeSaisie,
          codesTypeDroitsVisite,
          GlobalConstants.TypeVisite.CREATION.getCode(),
          TypeDroitRepository.TypeDroitsPourMobile.HYDRANTS_CREATION_C.getCodeDroitMobile());
      checkDroit(
          typeVisiteUtilisateur,
          typeSaisie,
          codesTypeDroitsVisite,
          GlobalConstants.TypeVisite.CONTROLE.getCode(),
          TypeDroitRepository.TypeDroitsPourMobile.HYDRANTS_CONTROLE_C.getCodeDroitMobile());
      checkDroit(
          typeVisiteUtilisateur,
          typeSaisie,
          codesTypeDroitsVisite,
          GlobalConstants.TypeVisite.NON_PROGRAMMEE.getCode(),
          TypeDroitRepository.TypeDroitsPourMobile.HYDRANTS_ANOMALIES_C.getCodeDroitMobile());
      checkDroit(
          typeVisiteUtilisateur,
          typeSaisie,
          codesTypeDroitsVisite,
          GlobalConstants.TypeVisite.RECONNAISSANCE.getCode(),
          TypeDroitRepository.TypeDroitsPourMobile.HYDRANTS_RECONNAISSANCE_C.getCodeDroitMobile());
      checkDroit(
          typeVisiteUtilisateur,
          typeSaisie,
          codesTypeDroitsVisite,
          GlobalConstants.TypeVisite.RECEPTION.getCode(),
          TypeDroitRepository.TypeDroitsPourMobile.HYDRANTS_RECEPTION_C.getCodeDroitMobile());
    }
    return typeVisiteUtilisateur;
  }

  private void checkDroit(
      List<TypeHydrantSaisieModel> list,
      TypeHydrantSaisieModel typeSaisie,
      List<String> codesTypeDroitsVisite,
      String typeVisite,
      String typeDroit) {
    if (typeSaisie.getCode().equals(typeVisite)) {
      if (codesTypeDroitsVisite.contains(typeDroit)) {
        list.add(typeSaisie);
      }
    }
  }
}
