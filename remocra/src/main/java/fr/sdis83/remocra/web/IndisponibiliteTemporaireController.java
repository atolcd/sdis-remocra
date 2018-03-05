package fr.sdis83.remocra.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Hydrant;
import fr.sdis83.remocra.domain.remocra.HydrantIndispoTemporaire;
import fr.sdis83.remocra.domain.remocra.TypeHydrantIndispoStatut;
import fr.sdis83.remocra.domain.utils.RemocraDateHourTransformer;
import fr.sdis83.remocra.service.IndisponibiliteTemporaireService;
import fr.sdis83.remocra.web.message.ItemFilter;
import fr.sdis83.remocra.web.message.ItemSorting;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtListSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
@RequestMapping("/indisponibilites")
@Controller
public class IndisponibiliteTemporaireController  {

    @Autowired
    private IndisponibiliteTemporaireService indisponibiliteTemporaireService;

    @RequestMapping(value = "", headers = "Accept=application/json")
    @PreAuthorize("hasRight('INDISPOS', 'READ')")
    public ResponseEntity<String> listJson(final @RequestParam(value = "page", required = false) Integer page,
            final @RequestParam(value = "start", required = false) Integer start, final @RequestParam(value = "limit", required = false) Integer limit,
            final @RequestParam(value = "sort", required = false) String sorts, final @RequestParam(value = "filter", required = false) String filters,
            final @RequestParam(value = "query", required = false) String query) {

        final List<ItemSorting> sortList = ItemSorting.decodeJson(sorts);
        final List<ItemFilter> itemFilterList = ItemFilter.decodeJson(filters);

        if (query != null && !query.isEmpty()) {
            itemFilterList.add(new ItemFilter("hydrantId", query));
        }
        if (sortList.isEmpty()) {
            sortList.add(new ItemSorting("dateDebut", "ASC"));
            sortList.add(new ItemSorting("datePrevDebut", "ASC"));
        }

        return new AbstractExtListSerializer<HydrantIndispoTemporaire>("fr.sdis83.remocra.domain.remocra.HydrantIndispoTemporaire retrieved.") {
            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                serializer
                    .include("data.nomStatut").include("data.id").include("data.datePrevDebut").include("data.dateRappelDebut").include("data.dateRappelFin")
                    .include("data.datePrevFin").include("data.dateDebut").include("data.dateFin").include("data.motif")
                    .include("data.totalHydrants").include("data.statut").include("data.countHydrant").include("data.hydrants.id").include("data.hydrants.numero").include("data.geometrie").include("data.hydrants.jsonGeometrie");

                return serializer.include("total").include("message").exclude("data.hydrants.*");
            }
            @Override
            protected List<HydrantIndispoTemporaire> getRecords() {
                if(itemFilterList.size()!=0 && itemFilterList.get(0).getFieldName().equals("hydrantId")) {
                    return indisponibiliteTemporaireService.getIndisponibilite(Long.valueOf(itemFilterList.get(0).getValue()));
                }
                return indisponibiliteTemporaireService.find(start, limit, sortList, itemFilterList);
            }

            @Override
            protected Long countRecords() {
                return indisponibiliteTemporaireService.count(itemFilterList);
            }

        }.serialize();
    }

    @RequestMapping(value = "/indispoTemp", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('INDISPOS', 'CREATE')")
    public ResponseEntity<java.lang.String> setIndispo(final @RequestBody String json) {
        Integer nbHydrantIndispo = indisponibiliteTemporaireService.setIndispo(json);

        return new SuccessErrorExtSerializer(true, nbHydrantIndispo.toString() + " Point(s) d'eau sont déclarés indisponible(s)").serialize();

    }

    @RequestMapping(value = "/activeIndispoTemp/{id}", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('INDISPOS', 'UPDATE') ")
    public ResponseEntity<java.lang.String> activeIndispoTemp(@PathVariable("id") Long id, final @RequestParam String dateDebut) {
        try {

            DateFormat df = new SimpleDateFormat(RemocraDateHourTransformer.FORMAT);
            HydrantIndispoTemporaire attached = HydrantIndispoTemporaire.findHydrantIndispoTemporaire(id);
            //voir si on supprime les dates previsionnelle ou pas
           // attached.setDatePrevDebut(null);
         //   attached.setDateDebut(df.parse(String.valueOf(dateDebut)));
            Date date = df.parse(dateDebut);
            attached.setDateDebut(date);
            TypeHydrantIndispoStatut indispoStatut = TypeHydrantIndispoStatut.findTypeHydrantIndispoStatutByCode("EN_COURS").getSingleResult();
            attached.setStatut(indispoStatut);
            attached.merge();
            attached.flush();
            //On rajoute l'indisponibilite temporaire dans les anomalies des hydrants concernés
            Iterator<Hydrant> hydrants = attached.getHydrants().iterator();
            ArrayList<Long> ids = new ArrayList<Long>();
            while (hydrants.hasNext()) {
                ids.add(hydrants.next().getId());
            }
            //on met à jour l'utilisateur
            indisponibiliteTemporaireService.updateUserModification(ids);
            //on rajoute l'anomalie
            indisponibiliteTemporaireService.setHydrantAnomalie(ids);
            return new SuccessErrorExtSerializer(true, "Indisponibilité temporaire activée").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/leveIndispoTemp/{id}", method = RequestMethod.POST, headers = "Accept=application/json")
    @PreAuthorize("hasRight('INDISPOS', 'UPDATE') ")
    public ResponseEntity<java.lang.String> leveIndispoTemp(@PathVariable("id") Long id, final @RequestParam String dateFin) {
        try {
            DateFormat df = new SimpleDateFormat(RemocraDateHourTransformer.FORMAT);
            HydrantIndispoTemporaire attached = HydrantIndispoTemporaire.findHydrantIndispoTemporaire(id);
            Date date = df.parse(dateFin);
            attached.setDateFin(date);
            TypeHydrantIndispoStatut indispoStatut = TypeHydrantIndispoStatut.findTypeHydrantIndispoStatutByCode("TERMINE").getSingleResult();
            attached.setStatut(indispoStatut);
            attached.merge();
            attached.flush();
            //On rajoute l'indisponibilite temporaire dans les anomalies des hydrants concernés
            Iterator<Hydrant> hydrants = attached.getHydrants().iterator();
            ArrayList<Long> ids = new ArrayList<Long>();
            while (hydrants.hasNext()) {
                ids.add(hydrants.next().getId());
            }
            //on met à jour l'utilisateur'
            indisponibiliteTemporaireService.updateUserModification(ids);
            //on supprime l'anomalie
            indisponibiliteTemporaireService.deleteHydrantAnomalie(ids);
            return new SuccessErrorExtSerializer(true, "Indisponibilité temporaire levée").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @PreAuthorize("hasRight('INDISPOS', 'DELETE')")
    public ResponseEntity<java.lang.String> delete(@PathVariable("id") Long id) {
        try {
            indisponibiliteTemporaireService.delete(id);
            return new SuccessErrorExtSerializer(true, "Indisponibilité temporaire supprimée").serialize();
        } catch (Exception e) {
            return new SuccessErrorExtSerializer(false, e.getMessage()).serialize();
        }
    }


}
