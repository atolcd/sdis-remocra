package fr.sdis83.remocra.web.serialize;

import flexjson.JSONSerializer;
import fr.sdis83.remocra.domain.remocra.Droit;
import fr.sdis83.remocra.web.serialize.transformer.DroitTransformer;

public class AccessRightSerializer extends JSONSerializer {

    public AccessRightSerializer() {
        include("permissions").exclude("*.class").exclude("authority").transform(new DroitTransformer(), Droit.class);
    }
}
