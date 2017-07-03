Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.TypeOldebResidence');

Ext.define('Sdis.Remocra.store.TypeOldebResidence', {
    extend: 'Ext.data.Store',
    requires: ['Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model: 'Sdis.Remocra.model.TypeOldebResidence',
    alias: 'store.crTypeOldebResidence',
    storeId: 'TypeOldebResidence',
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
        url: Sdis.Remocra.util.Util.withBaseUrl('../typeoldebresidence'),
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});