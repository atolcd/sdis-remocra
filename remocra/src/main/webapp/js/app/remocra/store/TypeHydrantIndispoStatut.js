Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.TypeHydrantIndispoStatut');

Ext.define('Sdis.Remocra.store.TypeHydrantIndispoStatut', {
    extend: 'Ext.data.Store',
    requires: ['Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model: 'Sdis.Remocra.model.TypeHydrantIndispoStatut',
    alias: 'store.crTypeIndispoStatut',
    storeId: 'TypeIndispoStatut',
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
        url: Sdis.Remocra.util.Util.withBaseUrl('../typeindispostatut'),
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});