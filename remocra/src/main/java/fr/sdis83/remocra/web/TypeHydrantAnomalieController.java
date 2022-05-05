package fr.sdis83.remocra.web;

import java.util.List;
import java.util.Map;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.ProcessusEtl;
import fr.sdis83.remocra.service.XmlService;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vividsolutions.jts.geom.Geometry;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.TypeHydrantAnomalie;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.TypeHydrantAnomalieService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import fr.sdis83.remocra.web.serialize.transformer.GeometryTransformer;

@RequestMapping("/typehydrantanomalies")
@Controller
public class TypeHydrantAnomalieController {

    @Autowired
    TypeHydrantAnomalieService service;

    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson(final @RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            final @RequestParam(value = "sort", required = false) String sorts, final @RequestParam(value = "filter", required = false) String filters,
            final @RequestParam(value = "query", required = false) String query) {

        final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

        if (query != null && !query.isEmpty()) {
            itemFilterList.add(new ItemFilter("query", query));
        }

        return new AbstractExtListSerializer<TypeHydrantAnomalie>("fr.sdis83.remocra.domain.remocra.TypeHydrantAnomalie retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return new JSONSerializer().include("data.anomalieNatures", "data.anomalieNatures.saisies").exclude("*.class").transform(new GeometryTransformer(), Geometry.class);
            }

            @Override
            protected List<TypeHydrantAnomalie> getRecords() {
                return service.find(start, limit, sortList, itemFilterList);
            }

            @Override
            protected Long countRecords() {
                return service.count(itemFilterList);
            }

        }.serialize();
    }

    @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    public ResponseEntity<java.lang.String> create(final @RequestBody String json) {
        try {
            final TypeHydrantAnomalie attached = service.create(json, null);
            if (StringUtils.isEmpty(attached.getCode())) {
                String code = null;
               do {
                   code = attached.getNom().replaceAll("[^a-zA-Z0-9]", "").toUpperCase().substring(0, attached.getNom().length() > 5 ? 4 : attached.getNom().length()) + Math.round(Math.random() * 1000);
                   attached.setCode(code);
                   attached.flush();
               } while (!service.checkCodeExist(attached.getCode()));

            }
            return new AbstractExtObjectSerializer<TypeHydrantAnomalie>("TypeHydrantAnomalie created") {
                @Override
                protected TypeHydrantAnomalie getRecord() throws BusinessException {
                    return attached;
                }

            }.serialize();
        } catch (Exception e) {
            e.printStackTrace();
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    public ResponseEntity<java.lang.String> update(final @PathVariable Long id, final @RequestBody String json) {
        try {
            final TypeHydrantAnomalie attached = service.update(id, json, null);
            if (attached != null) {
                return new AbstractExtObjectSerializer<TypeHydrantAnomalie>("TypeHydrantAnomalie updated.") {
                    @Override
                    protected TypeHydrantAnomalie getRecord() throws BusinessException {
                        return attached;
                    }
                }.serialize();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
        return new SuccessErrorExtSerializer(false, "TypeHydrantAnomalie inexistant", HttpStatus.NOT_FOUND).serialize();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    public ResponseEntity<java.lang.String> delete(@PathVariable("id") Long id) {
        try {
            service.delete(id);
            return new SuccessErrorExtSerializer(true, "TypeHydrantAnomalie supprimé").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

}
