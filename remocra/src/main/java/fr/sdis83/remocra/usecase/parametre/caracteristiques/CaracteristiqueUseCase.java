package fr.sdis83.remocra.usecase.parametre.caracteristiques;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sdis83.remocra.domain.datasource.CodeLibelleOrdreData;
import fr.sdis83.remocra.enums.PeiCaracteristique;
import fr.sdis83.remocra.repository.ParametreRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaracteristiqueUseCase {

  private static final String PREFIX_PARAMETRE = "CARACTERISTIQUE_";
  private static final String PARAMETRE_PENA = PREFIX_PARAMETRE + "PENA";
  private static final String PARAMETRE_PIBI = PREFIX_PARAMETRE + "PIBI";

  @Autowired ParametreRepository parametreRepository;

  public CaracteristiqueUseCase() {}

  /**
   * Renvoie la liste des paramètres non sélectionnés par l'utilisateur à afficher sur la tablette
   *
   * @return String
   */
  public String getCaracteristiquesNonChoisiesByType(
      PeiCaracteristique.TypeCaracteristique typeCaracteristique) throws JsonProcessingException {

    List<PeiCaracteristique> caracteristiquesListeComplete =
        PeiCaracteristique.getValuesByType(typeCaracteristique);
    List<CodeLibelleOrdreData> codeLibelleOrdreDataListe = new ArrayList<>();

    for (PeiCaracteristique peiCaracteristique : caracteristiquesListeComplete) {
      codeLibelleOrdreDataListe.add(
          new CodeLibelleOrdreData(peiCaracteristique.getCode(), peiCaracteristique.getLibelle()));
    }

    // Crée une copie de la liste complète pour éviter de modifier la liste en cours de parcours
    List<CodeLibelleOrdreData> listeNonChoisie = new ArrayList<>(codeLibelleOrdreDataListe);
    /*
      On enlève de la liste "listeNonChoisie" les éléments qui ont un code présent dans la "listeChoisie"
    */

    for (CodeLibelleOrdreData element : codeLibelleOrdreDataListe) {
      if (getCodesChoisis(typeCaracteristique).contains(element.getCode())) {
        listeNonChoisie.remove(element);
      }
    }

    return new ObjectMapper().writeValueAsString(listeNonChoisie);
  }

  /**
   * Renvoie la liste des paramètres sélectionnable par l'utilisateur à afficher sur la tablette
   *
   * @return String
   */
  public String getCaracteristiquesChoisiesByType(
      PeiCaracteristique.TypeCaracteristique typeCaracteristique) throws JsonProcessingException {

    return new ObjectMapper().writeValueAsString(getListCaracChoisie(typeCaracteristique));
  }

  private List<CodeLibelleOrdreData> getListCaracChoisie(
      PeiCaracteristique.TypeCaracteristique typeCaracteristique) {

    String cle = PREFIX_PARAMETRE + typeCaracteristique;
    String param = parametreRepository.getByCle(cle).getValeurParametre();
    List<CodeLibelleOrdreData> list = new ArrayList<>();
    int index = 0;
    for (String caracString : param.split(",")) {
      if (caracString != null && !caracString.isEmpty()) {
        PeiCaracteristique caracteristique = PeiCaracteristique.fromString(caracString);
        list.add(
            new CodeLibelleOrdreData(
                caracteristique.getCode(), caracteristique.getLibelle(), index++));
      }
    }
    return list;
  }

  private List<String> getCodesChoisis(PeiCaracteristique.TypeCaracteristique typeCaracteristique) {
    List<CodeLibelleOrdreData> listeChoisie = getListCaracChoisie(typeCaracteristique);
    List<String> listeCodeChoisi = new ArrayList<>();
    for (CodeLibelleOrdreData element : listeChoisie) {
      listeCodeChoisi.add(element.getCode());
    }
    return listeCodeChoisi;
  }

  public void updateCaracteristiquesChoisieByType(
      List<CodeLibelleOrdreData> pibiValeur, List<CodeLibelleOrdreData> penaValeur) {

    // on s'assure que l'ordre est respecté
    pibiValeur.sort(new SortCodeLibelleOrdre());
    // on fait une list de code dans le bon ordre (on a juste besoin des codes)
    String parametrePibi = getParam(pibiValeur);
    String parametrePena = getParam(penaValeur);
    parametreRepository.updateByKey(PARAMETRE_PIBI, parametrePibi);
    parametreRepository.updateByKey(PARAMETRE_PENA, parametrePena);
  }

  private String getParam(List<CodeLibelleOrdreData> list) {

    List<String> listeCode = new ArrayList<>();
    for (CodeLibelleOrdreData data : list) {
      listeCode.add(data.getCode());
    }
    return concat(listeCode, ",");
  }

  private static String concat(Iterable<String> values, String sep) {
    Iterator<String> iterator = values.iterator();
    if (iterator.hasNext()) {
      StringBuilder sb = new StringBuilder();
      // On ajoute le premier élément :
      sb.append(iterator.next());
      while (iterator.hasNext()) {
        // Puis les suivants précédés du séparateur :
        sb.append(sep);
        sb.append(iterator.next());
      }
      // Et on renvoit le résultat :
      return sb.toString();
    }
    // Cas particulier : aucun élément
    return "";
  }

  static class SortCodeLibelleOrdre implements java.util.Comparator<CodeLibelleOrdreData> {

    @Override
    public int compare(CodeLibelleOrdreData a, CodeLibelleOrdreData b) {
      return a.getOrdre().compareTo(b.getOrdre());
    }
  }
}
