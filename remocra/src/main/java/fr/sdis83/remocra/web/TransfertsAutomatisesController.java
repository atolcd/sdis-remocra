package fr.sdis83.remocra.web;

import flexjson.JSONDeserializer;
import fr.sdis83.remocra.db.model.remocra.tables.pojos.TransfertsAutomatises;
import fr.sdis83.remocra.repository.TransfertsAutomatisesRepository;
import fr.sdis83.remocra.web.serialize.ext.AbstractExtObjectSerializer;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/transfertsautomatises")
@Controller
public class TransfertsAutomatisesController {

    @Autowired
    TransfertsAutomatisesRepository transfertsAutomatisesRepository;

    @RequestMapping(value = "", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<String> getTypesOrganismeAcces() {
        return new AbstractExtObjectSerializer<List<Map<String,Object>>>("fr.sdis83.remocra.domain.remocra.TransfertsAutomatisesController retrieved.") {

            @Override
            protected List<Map<String,Object>> getRecord() {
                return transfertsAutomatisesRepository.getTypesOrganismeAcces();
            }

        }.serialize();
    }

    @RequestMapping(value = "/updateadmin/{id}", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> updateAdministrer(@PathVariable Long id, @RequestBody String json) {
        try{
            HashMap<String, Object> param = new JSONDeserializer<HashMap<String, Object>>().deserialize(json);
            transfertsAutomatisesRepository.updateAdministrer(id, (Boolean)param.get("value"));
            return new SuccessErrorExtSerializer(true, "Accès modifiés avec succès").serialize();
        } catch (Exception e){
            e.printStackTrace();
            return new SuccessErrorExtSerializer(false, "Erreur dans la modification des accès").serialize();
        }

    }

    @RequestMapping(value = "/updatepost/{id}", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> updateTransmettre(@PathVariable Long id, @RequestBody String json) {
        try{
            HashMap<String, Object> param = new JSONDeserializer<HashMap<String, Object>>().deserialize(json);
            transfertsAutomatisesRepository.updateTransmettre(id, (Boolean)param.get("value"));
            return new SuccessErrorExtSerializer(true, "Accès modifiés avec succès").serialize();
        } catch (Exception e){
            e.printStackTrace();
            return new SuccessErrorExtSerializer(false, "Erreur dans la modification des accès").serialize();
        }

    }

    @RequestMapping(value = "/updateget/{id}", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> updateRecuperer(@PathVariable Long id, final @RequestBody String json) {
        try{
            HashMap<String, Object> param = new JSONDeserializer<HashMap<String, Object>>().deserialize(json);
            transfertsAutomatisesRepository.updateRecuperer(id, (Boolean)param.get("value"));
            return new SuccessErrorExtSerializer(true, "Accès modifiés avec succès").serialize();
        } catch (Exception e){
            e.printStackTrace();
            return new SuccessErrorExtSerializer(false, "Erreur dans la modification des accès").serialize();
        }

    }
}
