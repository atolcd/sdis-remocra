package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ParamConf;
import fr.sdis83.remocra.domain.remocra.Commune;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.repository.CommuneRepository;
import fr.sdis83.remocra.repository.OrganismeRepository;
import fr.sdis83.remocra.repository.ParamConfRepository;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/communes")
@Controller
public class CommuneController {

  @Autowired private UtilisateurService utilisateurService;
  @Autowired private CommuneRepository communeRepository;
  @Autowired private ParamConfRepository paramConfRepository;
  @Autowired private OrganismeRepository organismeRepository;
  @Autowired private ParamConfService paramConfService;

  private final Logger logger = Logger.getLogger(getClass());

  @RequestMapping(value = "/nom", headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> listNomJson(
      final @RequestParam(value = "page", required = false) Integer page,
      final @RequestParam(value = "start", required = false) Integer start,
      final @RequestParam(value = "withgeom", required = false, defaultValue = "true") boolean
              withgeom,
      final @RequestParam(value = "limit", required = false) Integer limit,
      final @RequestParam(value = "query", required = false) String query,
      @RequestParam(value = "sort", required = false) String sorts,
      final @RequestParam(value = "filter", required = false) String filters) {

    final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
    final ItemSorting itemSorting =
        sortList.isEmpty() ? new ItemSorting("nom", "ASC") : sortList.get(0);

    return new AbstractExtListSerializer<fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune>(
        "Communes retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer
            .include("data.id")
            .include("data.nom")
            .include("data.insee")
            .include("data.pprif")
            .include("data.bbox")
            .include(withgeom ? "data.geometrie" : "")
            .exclude("*");
      }

      @Override
      protected List<fr.sdis83.remocra.db.model.remocra.tables.pojos.Commune> getRecords() {
        // On récupère les organismes accessibles à l'utilisateur (son organisme et les enfants de
        // cet organisme)
        SecurityContext sc = SecurityContextHolder.getContext();
        Authentication aut = sc.getAuthentication();
        List<Integer> organismes = Collections.EMPTY_LIST;

        // Si on n'a pas d'utilisateur connecté, on ne va pas chercher les organismes
        if (aut != null && aut.isAuthenticated() && !"anonymousUser".equals(aut.getPrincipal())) {
          fr.sdis83.remocra.db.model.remocra.tables.pojos.Organisme organismeUtilisateur =
              organismeRepository.getOrganismeWithIdUser(
                  utilisateurService.getCurrentUtilisateur().getId());
          organismes = Organisme.getOrganismeAndChildren(organismeUtilisateur.getId().intValue());
        }

        // On va regarder pour filtrer sur uniquement les communes du département puisqu'il est
        // possible pour des raisons métier d'avoir les communes limitrophes d'autres départements
        String dep = null;
        ParamConf paramConf =
            paramConfRepository.getByCle(
                fr.sdis83.remocra.domain.remocra.ParamConf.ParamConfParam
                    .COMMUNES_INSEE_LIKE_FILTRE_SQL
                    .getCle());
        try {
          dep = paramConf.getValeur();
        } catch (Exception e) {
          logger.error(
              "Paramètre " + paramConf.getCle() + ", restitution de la valeur par défaut", e);
        }

        if (dep == null) {
          dep = "83%";
        }

        if (dep.trim().isEmpty()) {
          dep = "%"; // tout
        }

        // On retourne la liste des communes en fonction des critères
        return communeRepository.getListCommune(
            organismes, query, dep, itemSorting.isAsc(), limit, start, withgeom);
      }
    }.serialize();
  }

  @RequestMapping(value = "/xy", headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> findCommuneByPoint(
      final @RequestParam(value = "srid", required = true) Integer srid,
      final @RequestParam(value = "wkt", required = true) String wkt) {
    return new AbstractExtListSerializer<Commune>("Communes retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer
            .include("data.id")
            .include("data.nom")
            .include("data.insee")
            .include("data.pprif")
            .include("data.geometrie")
            .exclude("*");
      }

      @Override
      protected List<Commune> getRecords() {
        Long delta = paramConfService.getHydrantToleranceCommuneMetres();
        return Commune.findCommunesByPointWithDelta(srid, wkt, delta);
      }
    }.serialize();
  }
}
