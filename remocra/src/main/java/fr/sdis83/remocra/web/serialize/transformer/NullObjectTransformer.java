package fr.sdis83.remocra.web.serialize.transformer;

import flexjson.transformer.ObjectTransformer;

/**
 * Permet de supprimer les objets null de la serialisation
 * 
 * @author bpa
 * 
 */
public class NullObjectTransformer extends ObjectTransformer {

    @Override
    public Boolean isInline() {
        return true;
    }

    @Override
    public void transform(Object object) {
        // Do nothing, null objects are not serialized.
        return;
    }
}
