package fr.sdis83.remocra.web;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.sdis83.remocra.domain.remocra.ParamConf;
import fr.sdis83.remocra.service.ParamConfService;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;

@RequestMapping("/paramconfs")
@Controller
public class ParamConfController {

    @Autowired
    protected ParamConfService paramConfService;

    @PersistenceContext
    private EntityManager entityManager;

    public ParamConfController() {
    }

    @RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS', 'CREATE')")
    public ResponseEntity<java.lang.String> listJson() {
        return new AbstractExtListSerializer<ParamConf>("ParamConf retrieved.") {
            @SuppressWarnings("unchecked")
            @Override
            protected List<ParamConf> getRecords() {
                return entityManager.createQuery("SELECT o FROM ParamConf o order by cle").getResultList();
            }
        }.serialize();
    }

    @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS', 'CREATE')")
    public ResponseEntity<java.lang.String> updateFromJson(final @RequestBody String json) {
        return new AbstractExtListSerializer<ParamConf>("ParamConf retrieved.") {
            @Override
            protected List<ParamConf> getRecords() {
                Collection<ParamConf> inParamConfs = ParamConf.fromJsonArrayToParamConfs(json);
                List<ParamConf> updated = paramConfService.update(inParamConfs);
                return updated;
            }
        }.serialize();
    }

    // ATTENTION : ici, le POST est considéré comme un PUT
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS', 'CREATE')")
    public ResponseEntity<java.lang.String> postFromJson(final @RequestBody String json) {
        return updateFromJson(json);
    }
}
