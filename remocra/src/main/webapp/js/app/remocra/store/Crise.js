Ext.require('Sdis.Remocra.model.Crise');

Ext.define('Sdis.Remocra.store.Crise', {
    extend: 'Ext.data.Store',
    alias: 'store.crCrise',
    storeId: 'Crise',
    model: 'Sdis.Remocra.model.Crise',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15
});
