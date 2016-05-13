package fr.sdis83.remocra.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.BlocDocument;
import fr.sdis83.remocra.domain.remocra.ProfilDroit;
import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.security.AccessRight.Permission;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.service.BlocDocumentService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;

@RequestMapping("/blocdocuments")
@Controller
public class BlocDocumentController extends AbstractRemocraController {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private BlocDocumentService blocDocumentService;

    @Autowired
    private AuthoritiesUtil authUtils;

    @RequestMapping(value = "", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
    @PreAuthorize("hasRight('DOCUMENTS', 'CREATE')")
    public ResponseEntity<java.lang.String> create(MultipartHttpServletRequest request) {
        String json = request.getParameter("data");
        Map<String, MultipartFile> files = request.getFileMap();
        if (files.size() != 1) {
            return new SuccessErrorExtSerializer(false, "Le fichier est obligatoire").serialize();
        }
        try {
            final BlocDocument blocDocument = blocDocumentService.create(json, files);
            return new AbstractExtObjectSerializer<BlocDocument>("BlocDocument created", SuccessErrorExtSerializer.DEFAULT_CONTENT_TYPE) {
                @Override
                protected BlocDocument getRecord() throws BusinessException {
                    return blocDocument;
                }
            }.serialize();
        } catch (Exception e) {
            logger.error(e);
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(headers = "Accept=application/json")
    @PreAuthorize("hasRight('DOCUMENTS', 'READ')")
    public ResponseEntity<java.lang.String> list(@RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "sort", required = false) String sorts, @RequestParam(value = "filter", required = false) String filters) {
        final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

        // Tri par défaut (date)
        if (sortList.isEmpty()) {
            sortList.add(new ItemSorting("dateDoc", "DESC"));
        }

        ProfilDroit profilDroit;
        try {
            profilDroit = utilisateurService.getCurrentProfilDroit();
        } catch (BusinessException e) {
            // Sans doute pas de ProfilDroit : on retourne une liste vide
            return new AbstractExtListSerializer<BlocDocument>("Model retrieved.") {
                @Override
                protected List<BlocDocument> getRecords() {
                    return new LinkedList<BlocDocument>();
                }
            }.serialize();
        }

        // Pas le droit d'administrer les documents (consultation simple)
        if (!authUtils.hasRight(TypeDroitEnum.DOCUMENTS, Permission.CREATE)) {
            itemFilterList.add(new ItemFilter("profilDroitId", profilDroit.getId().toString()));
        }

        return new AbstractExtListSerializer<BlocDocument>("Model retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                // TODO voir quand filtrer les profils et thématiques (doivent
                // être présents en administration uniquement de préférence)
                return serializer
                // Profils de droits
                        .include("data.profilDroits.id").include("data.profilDroits.nom").exclude("data.profilDroits.*")
                        // Thématiques
                        .include("data.thematiques.id").include("data.thematiques.nom").exclude("data.thematiques.*")
                        // Autres
                        .exclude("data.document");
            }

            @Override
            protected List<BlocDocument> getRecords() {
                return blocDocumentService.find(start, limit, sortList, itemFilterList);
            }

            @Override
            protected Long countRecords() {
                return blocDocumentService.count(itemFilterList);
            }
        }.serialize();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
    @PreAuthorize("hasRight('DOCUMENTS', 'CREATE')")
    public ResponseEntity<java.lang.String> update(MultipartHttpServletRequest request, @PathVariable("id") Long id) {
        String json = request.getParameter("data");
        Map<String, MultipartFile> files = request.getFileMap();
        try {
            final BlocDocument blocDocument = blocDocumentService.update(id, json, files);
            return new AbstractExtObjectSerializer<BlocDocument>("BlocDocument updated", SuccessErrorExtSerializer.DEFAULT_CONTENT_TYPE) {
                @Override
                protected BlocDocument getRecord() throws BusinessException {
                    return blocDocument;
                }
            }.serialize();
        } catch (Exception e) {
            logger.error(e);
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("hasRight('DOCUMENTS', 'CREATE')")
    public ResponseEntity<java.lang.String> delete(@PathVariable("id") Long id) {
        try {
            blocDocumentService.delete(id);
            return new SuccessErrorExtSerializer(true, "BlocDocument supprimé").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }
}
