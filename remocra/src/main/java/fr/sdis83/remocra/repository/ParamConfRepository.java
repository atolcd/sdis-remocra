package fr.sdis83.remocra.repository;

import fr.sdis83.remocra.db.model.remocra.tables.pojos.ParamConf;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static fr.sdis83.remocra.db.model.remocra.Tables.PARAM_CONF;

@Configuration
public class ParamConfRepository {
    @Autowired
    DSLContext context;

    public ParamConfRepository() {
    }

    @Bean
    public ParamConfRepository paramConfRepository(DSLContext context) {
        return new ParamConfRepository(context);
    }

    // Clés possibles
    public final static String DOSSIER_ROOT = "1_DOSSIER_ROOT";
    public final static String DOSSIER_INTEGRATION = "2_DOSSIER_INTEGRATION";
    public final static String DOSSIER_SAUVEGARDE_DOCUMENTS = "3_DOSSIER_SAUVEGARDE_DOCUMENTS";


    ParamConfRepository(DSLContext context) {
        this.context = context;
    }

    public ParamConf getByCle(String cle) {
       return context.selectFrom(PARAM_CONF)
                .where(PARAM_CONF.CLE.eq(cle))
                .fetchOneInto(ParamConf.class);
    }

}