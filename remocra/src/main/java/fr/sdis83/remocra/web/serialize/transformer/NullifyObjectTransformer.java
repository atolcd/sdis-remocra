package fr.sdis83.remocra.web.serialize.transformer;

import flexjson.transformer.AbstractTransformer;

/**
 * Permet de rendre null les objets de la serialisation
 * 
 * @author bpa
 * 
 */
public class NullifyObjectTransformer extends AbstractTransformer {

    @Override
    public void transform(Object object) {
        getContext().write("null");
    }
}
