package fr.sdis83.remocra.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.AbstractService;
import fr.sdis83.remocra.util.ExceptionUtils;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;

// TODO voir si on crée une classe Model ou une interface IModel
public abstract class AbstractServiceableController<ServiceT extends AbstractService<ModelT>, ModelT extends Object> extends AbstractRemocraController {

    private final Logger logger = Logger.getLogger(getClass());

    protected abstract ServiceT getService();

    protected String getConstraintViolationExceptionMsg() {
        return "Une contrainte n'est pas respectée.<br/>Veuillez modifier votre saisie.";
    }

    protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer;
    }

    @RequestMapping(headers = "Accept=application/json")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<java.lang.String> listJson(@RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "sort", required = false) String sorts, @RequestParam(value = "filter", required = false) String filters,
            @RequestParam(value = "query", required = false) String query) {
        return this.doListJson(page, start, limit, sorts, filters, query);
    }

    protected ResponseEntity<java.lang.String> doListJson(Integer page, final Integer start, final Integer limit, String sorts, String filters, String query) {
        final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);
        if (query != null && !query.isEmpty()) {
            itemFilterList.add(new ItemFilter("query", query));
        }

        return new AbstractExtListSerializer<ModelT>("Model retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return AbstractServiceableController.this.additionnalIncludeExclude(serializer);
            }

            @Override
            protected List<ModelT> getRecords() {
                return getService().find(start, limit, sortList, itemFilterList);
            }

            @Override
            protected Long countRecords() {
                return getService().count(itemFilterList);
            }
        }.serialize();
    }

    @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS', 'CREATE')")
    public ResponseEntity<java.lang.String> create(final @RequestBody String json) {
        return this.doCreate(json);
    }

    protected ResponseEntity<java.lang.String> doCreate(final String json) {
        try {
            final ModelT attached = getService().create(json, null);
            return new AbstractExtObjectSerializer<ModelT>("Model created") {
                @Override
                protected ModelT getRecord() throws BusinessException {
                    return attached;
                }

            }.serialize();
        } catch (Exception e) {
            if (ExceptionUtils.getNestedExceptionWithClass(e, ConstraintViolationException.class) != null) {
                // Contrainte non respectée. Par pour le ModelT Droit,
                // certainement l'unicité : (TypeDroit,ProfilDroit)
                return new SuccessErrorExtSerializer(false, getConstraintViolationExceptionMsg()).serialize();
            }
            logger.error(e.getMessage(), e);
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS', 'CREATE')")
    public ResponseEntity<java.lang.String> update(final @PathVariable Long id, final @RequestBody String json) {
        return this.doUpdate(id, json);
    }

    protected ResponseEntity<java.lang.String> doUpdate(final Long id, final String json) {
        try {
            final ModelT attached = getService().update(id, json, null);
            if (attached != null) {
                return new AbstractExtObjectSerializer<ModelT>("Model updated.") {
                    @Override
                    protected ModelT getRecord() throws BusinessException {
                        return attached;
                    }
                }.serialize();
            }
        } catch (Exception e) {
            if (ExceptionUtils.getNestedExceptionWithClass(e, ConstraintViolationException.class) != null) {
                // Contrainte non respectée. Par pour le ModelT Droit,
                // certainement l'unicité : (TypeDroit,ProfilDroit)
                return new SuccessErrorExtSerializer(false, getConstraintViolationExceptionMsg()).serialize();
            }
            logger.error(e.getMessage(), e);
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
        return new SuccessErrorExtSerializer(false, "Model inexistant", HttpStatus.NOT_FOUND).serialize();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS', 'CREATE')")
    public ResponseEntity<java.lang.String> delete(@PathVariable("id") Long id) {
        return this.doDelete(id);
    }

    protected ResponseEntity<java.lang.String> doDelete(Long id) {
        try {
            getService().delete(id);
            return new SuccessErrorExtSerializer(true, "Model supprimé").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }
}
