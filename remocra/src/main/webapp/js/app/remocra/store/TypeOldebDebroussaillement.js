Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.TypeOldebDebroussaillement');

Ext.define('Sdis.Remocra.store.TypeOldebDebroussaillement', {
    extend: 'Ext.data.Store',
    requires: ['Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model: 'Sdis.Remocra.model.TypeOldebDebroussaillement',
    alias: 'store.crTypeOldebDebroussaillement',
    storeId: 'TypeOldebDebroussaillement',
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
        url: Sdis.Remocra.util.Util.withBaseUrl('../typeoldebdebroussaillement'),
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});