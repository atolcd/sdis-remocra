Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.TypeOldebZoneUrbanisme');

Ext.define('Sdis.Remocra.store.TypeOldebZoneUrbanisme', {
    extend: 'Ext.data.Store',
    requires: ['Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model: 'Sdis.Remocra.model.TypeOldebZoneUrbanisme',
    alias: 'store.crTypeOldebZoneUrbanisme',
    storeId: 'TypeOldebZoneUrbanisme',
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
        url: Sdis.Remocra.util.Util.withBaseUrl('../typeoldebzoneurbanisme'),
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});