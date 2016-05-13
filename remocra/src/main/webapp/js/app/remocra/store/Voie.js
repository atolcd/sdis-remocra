Ext.require('Sdis.Remocra.model.Voie');

Ext.define('Sdis.Remocra.store.Voie', {
    extend: 'Ext.data.Store',
    alias: 'store.crVoie',
    
    requires: ['Sdis.Remocra.util.Util','Sdis.Remocra.util.Msg'],
    model: 'Sdis.Remocra.model.Voie',
    storeId: 'Voie',
    pageSize: 10,
    remoteSort: true,
    remoteFilter: true,
    proxy: {
        format: 'json',
        type: 'rest',
        headers: {
            'Accept': 'application/json,application/xml',
            'Content-Type': 'application/json'
        },
        url: Sdis.Remocra.util.Util.withBaseUrl('../voies/mc'),
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});
