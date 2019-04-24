package fr.sdis83.remocra.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Organisme;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.OrganismeService;

import fr.sdis83.remocra.util.ExceptionUtils;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.naming.AuthenticationException;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;


@RequestMapping("/organismes")
@Controller
public class OrganismeController extends AbstractServiceableController<OrganismeService, Organisme> {

    @Autowired
    private OrganismeService service;

    @Override
    protected OrganismeService getService() {
        return service;
    }

    @Override
    protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.include("data.organismeParent.id", "data.organismeParent.code", "data.organismeParent.nom").exclude("data.zoneCompetence.geometrie", "data.organismeParent.*");
    }

    @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    @Override
    public ResponseEntity<String> create(final @RequestBody String json) {
        return doCreate(json);
    }

    @Override
    protected ResponseEntity<java.lang.String> doCreate(final String json) {
        try {
            final Organisme attached = getService().create(json, null);
            return new SuccessErrorExtSerializer(true, "Model created").serialize();
        } catch (Exception e) {
            if (ExceptionUtils.getNestedExceptionWithClass(e, ConstraintViolationException.class) != null) {
                return new SuccessErrorExtSerializer(false, getConstraintViolationExceptionMsg()).serialize();
            }
        }
        return new SuccessErrorExtSerializer(false, "Le modèle ne peut être ajouté").serialize();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    @Override
    public ResponseEntity<java.lang.String> update(final @PathVariable Long id, final @RequestBody String json) {
        return doUpdate(id, json);
    }

    @Override
    protected ResponseEntity<java.lang.String> doUpdate(final Long id, final String json) {
        try {
            final Organisme attached = getService().update(id, json, null);
            if (attached != null) {
                return new SuccessErrorExtSerializer(true, "Model updated").serialize();
            }
        } catch (Exception e) {
            if (ExceptionUtils.getNestedExceptionWithClass(e, ConstraintViolationException.class) != null) {
                return new SuccessErrorExtSerializer(false, getConstraintViolationExceptionMsg()).serialize();
            }
        }
        return new SuccessErrorExtSerializer(false, "Model inexistant", HttpStatus.NOT_FOUND).serialize();
    }
    
    @RequestMapping(value = "/removeOrganismeParentForSpecificType", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    protected ResponseEntity<java.lang.String> removeOrganismeParentForSpecificType(final Long idTypeOrganisme){
        try{
            service.removeOrganismeParentForSpecificType(idTypeOrganisme);
            return new SuccessErrorExtSerializer(true, "Organismes updated").serialize();
        }catch(Exception e){
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/nbOrganismesEnfants/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    protected ResponseEntity<java.lang.String> nbOrganismesEnfants(final @PathVariable Long id){
        try{
            int response = service.nbOrganismesAvecParentEtProfilSpecifique(id);
            return new SuccessErrorExtSerializer(true, String.valueOf(response)).serialize();
        }catch(Exception e){
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }

    }

    @RequestMapping(value = "/removeOrganismeParentForSpecificParent", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    protected ResponseEntity<java.lang.String> removeOrganismeParentForSpecificParent(final Long idOrganisme) {
        try {
            service.removeOrganismeParentForSpecificParent(idOrganisme);
            return new SuccessErrorExtSerializer(true, "Organismes updated").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/organismeetenfants", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<java.lang.String> getOrganismeAndChildrenId(){
        try {
            return new AbstractExtListSerializer<Integer>("Data retrieved.") {
                @Override
                protected List<Integer> getRecords() throws BusinessException, AuthenticationException {
                    return Organisme.getOrganismeAndChildren((utilisateurService.getCurrentUtilisateur().getOrganisme().getId()).intValue());
                }
            }.serialize();
        }catch (Exception e) {
                return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    /**
     * Renvoie un objet JSON contenant les données des organismes pouvant être autorité de police DECI d'un PEI
     * Les organismes acceptés sont les communes, epci et préfectures dont la zone de compétence comprend le PEI
     * @param geometrie La géométrie (de type POINT) du PEI
     * @return Les données au format JSON
     */
    @RequestMapping(value = "/autoritepolicedeci", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<String> getAvailableAutoritePolice(final @RequestParam(value="geometrie") String geometrie) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json;charset=utf-8");
        return new ResponseEntity<String>(service.getAvailableOrganismes(geometrie, Arrays.asList("COMMUNE", "EPCI", "PREFECTURE")).toString(), responseHeaders, HttpStatus.OK);
    }

    /**
     * Renvoie un objet JSON contenant les données des organismes pouvant être gestionnaire d'un PEI
     * Les organismes acceptés sont les communes et les epci dont la zone de compétence comprend le PEI
     * @param geometrie La géométrie (de type POINT) du PEI
     * @return Les données au format JSON
     */
    @RequestMapping(value = "/gestionnairepublic", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<String> getAvailableGestionnairePublic(final @RequestParam(value="geometrie") String geometrie) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json;charset=utf-8");
        return new ResponseEntity<String>(service.getAvailableOrganismes(geometrie, Arrays.asList("COMMUNE", "EPCI")).toString(), responseHeaders, HttpStatus.OK);
    }
}