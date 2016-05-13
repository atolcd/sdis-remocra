Ext.require('Sdis.Remocra.model.Tournee');

Ext.define('Sdis.Remocra.store.Tournee', {
    extend: 'Ext.data.Store',
    storeId: 'Tournee',

    model: 'Sdis.Remocra.model.Tournee',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 20
});
