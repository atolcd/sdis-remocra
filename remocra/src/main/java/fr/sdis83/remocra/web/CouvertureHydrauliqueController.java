package fr.sdis83.remocra.web;

import fr.sdis83.remocra.repository.CouvertureHydrauliqueRepository;
import fr.sdis83.remocra.web.model.PlusProchePei;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RequestMapping("/couverturehydraulique")
@Controller
public class CouvertureHydrauliqueController {

  @Autowired
  private CouvertureHydrauliqueRepository couvertureHydrauliqueRepository;

  @RequestMapping(value = "/calcul", method = RequestMethod.POST,  headers = "Content-Type=multipart/form-data")
  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  @Transactional
  public ResponseEntity<String> calcul(MultipartHttpServletRequest request) {
    try {
      String hydrantsExistants = request.getParameter("hydrantsExistants");
      String hydrantsProjet = request.getParameter("hydrantsProjet");
      String etude = request.getParameter("etude");
      String useReseauImporte = request.getParameter("reseauImporte");

      couvertureHydrauliqueRepository.calcul(hydrantsExistants, hydrantsProjet, etude, useReseauImporte);
      return new SuccessErrorExtSerializer(true, "La couverture hydraulique a bien été tracée").serialize();
    }catch(Exception e){
      e.printStackTrace();
      return new SuccessErrorExtSerializer(false, "Une erreur est survenue lors du calcul de la couverture hydraulique").serialize();
    }
  }

  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  @RequestMapping(value = "", method = RequestMethod.DELETE, headers = "Accept=application/json")
  public ResponseEntity<java.lang.String> delete(final @RequestBody String etude) {
      try {
          couvertureHydrauliqueRepository.deleteCouverture(Long.valueOf(etude));
          return new SuccessErrorExtSerializer(true, "Couverture hydraulique effacée").serialize();
      } catch (Exception e) {
          return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
      }
  }

  @PreAuthorize("hasRight('PLANIFIER_DECI')")
  @RequestMapping(value = "/closestPei", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
  @Transactional(noRollbackFor = Exception.class)
  public ResponseEntity<String> closestPei(MultipartHttpServletRequest request) {

    String json = request.getParameter("data");

    return new AbstractExtObjectSerializer<PlusProchePei>("Contacts retrieved.") {
      @Override
      protected PlusProchePei getRecord() {
        try {
          return couvertureHydrauliqueRepository.closestPei(json);
        } catch (CRSException e) {
          e.printStackTrace();
          return null;
        } catch (IllegalCoordinateException e) {
          e.printStackTrace();
          return null;
        }
      }
    }.serialize();
  }
}
