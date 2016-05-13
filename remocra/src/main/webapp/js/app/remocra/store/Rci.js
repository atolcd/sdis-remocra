Ext.require('Sdis.Remocra.model.Rci');

Ext.define('Sdis.Remocra.store.Rci', {
    extend: 'Ext.data.Store',
    storeId: 'Rci',

    model: 'Sdis.Remocra.model.Rci',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15
});
