package fr.sdis83.remocra.repository;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.sdis83.remocra.db.model.remocra.Tables.TYPE_ORGANISME;
import static fr.sdis83.remocra.db.model.remocra.Tables.TRANSFERTS_AUTOMATISES;

@Configuration
public class TransfertsAutomatisesRepository {

    @Autowired
    DSLContext context;

    public TransfertsAutomatisesRepository() {
    }


    @Bean
    public TransfertsAutomatisesRepository transfertsAutomatisesRepository(DSLContext context) {
        return new TransfertsAutomatisesRepository(context);
    }

    TransfertsAutomatisesRepository(DSLContext context) {
        this.context = context;
    }

    public List<Map<String,Object>> getTypesOrganismeAcces(){
        List<TypeOrganismeAcces> listeTypeOrganismeAcces =
                context.select( TRANSFERTS_AUTOMATISES.ID,
                                TRANSFERTS_AUTOMATISES.ADMINISTRER,
                                TRANSFERTS_AUTOMATISES.TRANSMETTRE,
                                TRANSFERTS_AUTOMATISES.RECUPERER,
                                TYPE_ORGANISME.ID.as("organismeId"),
                                TYPE_ORGANISME.NOM.as("organismeNom"))
                .from(TRANSFERTS_AUTOMATISES)
                .join(TYPE_ORGANISME)
                .on(TRANSFERTS_AUTOMATISES.TYPE_ORGANISME.eq(TYPE_ORGANISME.ID))
                .orderBy(TYPE_ORGANISME.NOM)
                .fetchInto(TypeOrganismeAcces.class);

        List<Map<String,Object>> listeTransfertsAutomatises = new ArrayList<>();
        for(TypeOrganismeAcces typeOrganismeAcces : listeTypeOrganismeAcces){
            Map<String, Object> map = new HashMap<>();
            map.put("id",typeOrganismeAcces.getId());
            map.put("administrer",typeOrganismeAcces.getAdministrer());
            map.put("transmettre",typeOrganismeAcces.getTransmettre());
            map.put("recuperer",typeOrganismeAcces.getRecuperer());
            map.put("nom",typeOrganismeAcces.getOrganismeNom());
            map.put("organisme_id",typeOrganismeAcces.getOrganismeId());
            listeTransfertsAutomatises.add(map);
        }
        return listeTransfertsAutomatises;
    }

    public void updateAdministrer(Long idTypeOrganisme, Boolean value) {
        context.update(TRANSFERTS_AUTOMATISES)
                .set(TRANSFERTS_AUTOMATISES.ADMINISTRER, value)
                .where(TRANSFERTS_AUTOMATISES.TYPE_ORGANISME.eq(idTypeOrganisme)).execute();
    }

    public void updateTransmettre(Long idTypeOrganisme, Boolean value) {
        context.update(TRANSFERTS_AUTOMATISES)
                .set(TRANSFERTS_AUTOMATISES.TRANSMETTRE, value)
                .where(TRANSFERTS_AUTOMATISES.TYPE_ORGANISME.eq(idTypeOrganisme)).execute();
    }

    public void updateRecuperer(Long idTypeOrganisme, Boolean value) {
        context.update(TRANSFERTS_AUTOMATISES)
                .set(TRANSFERTS_AUTOMATISES.RECUPERER, value)
                .where(TRANSFERTS_AUTOMATISES.TYPE_ORGANISME.eq(idTypeOrganisme)).execute();
    }
}
