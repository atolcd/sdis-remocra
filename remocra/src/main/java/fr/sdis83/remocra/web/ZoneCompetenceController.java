package fr.sdis83.remocra.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.ZoneCompetence;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.service.ZoneCompetenceService;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;

@RequestMapping("/zonecompetences")
@Controller
public class ZoneCompetenceController extends AbstractServiceableController<ZoneCompetenceService, ZoneCompetence> {

    @Autowired
    private ZoneCompetenceService service;

    @Autowired
    private UtilisateurService serviceUtilisateur;

    @Override
    protected ZoneCompetenceService getService() {
        return service;
    }

    @Override
    protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.exclude("data.geometrie");
    }

    @RequestMapping(value = "/check", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<java.lang.String> checkByXY(final @RequestParam(value = "srid", required = true) Integer srid,
            final @RequestParam(value = "wkt", required = true) String wkt) {
        try {
            Boolean result = service.check(wkt, srid, serviceUtilisateur.getCurrentUtilisateur().getOrganisme().getZoneCompetence());
            return new SuccessErrorExtSerializer(result, "zonecompetences - check " + (result ? "OK" : "NOK")).serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, "zonecompetences - check").serialize();
        }

    }
}
