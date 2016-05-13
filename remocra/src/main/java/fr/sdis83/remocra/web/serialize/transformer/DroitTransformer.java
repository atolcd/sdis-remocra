package fr.sdis83.remocra.web.serialize.transformer;

import flexjson.transformer.AbstractTransformer;
import fr.sdis83.remocra.domain.remocra.Droit;

public class DroitTransformer extends AbstractTransformer {

    @Override
    public void transform(Object object) {
        if (object instanceof Droit) {
        	Droit droit = (Droit) object;
            getContext().writeQuoted(droit.toString());
        }
    }
}
