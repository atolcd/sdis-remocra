package fr.sdis83.remocra.web;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import flexjson.JSONSerializer;
import flexjson.transformer.AbstractTransformer;
import fr.sdis83.remocra.domain.remocra.Alerte;
import fr.sdis83.remocra.domain.remocra.AlerteDocument;
import fr.sdis83.remocra.domain.remocra.AlerteElt;
import fr.sdis83.remocra.domain.remocra.AlerteEltAno;
import fr.sdis83.remocra.domain.remocra.Document;
import fr.sdis83.remocra.domain.remocra.Document.TypeDocument;
import fr.sdis83.remocra.domain.remocra.TypeAlerteAno;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.util.Feature;
import fr.sdis83.remocra.util.FeatureCollection;
import fr.sdis83.remocra.util.FeatureUtil;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import fr.sdis83.remocra.web.serialize.transformer.AlerteDocumentTransformer;
import java.util.Date;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/adresses")
@Controller
public class AdressesController extends AbstractRemocraController {

  // ------------------------------
  // --- Alertes
  // ------------------------------

  @RequestMapping(value = "/alerte", method = RequestMethod.GET)
  @PreAuthorize("hasRight('ADRESSES_C')")
  public ResponseEntity<String> alertes() {
    // Récupération des alertes filtrées par utilisateur
    List<Alerte> alertes =
        Alerte.findAlertesByRapporteurEquals(utilisateurService.getCurrentUtilisateur())
            .getResultList();

    // FeatureCollection spécifique "Alertes"
    FeatureCollection fc = new FeatureCollection();
    fc.getSerializer()
        .include("features.properties.documents")
        .transform(AlerteDocumentTransformer.getInstance(), AlerteDocument.class);
    return FeatureUtil.getResponse(alertes, fc);
  }

  @RequestMapping(
      value = "/alerte",
      method = RequestMethod.POST,
      headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('ADRESSES_C')")
  public ResponseEntity<String> alerte(
      MultipartHttpServletRequest request,
      final @RequestParam String jsonAlerte,
      final @RequestParam int fileCounter) {
    try {

      final Alerte alerte = Alerte.fromJsonToAlerte(jsonAlerte);

      if (alerte.getAlerteElts().size() < 1) {
        throw new Exception("Aucun élément n'a été envoyé");
      }

      alerte.setEtat(null);
      alerte.setDateModification(new Date());
      alerte.setRapporteur(utilisateurService.getCurrentUtilisateur());

      // Calcul de la géométrie
      Geometry[] geometries = new Geometry[alerte.getAlerteElts().size()];
      int j = 0;
      for (AlerteElt elt : alerte.getAlerteElts()) {
        elt.getGeometrie().setSRID(parametreProvider.get().getSridInt());
        geometries[j++] = elt.getGeometrie();
        elt.setAlerte(alerte);

        for (AlerteEltAno ano : elt.getAlerteEltAnos()) {
          ano.setAlerteElt(elt);
          ano.setTypeAlerteAno(TypeAlerteAno.findTypeAlerteAno(ano.getTypeAlerteAno().getId()));
        }
      }
      Point centroid = new GeometryCollection(geometries, new GeometryFactory()).getCentroid();
      centroid.setSRID(parametreProvider.get().getSridInt());
      alerte.setGeometrie(centroid);

      // Récupération des fichiers
      for (int i = 0; i < fileCounter; i++) {
        // Récupération des paramètres
        saveAlerteFile(request.getFile("doc" + i), alerte);
      }

      alerte.persist();

      // Réponse
      return new AbstractExtObjectSerializer<Feature>(
          "L'alerte a bien été créée.", SuccessErrorExtSerializer.DEFAULT_CONTENT_TYPE) {

        @Override
        protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {

          return super.additionnalIncludeExclude(serializer)
              .transform(
                  new AbstractTransformer() {
                    // les geometry sont déjà "formatées" en geojson, il ne
                    // faut donc
                    // pas
                    // les reformatter en tant que String
                    @Override
                    public void transform(Object object) {
                      getContext().write(object.toString());
                    }
                  },
                  "data.geometry")
              .include("data.properties.documents")
              .transform(AlerteDocumentTransformer.getInstance(), AlerteDocument.class);
        }

        @Override
        protected Feature getRecord() throws BusinessException {
          return alerte.toFeature();
        }
      }.serialize();

    } catch (Exception e) {
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
    }
  }

  protected void saveAlerteFile(MultipartFile mf, Alerte alerte) throws Exception {

    String depotRepertoire = parametreProvider.get().getDossierDepotAlerte();

    // Document "générique"
    Document d = createNonPersistedDocument(TypeDocument.ALERTE, mf, depotRepertoire);

    AlerteDocument ad = new AlerteDocument();
    ad.setAlerte(alerte);
    ad.setDocument(d);
    alerte.getAlerteDocuments().add(ad);

    d.persist();
  }
}
