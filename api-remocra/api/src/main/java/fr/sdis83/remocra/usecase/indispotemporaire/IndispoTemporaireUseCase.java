package fr.sdis83.remocra.usecase.indispotemporaire;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.HydrantIndispoTemporaire;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TypeHydrantIndispoStatut;
import fr.sdis83.remocra.repository.HydrantIndispoTemporaireRepository;
import fr.sdis83.remocra.repository.IndispoTemporaireRepository;
import fr.sdis83.remocra.repository.PeiRepository;
import fr.sdis83.remocra.repository.TypeHydrantIndispoStatutRepository;
import fr.sdis83.remocra.usecase.pei.PeiUseCase;
import fr.sdis83.remocra.usecase.utils.DateUtils;
import fr.sdis83.remocra.web.exceptions.ResponseException;
import fr.sdis83.remocra.web.model.indispotemporaire.IndispoTemporaireForm;
import fr.sdis83.remocra.web.model.indispotemporaire.IndispoTemporaireModel;
import fr.sdis83.remocra.web.model.indispotemporaire.IndispoTemporaireSpecifiqueForm;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.format.DateTimeParseException;
import java.util.List;

public class IndispoTemporaireUseCase {

  @Inject
  IndispoTemporaireRepository indispoTemporaireRepository;

  @Inject
  TypeHydrantIndispoStatutRepository typeHydrantIndispoStatutRepository;

  @Inject
  HydrantIndispoTemporaireRepository hydrantIndispoTemporaireRepository;

  @Inject
  PeiRepository peiRepository;

  @Inject
  PeiUseCase peiUseCase;

  public List<IndispoTemporaireModel> getAll(String organismeApi, String hydrant, String statut, Integer limit, Integer start) {
    return this.indispoTemporaireRepository.getAll(organismeApi, hydrant, statut, limit, start);
  }

  public HydrantIndispoTemporaire addIndispoTemporaire(IndispoTemporaireForm indispoForm) throws ResponseException {
    try {
      TypeHydrantIndispoStatut typeHydrantIndispoStatut = typeHydrantIndispoStatutRepository.getByCode(indispoForm.statut());
      if (typeHydrantIndispoStatut == null) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "3000 : Le statut de l'indisponibilité temporaire ne correspond à aucune valeur connue");
      }

      HydrantIndispoTemporaire indispo = new HydrantIndispoTemporaire();
      indispo.setStatut(typeHydrantIndispoStatut.getId());

      if (indispoForm.date_debut() != null) {
        indispo.setDateDebut(DateUtils.getMoment(indispoForm.date_debut()).toInstant());
      }

      if (indispoForm.date_fin() != null) {
        indispo.setDateFin(DateUtils.getMoment(indispoForm.date_fin()).toInstant());
      }

      if (indispo.getDateDebut() != null && indispo.getDateFin() != null && !indispo.getDateDebut().isBefore(indispo.getDateFin())) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "3100 : La date de fin ne peut être égale ou antérieure à la date de début");
      }

      indispo.setMotif(indispoForm.motif());
      indispo.setBasculeAutoIndispo(indispoForm.bascule_auto_indispo());
      indispo.setBasculeAutoDispo(indispoForm.bascule_auto_dispo());
      indispo.setMelAvantIndispo(indispoForm.mel_avant_indispo());
      indispo.setMelAvantDispo(indispoForm.mel_avant_dispo());

      // On vérifie que les PEI sont bien accessibles par l'organisme
      for (String s : indispoForm.hydrants()) {
        if (!peiUseCase.isPeiAccessible(s)) {
          throw new ResponseException(Response.Status.BAD_REQUEST, "3101 : Au moins 1 hydrants transmis n'existe pas ou n'est pas accessible à votre organisme");
        }
      }

      List<Long> communes = peiRepository.getDistinctIdsCommuneFromListPei(indispoForm.hydrants());

      if (communes.size() > 1) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "3102 : Les hydrants transmis sont situés sur des communes différentes");
      }

      return this.indispoTemporaireRepository.addIndispoTemporaire(indispo, indispoForm.hydrants());
    } catch (DateTimeParseException e) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "3001 : Une date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm");
    }

  }

  public void editIndispoTemporaire(String idIndispo, IndispoTemporaireSpecifiqueForm indispoForm) throws ResponseException {
    Long id = Long.valueOf(idIndispo);
    if (!this.isIndispoTemporaireAccessible(id)) {
      throw new ResponseException(Response.Status.FORBIDDEN, "3103 : L'indisponibilité temporaire demandée n'existe pas ou ne vous est pas accessible");
    }

    HydrantIndispoTemporaire indispo = hydrantIndispoTemporaireRepository.getById(id);

    TypeHydrantIndispoStatut typeHydrantIndispoStatut = typeHydrantIndispoStatutRepository.getByCode(indispoForm.statut());
    if (typeHydrantIndispoStatut == null) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "3000 : Le statut de l'indisponibilité temporaire ne correspond à aucune valeur connue");
    }

    indispo.setStatut(typeHydrantIndispoStatut.getId());

    try {
      // Vérifications sur les dates
      if (indispoForm.date_debut() != null) {
        indispo.setDateDebut(DateUtils.getMoment(indispoForm.date_debut()).toInstant());
      }

      if (indispoForm.date_fin() != null) {
        indispo.setDateFin(DateUtils.getMoment(indispoForm.date_fin()).toInstant());
      }

      if (indispo.getDateDebut() != null && indispo.getDateFin() != null && !indispo.getDateDebut().isBefore(indispo.getDateFin())) {
        throw new ResponseException(Response.Status.BAD_REQUEST, "3100 : La date de fin ne peut être égale ou antérieure à la date de début");
      }

      indispo.setMotif(indispoForm.motif());
      indispo.setBasculeAutoIndispo(indispoForm.bascule_auto_indispo());
      indispo.setBasculeAutoDispo(indispoForm.bascule_auto_dispo());
      indispo.setMelAvantIndispo(indispoForm.mel_avant_indispo());
      indispo.setMelAvantDispo(indispoForm.mel_avant_dispo());
      this.indispoTemporaireRepository.editIndispoTemporaie(indispo);
    } catch (DateTimeParseException e) {
      throw new ResponseException(Response.Status.BAD_REQUEST, "3001 : Une date spécifiée n'existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm");
    }
  }

  /**
   * Vérifie si une indisponibilité temporaire est accessible à l'utilisateur
   * Si au moins un PEI composant l'IT est accessible, l'indispo temporaire est entièrement accessible
   *
   * @param id L'identifiant de l'indisponibilité temporaire
   */
  private boolean isIndispoTemporaireAccessible(Long id) {
    List<String> hydrants = hydrantIndispoTemporaireRepository.getPeiNumerosFromId(id);
    return peiUseCase.listHydrantsAccessibilite(hydrants)
            .stream()
            .anyMatch(PeiUseCase.HydrantAccessibilite::isAccessible);
  }
}
