Ext.require('Sdis.Remocra.model.HydrantRecord');

Ext.define('Sdis.Remocra.store.HydrantRecord', {
    extend: 'Ext.data.Store',
    storeId: 'HydrantRecord',

    model: 'Sdis.Remocra.model.HydrantRecord',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15
});
