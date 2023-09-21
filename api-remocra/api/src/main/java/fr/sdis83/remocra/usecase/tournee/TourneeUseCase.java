package fr.sdis83.remocra.usecase.tournee;

import com.google.inject.Inject;
import fr.sdis83.remocra.repository.TourneeRepository;
import fr.sdis83.remocra.web.model.mobilemodel.ImmutableTourneeModel;
import fr.sdis83.remocra.web.model.mobilemodel.TourneeModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TourneeUseCase {
  @Inject TourneeRepository tourneeRepository;

  @Inject
  public TourneeUseCase(TourneeRepository tourneeRepository) {
    this.tourneeRepository = tourneeRepository;
  }

  public List<TourneeModel> getTourneesDisponibles(Long idOrganisme) {
    return tourneeRepository.getTourneesDisponibles(idOrganisme);
  }

  public ReservationTourneesResponse reserveTournees(List<Long> listIdTournees, Long idUser) {
    List<TourneeModel> tournees = tourneeRepository.getTourneesByIds(listIdTournees);

    List<TourneeModel> dejaReservees =
        tournees.stream().filter(t -> t.reservation() != null).collect(Collectors.toList());

    // On supprime les tournées déjà réservées
    tournees.removeAll(dejaReservees);

    // Puis, on réserve celle qu'on peut
    int result =
        tourneeRepository.reserveTournees(
            tournees.stream().map(TourneeModel::idRemocra).collect(Collectors.toList()), idUser);

    // Si on n'a pas réussi à réserver toutes les tournées
    if (result != tournees.size()) {
      throw new IllegalArgumentException("Erreur lors de la réservation des tournées " + tournees);
    }

    Map<Long, List<Long>> mapHydrantsByTournee =
        tourneeRepository.getHydrantsByTournee(
            tournees.stream().map(TourneeModel::idRemocra).collect(Collectors.toList()));
    ArrayList<TourneeModel> listeTourneeAvecHydrant = new ArrayList<>();
    tournees.stream()
        .forEach(
            tournee ->
                listeTourneeAvecHydrant.add(
                    ImmutableTourneeModel.builder()
                        .idRemocra(tournee.idRemocra())
                        .nom(tournee.nom())
                        .affectation(tournee.affectation())
                        .reservation(tournee.affectation())
                        .listeHydrant(mapHydrantsByTournee.get(tournee.idRemocra()))
                        .build()));

    // On retourne les tournées réservées et celles qu'on n'a pas pu réserver
    return new ReservationTourneesResponse(listeTourneeAvecHydrant, dejaReservees);
  }

  static class ReservationTourneesResponse {
    public final List<TourneeModel> tourneesReservees;
    public final List<TourneeModel> tourneesNonReservees;

    public ReservationTourneesResponse(
        List<TourneeModel> tourneesReservees, List<TourneeModel> tourneesNonReservees) {
      this.tourneesReservees = tourneesReservees;
      this.tourneesNonReservees = tourneesNonReservees;
    }
  }
}
