package fr.sdis83.remocra.usecase.referentiel;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.Parametre;
import fr.sdis83.remocra.enums.PeiCaracteristique;
import fr.sdis83.remocra.repository.ParametreRepository;
import fr.sdis83.remocra.repository.ReferentielRepository;
import fr.sdis83.remocra.usecase.utils.DateUtils;
import fr.sdis83.remocra.util.GlobalConstants;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * UseCase permettant de gérer la récupération dynamique des caractéristiques des PEI, et leur
 * transformation en vue d'être affichées par l'appli mobile
 */
public class PeiCaracteristiquesUseCase {

  @Inject ReferentielRepository referentielRepository;

  @Inject ParametreRepository parametreRepository;

  @Inject
  public Map<Long, String> getPeiCaracteristiques() {
    Map<String, Parametre> mapParametres =
        parametreRepository.getParametres(
            Set.of(
                GlobalConstants.PARAMETRE_CARACTERISTIQUE_PIBI,
                GlobalConstants.PARAMETRE_CARACTERISTIQUE_PENA));

    String pibiSelectedCaracteristiques =
        mapParametres.get(GlobalConstants.PARAMETRE_CARACTERISTIQUE_PIBI).getValeurParametre();

    String penaSelectedCaracteristiques =
        mapParametres.get(GlobalConstants.PARAMETRE_CARACTERISTIQUE_PENA).getValeurParametre();
    Map<Long, List<ReferentielRepository.PeiCaracteristiquePojo>> map =
        referentielRepository.getPeiCaracteristiques(
            fromStringParameter(pibiSelectedCaracteristiques),
            fromStringParameter(penaSelectedCaracteristiques));

    // On transforme la liste de caractéristiques en HTML (liste à puces dans une DIV)
    Map<Long, String> mapRetour = new HashMap<>();
    map.forEach(
        (key, value) -> {
          String decorated =
              "<div><ul>"
                  + value.stream()
                      .map(
                          it ->
                              "<li>"
                                  + it.getCaracteristique().getLibelle()
                                  + " : "
                                  + formatValue(it.getValue(), it.getCaracteristique())
                                  + "</li>")
                      .collect(Collectors.joining())
                  + "</ul></div>";
          mapRetour.put(key, decorated);
        });

    return mapRetour;
  }

  private static List<PeiCaracteristique> fromStringParameter(String selectedCaracteristiques) {
    return Arrays.stream(selectedCaracteristiques.split(","))
        .map(String::trim)
        .filter(it -> !it.isEmpty())
        .map(PeiCaracteristique::fromString)
        .collect(Collectors.toList());
  }

  /**
   * Formate le type de retour en fonction du PeiCaracteristique choisi
   *
   * @param value Object
   * @param peiCaracteristique PeiCaracteristique
   * @return String la chaine décorée pour intégration directe
   */
  private static String formatValue(Object value, PeiCaracteristique peiCaracteristique) {
    switch (peiCaracteristique) {
      case DATE_RECEPTION:
        return DateUtils.formatNaturel((Instant) value);
      case CAPACITE:
        return (value == null) ? "Non renseignée" : value + " m3";
      case DEBIT:
        return (value == null) ? "Non renseigné" : value + " m3/h";
      default:
        return (value == null) ? "" : value.toString();
    }
  }
}
