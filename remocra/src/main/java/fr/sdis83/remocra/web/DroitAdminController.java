package fr.sdis83.remocra.web;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Droit;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
import fr.sdis83.remocra.web.serialize.ext.SuccessErrorExtSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RequestMapping("/droitsadmin")
@Controller
public class DroitAdminController {

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    public ResponseEntity<java.lang.String> read() {
        final Map<Long, List<Long>> results = new HashMap<Long, List<Long>>();
        List<Droit> droits = Droit.findAllDroits("id", "ASC");
        for (Droit droit : droits) {
            Long typeDroitId = droit.getTypeDroit().getId();
            Long profilDroitId = droit.getProfilDroit().getId();
            List<Long> pds = results.get(typeDroitId);
            if (pds == null) {
                pds = new LinkedList<Long>();
                results.put(typeDroitId, pds);
            }
            pds.add(profilDroitId);
        }
        return new AbstractExtObjectSerializer<Map<Long, List<Long>>>("Droits retrieved.") {
            @Override
            protected JSONSerializer additionnalIncludeExclude(JSONSerializer serializer) {
                serializer
                        .include("data.*");
                return serializer.include("total").include("message");
            }

            @Override
            protected Map<Long, List<Long>> getRecord() {
                return results;
            }

        }.serialize();
    }

    @RequestMapping(value = "/{idtypedroit}/{idprofil}/{active}", method = RequestMethod.PUT, headers = "Accept=application/json")
    @PreAuthorize("hasRight('REFERENTIELS_C')")
    @Transactional
    public ResponseEntity<java.lang.String> update(final @PathVariable(value = "idtypedroit") Long idtypedroit,
                                                final @PathVariable(value = "idprofil") Long idprofil,
                                                final @PathVariable(value = "active") Boolean active) {
        String strQuery = active ?
                "INSERT INTO remocra.droit(type_droit, profil_droit) SELECT :idtypedroit, :idprofil EXCEPT SELECT type_droit, profil_droit FROM remocra.droit"
                : "DELETE FROM remocra.droit WHERE type_droit = :idtypedroit AND profil_droit = :idprofil";
        Query query = entityManager
                .createNativeQuery(strQuery)
                .setParameter("idtypedroit", idtypedroit)
                .setParameter("idprofil", idprofil);
        int nbOp = query.executeUpdate();
        return new SuccessErrorExtSerializer(true, nbOp + " droits modified").serialize();
    }
}
