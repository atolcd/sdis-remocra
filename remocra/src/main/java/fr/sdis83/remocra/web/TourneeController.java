package fr.sdis83.remocra.web;

import java.io.StringWriter;
import java.util.List;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.repository.HydrantVisiteRepository;
import fr.sdis83.remocra.util.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.sdis83.remocra.domain.remocra.Tournee;
import fr.sdis83.remocra.service.TourneeService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;

@RequestMapping("/tournees")
@Controller
public class TourneeController {

    @Autowired
    private TourneeService tourneeService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private HydrantVisiteRepository hydrantVisiteRepository;

    @RequestMapping(value = "", headers = "Accept=application/json")
    @PreAuthorize("hasRight('TOURNEE_R')")
    public ResponseEntity<java.lang.String> listJson(final @RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            final @RequestParam(value = "sort", required = false) String sorts, final @RequestParam(value = "filter", required = false) String filters,
            final @RequestParam(value = "query", required = false) String query) {

        final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

        if (query != null && !query.isEmpty()) {
            itemFilterList.add(new ItemFilter("query", query));
        }

        ItemFilter affectation = ItemFilter.getFilter(itemFilterList, "affectation");
        if (affectation == null) {
            itemFilterList.add(new ItemFilter("affectation", utilisateurService.getCurrentUtilisateur().getOrganisme().getId().toString()));
        }

        return new AbstractExtListSerializer<Tournee>("fr.sdis83.remocra.domain.remocra.Tournee retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return serializer
                    // anomalies
                    .include("data.hydrantCount")
                    .exclude("data.affectation.zoneCompetence.geometrie")
                    .exclude("data.reservation.organisme.zoneCompetence.geometrie")
                    .exclude("data.affectation.organismeParent.zoneCompetence.geometrie")
                    .exclude("data.affectation.organismeParent.profilOrganisme.typeOrganisme.typeOrganismeParent")
                    .exclude("data.affectation.organismeParent.typeOrganisme.typeOrganismeParent.typeOrganismeParent")
                    .exclude("data.affectation.organismeParent.organismeParent");
            }
            @Override
            protected List<Tournee> getRecords() {
                return tourneeService.find(start, limit, sortList, itemFilterList);
            }

            @Override
            protected Long countRecords() {
                return tourneeService.count(itemFilterList);
            }

        }.serialize();
    }
    @RequestMapping(value = "", method = RequestMethod.GET, headers = {"Accept=application/xml", "Accept=application/xml;charset=utf-8"})
    @PreAuthorize("hasRight('TOURNEE_R')")
    public ResponseEntity<String> getTourneeDisponible(
            final @RequestParam(value = "dispo", required = false, defaultValue = "true") boolean dispo
    ) {
        List<Tournee> tournees = dispo ?
                tourneeService.getTourneeDisponible(utilisateurService.getCurrentUtilisateur())
                : tourneeService.getTourneeAll(utilisateurService.getCurrentUtilisateur());

        StringWriter stringWriter = new StringWriter();
        stringWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        stringWriter.append("<tournees"+(dispo?"Dispo":"")+">");
        for (Tournee tournee : tournees) {
            stringWriter.append("<tournee nom=\""+tournee.getNom().toString().replaceAll("\"", "&quot;")+"\">");
            stringWriter.append(tournee.getId().toString());
            stringWriter.append("</tournee>");
        }
        stringWriter.append("</tournees"+(dispo?"Dispo":"")+">");
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(stringWriter.toString(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("hasRight('TOURNEE_C')")
    public ResponseEntity<java.lang.String> deleteTournee(@PathVariable("id") Long id) {
        try {
            Tournee attached = Tournee.findTournee(id);
            attached.remove();
            attached.flush();
            return new SuccessErrorExtSerializer(true, "Tournée supprimée").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/cancelreservation/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasRight('TOURNEE_RESERVATION_D')")
    public ResponseEntity<java.lang.String> cancelReservationTournee(@PathVariable("id") Long id) {
        try {
            Tournee attached = Tournee.findTournee(id);
            attached.setReservation(null);
            attached.flush();
            return new SuccessErrorExtSerializer(true, "Réservation de la tournée annulée").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/resetTournee/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasRight('TOURNEE_POURCENTAGE_C')")
    public ResponseEntity<java.lang.String> resettournee(@PathVariable("id") Long id) {
        try {
            Tournee attached = Tournee.findTournee(id);
            attached.setEtat(0);
            attached.flush();
            return new SuccessErrorExtSerializer(true, "La tournée "+ attached.getNom()+" a été réinitialisée").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/finaliseTournee/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasRight('TOURNEE_POURCENTAGE_C')")
    public ResponseEntity<java.lang.String> finalisetournee(@PathVariable("id") Long id) {
        try {
            Tournee attached = Tournee.findTournee(id);
            attached.setEtat(100);
            attached.flush();
            return new SuccessErrorExtSerializer(true, "La tournée "+ attached.getNom()+" a été finalisée").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/renameTournee/{id}", headers = "Accept=application/json")
    @PreAuthorize("hasRight('TOURNEE_C')")
    public ResponseEntity<java.lang.String> renametournee(@PathVariable("id") Long id,final @RequestParam(value = "nom") String nom) {
        try {
            Tournee attached = Tournee.findTournee(id);
            attached.setNom(nom);
            attached.flush();
            return new SuccessErrorExtSerializer(true, "La tournée "+ attached.getNom()+" a été renommée").serialize();
        } catch (Exception e) {
            if (ExceptionUtils.getNestedExceptionWithClass(e, ConstraintViolationException.class) != null) {
                return new SuccessErrorExtSerializer(false, "Une tournée avec le nom "+ nom + " existe déjà").serialize();
            }
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();

        }
    }

    @RequestMapping(value = "/getHydrantTournee/{id}", headers = "Accept=application/json")
    @PreAuthorize("hasRight('TOURNEE_R') and hasRight('HYDRANTS_R')")
    public ResponseEntity<java.lang.String> getHydrantTournee(@PathVariable("id") Long id) {
        try {
            StringBuffer sb = new StringBuffer("<ul>");
            List<String> listeHydrants = tourneeService.getNumHydrants(id);

            for(String numero : listeHydrants)
                sb.append("<li>").append(numero).append("</li>");

            sb.append("</ul>");
            return new SuccessErrorExtSerializer(true, sb.toString()).serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/saisievisite", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> saisievisite(final @RequestBody String json) {
        try {
            String str = hydrantVisiteRepository.addVisiteFromTournee(json);
            return new SuccessErrorExtSerializer(true, str).serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }
}
