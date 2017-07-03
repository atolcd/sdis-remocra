Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.CadastreParcelle');

Ext.define('Sdis.Remocra.store.CadastreParcelle', {
    extend: 'Ext.data.Store',
    requires: ['Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model: 'Sdis.Remocra.model.CadastreParcelle',
    alias: 'store.crCadastreParcelle',
    storeId: 'CadastreParcelle',
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
        url: Sdis.Remocra.util.Util.withBaseUrl('../cadastreparcelle'),
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});