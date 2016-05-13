Ext.require('Sdis.Remocra.model.Hydrant');

Ext.define('Sdis.Remocra.store.Hydrant', {
    extend: 'Ext.data.Store',
    storeId: 'Hydrant',

    model: 'Sdis.Remocra.model.Hydrant',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15
});
