Ext.require('Sdis.Remocra.model.OldebVisite');

Ext.define('Sdis.Remocra.store.OldebVisite', {
    extend: 'Ext.data.Store',
    alias: 'store.crOldebVisite',
    storeId: 'OldebVisite',
    model: 'Sdis.Remocra.model.OldebVisite',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15
});
