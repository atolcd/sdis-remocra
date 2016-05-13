package fr.sdis83.remocra.web.serialize.transformer;

import flexjson.transformer.AbstractTransformer;
import fr.sdis83.remocra.domain.remocra.AlerteDocument;
import fr.sdis83.remocra.domain.remocra.Document;

public class AlerteDocumentTransformer extends AbstractTransformer {

    static AlerteDocumentTransformer instance;

    public static AlerteDocumentTransformer getInstance() {
        if (instance == null) {
            instance = new AlerteDocumentTransformer();
        }
        return instance;
    }
    
    protected AlerteDocumentTransformer() {
        
    }

    @Override
    public void transform(Object object) {
        if (object instanceof AlerteDocument) {
            Document ad = ((AlerteDocument) object).getDocument();
            getContext().write("{\"nom\":");
            getContext().writeQuoted(ad.getFichier());
            getContext().write(",\"code\":");
            getContext().writeQuoted(ad.getCode());
            getContext().write("}");
        }
    }
}
