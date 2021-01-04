package fr.sdis83.remocra.repository;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Result;
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
        Result<Record5<Integer, Boolean, Boolean, Long, String>> result = context.select(TRANSFERTS_AUTOMATISES.ID, TRANSFERTS_AUTOMATISES.TRANSMETTRE,
                        TRANSFERTS_AUTOMATISES.RECUPERER, TYPE_ORGANISME.ID.as("organisme_id"), TYPE_ORGANISME.NOM)
                .from(TRANSFERTS_AUTOMATISES)
                .join(TYPE_ORGANISME)
                .on(TRANSFERTS_AUTOMATISES.TYPE_ORGANISME.eq(TYPE_ORGANISME.ID))
                .orderBy(TYPE_ORGANISME.NOM)
                .fetch();

        List<Map<String,Object>> listeTransfertsAutomatises = new ArrayList<>();
        for(Record record : result){
            Map<String, Object> map = new HashMap<>();
            map.put("id",record.getValue("id"));
            map.put("transmettre",record.getValue("transmettre"));
            map.put("recuperer",record.getValue("recuperer"));
            map.put("nom",record.getValue("nom"));
            map.put("organisme_id",record.getValue("organisme_id"));

            listeTransfertsAutomatises.add(map);
        }
        return listeTransfertsAutomatises;
    }

    public void updateTransmettre(Long id, Boolean value){
        context.update(TRANSFERTS_AUTOMATISES)
                .set(TRANSFERTS_AUTOMATISES.TRANSMETTRE, value)
                .where(TRANSFERTS_AUTOMATISES.TYPE_ORGANISME.eq(id)).execute();
    }

    public void updateRecuperer(Long id, Boolean value){
        context.update(TRANSFERTS_AUTOMATISES)
                .set(TRANSFERTS_AUTOMATISES.RECUPERER, value)
                .where(TRANSFERTS_AUTOMATISES.TYPE_ORGANISME.eq(id)).execute();
    }
}
