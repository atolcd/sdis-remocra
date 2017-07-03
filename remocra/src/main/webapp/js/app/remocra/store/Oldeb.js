Ext.require('Sdis.Remocra.model.Oldeb');

Ext.define('Sdis.Remocra.store.Oldeb', {
    extend: 'Ext.data.Store',
    alias: 'store.crOldeb',
    storeId: 'Oldeb',
    model: 'Sdis.Remocra.model.Oldeb',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15
});
