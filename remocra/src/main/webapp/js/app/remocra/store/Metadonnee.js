Ext.require('Sdis.Remocra.model.Metadonnee');

Ext.define('Sdis.Remocra.store.Metadonnee', {
    extend : 'Ext.data.Store',
    storeId : 'Metadonnee',

    model : 'Sdis.Remocra.model.Metadonnee',
    autoLoad : true,
    
    proxy : {
        format : 'json',
        type : 'rest',
        headers : {
            'Accept' : 'application/json,application/xml',
            'Content-Type' : 'application/json'
        },
        url : Sdis.Remocra.util.Util.withBaseUrl('../metadonnees'),
        reader : {
            type : 'json',
            root : 'data',
            totalProperty : 'total'
        },
        writer : 'deepjson'
    }
});
