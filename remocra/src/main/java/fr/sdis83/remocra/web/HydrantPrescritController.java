package fr.sdis83.remocra.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.HydrantPrescrit;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.HydrantPrescritService;
import fr.sdis83.remocra.util.FeatureUtil;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;

@RequestMapping("/hydrantprescrits")
@Controller
public class HydrantPrescritController extends AbstractServiceableController<HydrantPrescritService, HydrantPrescrit> {

    @Autowired
    private HydrantPrescritService service;

    @Override
    protected HydrantPrescritService getService() {
        return service;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS_PRESCRIT', 'READ')")
    public ResponseEntity<java.lang.String> read(final @RequestParam(value = "id", required = true) Long id) {
        return new AbstractExtObjectSerializer<HydrantPrescrit>("fr.sdis83.remocra.domain.remocra.HydrantPrescrit retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return serializer.exclude("data.commune.geometrie");
            }

            @Override
            protected HydrantPrescrit getRecord() throws BusinessException {
                return service.getById(id);
            }
        }.serialize();
    }

    @RequestMapping(value = "/layer", method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS_PRESCRIT', 'CREATE')")
    public ResponseEntity<java.lang.String> layer(final @RequestParam String bbox) {
        if (bbox == null || bbox.isEmpty()) {
            return FeatureUtil.getResponse(service.findAllHydrantPrescrits());
        } else {
            return FeatureUtil.getResponse(service.findHydrantPrescritsByBBOX(bbox));
        }
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS_PRESCRIT', 'CREATE')")
    public ResponseEntity<java.lang.String> update(@PathVariable Long id, @RequestBody String json) {
        return this.doUpdate(id, json);
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS_PRESCRIT', 'CREATE')")
    public ResponseEntity<String> create(@RequestBody String json) {
        return this.doCreate(json);
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("hasRight('HYDRANTS_PRESCRIT', 'CREATE')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return this.doDelete(id);
    }

}
