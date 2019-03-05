package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.util.ExceptionUtils;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.sdis83.remocra.domain.remocra.TypeOrganisme;
import fr.sdis83.remocra.service.TypeOrganismeService;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.ConstraintViolationException;

@RequestMapping("/typeorganisme")
@Controller
public class TypeOrganismeController extends AbstractServiceableController<TypeOrganismeService, TypeOrganisme> {

    @Autowired
    private TypeOrganismeService service;

    @Override
    protected TypeOrganismeService getService() {
        return service;
    }

    @Override
    protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.include("data.typeOrganismeParent.id", "data.typeOrganismeParent.code", "data.typeOrganismeParent.nom").exclude("data.typeOrganismeParent.*");
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
            final TypeOrganisme attached = getService().create(json, null);
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
            final TypeOrganisme attached = getService().update(id, json, null);
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

    @RequestMapping(value = "/nbOrganismesAvecParentEtProfilSpecifique/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    protected ResponseEntity<java.lang.String> nbOrganismesAvecParentEtProfilSpecifique(final @PathVariable Long id){
        try{
            int response = service.nbOrganismesAvecParentEtProfilSpecifique(id);
            return new SuccessErrorExtSerializer(true, String.valueOf(response)).serialize();
        }catch(Exception e){
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }

    }
}
