package fr.sdis83.remocra.web;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.sdis83.remocra.domain.remocra.ProfilDroit;
import fr.sdis83.remocra.service.ProfilDroitService;
import fr.sdis83.remocra.util.ExceptionUtils;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;

@RequestMapping("/profildroits")
@Controller
public class ProfilDroitController extends AbstractServiceableController<ProfilDroitService, ProfilDroit> {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ProfilDroitService service;

    @Override
    protected ProfilDroitService getService() {
        return service;
    }

    @RequestMapping(value = "/{to}/droits/copyfrom/{from}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS', 'CREATE')")
    public ResponseEntity<java.lang.String> copydroits(final @PathVariable Long from, final @PathVariable Long to) {
        try {
            getService().copyDroits(from, to);
            return new SuccessErrorExtSerializer(true, "Droits copiés").serialize();
        } catch (Exception e) {
            if (ExceptionUtils.getNestedExceptionWithClass(e, ConstraintViolationException.class) != null) {
                return new SuccessErrorExtSerializer(false, getConstraintViolationExceptionMsg()).serialize();
            }
            logger.error(e.getMessage(), e);
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/{pd}/droits", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS', 'CREATE')")
    public ResponseEntity<java.lang.String> copydroits(final @PathVariable Long pd) {
        try {
            getService().clearDroits(pd);
            return new SuccessErrorExtSerializer(true, "Droits vidés").serialize();
        } catch (Exception e) {
            if (ExceptionUtils.getNestedExceptionWithClass(e, ConstraintViolationException.class) != null) {
                return new SuccessErrorExtSerializer(false, getConstraintViolationExceptionMsg()).serialize();
            }
            logger.error(e.getMessage(), e);
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }
}
