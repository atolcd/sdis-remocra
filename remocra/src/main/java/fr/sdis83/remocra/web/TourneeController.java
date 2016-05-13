package fr.sdis83.remocra.web;

import java.io.StringWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(value = "", headers = "Accept=application/json")
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
            protected List<Tournee> getRecords() {
                return tourneeService.find(start, limit, sortList, itemFilterList);
            }

            @Override
            protected Long countRecords() {
                return tourneeService.count(itemFilterList);
            }

        }.serialize();
    }

    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/xml")
    public ResponseEntity<String> getTourneeDisponible() {
        List<Tournee> tournees = tourneeService.getTourneeDisponible(utilisateurService.getCurrentUtilisateur());

        StringWriter stringWriter = new StringWriter();
        stringWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        stringWriter.append("<tourneesDispo>");
        for (Tournee tournee : tournees) {
            stringWriter.append("<tournee>");
            stringWriter.append(tournee.getId().toString());
            stringWriter.append("</tournee>");
        }
        stringWriter.append("</tourneesDispo>");
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(stringWriter.toString(), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
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
}
