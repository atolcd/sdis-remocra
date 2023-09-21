package fr.sdis83.remocra.web;

import com.vividsolutions.jts.geom.Geometry;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import fr.sdis83.remocra.GlobalConstants;
import fr.sdis83.remocra.domain.remocra.HydrantPrescrit;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.exception.BusinessException;
import fr.sdis83.remocra.service.HydrantPrescritService;
import fr.sdis83.remocra.service.UtilisateurService;
import fr.sdis83.remocra.service.ZoneCompetenceService;
import fr.sdis83.remocra.util.FeatureUtil;
import fr.sdis83.remocra.web.deserialize.GeometryFactory;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/hydrantprescrits")
@Controller
public class HydrantPrescritController
    extends AbstractServiceableController<HydrantPrescritService, HydrantPrescrit> {

  @Autowired private HydrantPrescritService service;

  @Autowired private ZoneCompetenceService zoneCompetenceService;

  @Autowired private UtilisateurService serviceUtilisateur;

  @Override
  protected HydrantPrescritService getService() {
    return service;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_PRESCRIT_R')")
  public ResponseEntity<java.lang.String> read(
      final @RequestParam(value = "id", required = true) Long id) {
    return new AbstractExtObjectSerializer<HydrantPrescrit>(
        "fr.sdis83.remocra.domain.remocra.HydrantPrescrit retrieved.") {

      @Override
      protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
        return serializer.exclude("data.commune.geometrie");
      }

      @Override
      protected HydrantPrescrit getRecord() throws BusinessException {
        HydrantPrescrit current = service.getById(id);

        if (zoneCompetenceService.check(
            current.getGeometrie(), serviceUtilisateur.getCurrentZoneCompetenceId())) {
          return current;
        } else {
          throw new AccessDeniedException(
              "Vous n'avez pas les autorisations sur cette zone géographique");
        }
      }
    }.serialize();
  }

  @RequestMapping(value = "/layer", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_PRESCRIT_R')")
  public ResponseEntity<java.lang.String> layer(final @RequestParam String bbox) {
    if (bbox == null || bbox.isEmpty()) {
      return FeatureUtil.getResponse(service.findAllHydrantPrescrits());
    } else {
      return FeatureUtil.getResponse(service.findHydrantPrescritsByBBOX(bbox));
    }
  }

  @Override
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_PRESCRIT_C')")
  public ResponseEntity<java.lang.String> update(@PathVariable Long id, @RequestBody String json) {
    HydrantPrescrit current =
        new JSONDeserializer<HydrantPrescrit>()
            .use(null, HydrantPrescrit.class)
            .use(Date.class, RemocraDateHourTransformer.getInstance())
            .use(Geometry.class, new GeometryFactory())
            .deserialize(json);
    current.getGeometrie().setSRID(GlobalConstants.SRID_2154);
    if (zoneCompetenceService.check(
        current.getGeometrie(), serviceUtilisateur.getCurrentZoneCompetenceId())) {
      return this.doUpdate(id, json);
    } else {
      throw new AccessDeniedException(
          "Vous n'avez pas les autorisations sur cette zone géographique");
    }
  }

  @Override
  @RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_PRESCRIT_C')")
  public ResponseEntity<String> create(@RequestBody String json) {
    HydrantPrescrit current =
        new JSONDeserializer<HydrantPrescrit>()
            .use(null, HydrantPrescrit.class)
            .use(Date.class, RemocraDateHourTransformer.getInstance())
            .use(Geometry.class, new GeometryFactory())
            .deserialize(json);
    current.getGeometrie().setSRID(GlobalConstants.SRID_2154);
    if (zoneCompetenceService.check(
        current.getGeometrie(), serviceUtilisateur.getCurrentZoneCompetenceId())) {
      return this.doCreate(json);
    } else {
      throw new AccessDeniedException(
          "Vous n'avez pas les autorisations sur cette zone géographique");
    }
  }

  @Override
  @RequestMapping(
      value = "/{id}",
      method = RequestMethod.DELETE,
      headers = "Accept=application/json")
  @PreAuthorize("hasRight('HYDRANTS_PRESCRIT_C')")
  public ResponseEntity<String> delete(@PathVariable Long id) {
    HydrantPrescrit current = service.getById(id);

    if (zoneCompetenceService.check(
        current.getGeometrie(), serviceUtilisateur.getCurrentZoneCompetenceId())) {
      return this.doDelete(id);
    } else {
      throw new AccessDeniedException(
          "Vous n'avez pas les autorisations sur cette zone géographique");
    }
  }
}
