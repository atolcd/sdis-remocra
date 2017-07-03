Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.TypeOldebSuite');

Ext.define('Sdis.Remocra.store.TypeOldebSuite', {
    extend: 'Ext.data.Store',
    requires: ['Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model: 'Sdis.Remocra.model.TypeOldebSuite',
    alias: 'store.crTypeOldebSuite',
    storeId: 'TypeOldebSuite',
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
        url: Sdis.Remocra.util.Util.withBaseUrl('../typeoldebsuite'),
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});