Ext.ns('Sdis.Remocra.network');

Ext.require('Sdis.Remocra.model.TypeOldebAvis');

Ext.define('Sdis.Remocra.store.TypeOldebAvis', {
    extend: 'Ext.data.Store',
    requires: ['Sdis.Remocra.util.Util', 'Sdis.Remocra.util.Msg' ],
    model: 'Sdis.Remocra.model.TypeOldebAvis',
    alias: 'store.crTypeOldebAvis',
    storeId: 'TypeOldebavis',
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
        url: Sdis.Remocra.util.Util.withBaseUrl('../typeoldebavis'),
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});