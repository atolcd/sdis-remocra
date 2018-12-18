package fr.sdis83.remocra.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Document.TypeDocument;
import fr.sdis83.remocra.domain.remocra.Permis;
import fr.sdis83.remocra.domain.remocra.PermisDocument;
import fr.sdis83.remocra.domain.remocra.TypeDroit.TypeDroitEnum;
import fr.sdis83.remocra.domain.remocra.ZoneCompetence;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.security.AuthoritiesUtil;
import fr.sdis83.remocra.service.PermisService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.service.ZoneCompetenceService;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;

@RequestMapping("/permis")
@Controller
public class PermisController extends AbstractRemocraController {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ZoneCompetenceService zoneCompetenceService;

    @Autowired
    private PermisService permisService;

    @Autowired
    private AuthoritiesUtil authUtils;

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Utilisé partout dans le contrôleur
     * 
     * @param serializer
     * @return
     */
    protected static JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer
        // Commune
                .include("data.commune.id").include("data.commune.nom").include("data.commune.pprif").exclude("data.commune.*")
                // Documents
                .include("data.permisDocuments.id").include("data.permisDocuments.titre").include("data.permisDocuments.code").exclude("data.permisDocuments.*");
    }

    @RequestMapping(value = "/searchxy", headers = "Accept=application/json")
    @PreAuthorize("hasRight('PERMIS_R')")
    public ResponseEntity<java.lang.String> searchPermis(final @RequestParam(value = "srid", required = true) Integer srid,
            final @RequestParam(value = "x", required = true) Float x, final @RequestParam(value = "y", required = true) Float y) {

        return new AbstractExtListSerializer<Permis>("Permis retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return PermisController.additionnalIncludeExclude(serializer);
            }

            @Override
            protected List<Permis> getRecords() {
                ZoneCompetence zc = utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence();
                Integer tolerance = paramConfService.getToleranceChargementMetres();
                int firstResult = 0;
                int maxResults = 10;
                return Permis.findPermisByXYTolerance(srid.intValue(), x, y, tolerance, firstResult, maxResults, zc.getGeometrie());
            }

        }.serialize();
    }

    @RequestMapping(value = "/search", headers = "Accept=application/json")
    @PreAuthorize("hasRight('PERMIS_R')")
    public ResponseEntity<java.lang.String> searchPermis(final @RequestParam(value = "nom", required = false) String nom,
            final @RequestParam(value = "commune", required = false) Integer commune, final @RequestParam(value = "numero", required = false) String numero,
            final @RequestParam(value = "sectionCadastrale", required = false) String sectionCadastrale,
            final @RequestParam(value = "parcelleCadastrale", required = false) String parcelleCadastrale, final @RequestParam(value = "avis", required = false) Long avis) {
        return new AbstractExtListSerializer<Permis>("Permis retrieved.") {

            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                return PermisController.additionnalIncludeExclude(serializer);
            }

            @Override
            protected List<Permis> getRecords() {
                int srid = 2154;
                int firstResult = 0;
                int maxResults = 10;
                Geometry geom = utilisateurService.getCurrentUtilisateur().getOrganisme().getZoneCompetence().getGeometrie();
                return Permis.findPermisByNomCommEtc(srid, nom, commune, numero, sectionCadastrale, parcelleCadastrale, avis, firstResult, maxResults, geom);
            }

        }.serialize();
    }

    private void checkZoneCompetence(Point geom) {
        if (!zoneCompetenceService.check(geom, utilisateurService.getCurrentZoneCompetenceId())) {
            throw new AccessDeniedException("Vous n'avez pas les autorisations sur cette zone géographique");
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRight('PERMIS_C')")
    public ResponseEntity<java.lang.String> deletePermis(@PathVariable("id") Long id) {
        try {
            Permis attached = Permis.findPermis(id);
            checkZoneCompetence(attached.getGeometrie());
            attached.remove();
            attached.flush();
            return new SuccessErrorExtSerializer(true, "Permis supprimé").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    /**
     * Mise à jour d'un permis.
     * 
     * @param id
     * @param request
     * @param jsonAlerte
     * @param fileCounter
     * @return
     */
    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
    @PreAuthorize("hasRight('PERMIS_C')")
    public ResponseEntity<java.lang.String> updatePermis(final @PathVariable("id") Long id, MultipartHttpServletRequest request, final @RequestParam String jsonPermis,
            final @RequestParam int fileCounter) {
        try {
            Permis record = Permis.fromJsonToPermis(jsonPermis);
            record.getGeometrie().setSRID(2154);
            checkZoneCompetence(record.getGeometrie());
            if (!id.equals(record.getId())) {
                logger.error("Requête non valide : idUrl = " + id + " est different de " + record.getId());
                return new SuccessErrorExtSerializer(false, "Requête non valide").serialize();
            }
            final Permis permis = permisService.updatePermis(record);

            if (authUtils.hasRight(TypeDroitEnum.PERMIS_DOCUMENTS_C)) {
                // Récupération des fichiers
                for (int i = 0; i < fileCounter; i++) {
                    // Récupération des paramètres
                    savePermisFile(request.getFile("doc" + i), permis);
                }
            } else {
                logger.error("Dépôt de documents refusés (" + utilisateurService.getCurrentUtilisateur().getIdentifiant() + ")");
            }

            return new AbstractExtObjectSerializer<Permis>("Permis created.", SuccessErrorExtSerializer.DEFAULT_CONTENT_TYPE) {
                @Override
                protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                    return PermisController.additionnalIncludeExclude(serializer);
                }

                @Override
                protected Permis getRecord() throws BusinessException {
                    return permis;
                }
            }.serialize();
        } catch (Exception e) {
            e.printStackTrace();
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    /**
     * Création d'un permis.
     * 
     * @param request
     * @param jsonPermis
     * @param fileCounter
     * @return
     */
    @Transactional
    @RequestMapping(value = "", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
    @PreAuthorize("hasRight('PERMIS_C')")
    public ResponseEntity<java.lang.String> createPermis(MultipartHttpServletRequest request, final @RequestParam String jsonPermis, final @RequestParam int fileCounter) {
        try {
            // sauvegarde du permis
            Permis record = Permis.fromJsonToPermis(jsonPermis);
            final Permis permis = permisService.createPermis(record);
            checkZoneCompetence(permis.getGeometrie());

            if (authUtils.hasRight(TypeDroitEnum.PERMIS_DOCUMENTS_C)) {
                // Récupération des fichiers
                for (int i = 0; i < fileCounter; i++) {
                    // Récupération des paramètres
                    savePermisFile(request.getFile("doc" + i), permis);
                }
            } else {
                logger.error("Dépôt de documents refusés (" + utilisateurService.getCurrentUtilisateur().getIdentifiant() + ")");
            }

            return new AbstractExtObjectSerializer<Permis>("Permis created.", SuccessErrorExtSerializer.DEFAULT_CONTENT_TYPE) {
                @Override
                protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                    return PermisController.additionnalIncludeExclude(serializer);
                }

                @Override
                protected Permis getRecord() throws BusinessException {
                    return permis;
                }
            }.serialize();

        } catch (Exception e) {
            e.printStackTrace();
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    protected void savePermisFile(MultipartFile mf, Permis permis) throws Exception {

        String depotRepertoire = paramConfService.getDossierDepotPermis();

        // Document "générique"
        Document d = createNonPersistedDocument(TypeDocument.PERMIS, mf, depotRepertoire);

        PermisDocument pd = new PermisDocument();
        pd.setPermis(permis);
        pd.setDocument(d);
        permis.getPermisDocuments().add(pd);

        d.persist();
    }

}
