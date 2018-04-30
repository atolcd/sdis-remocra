Ext.require('Sdis.Remocra.model.RequeteModele');

Ext.define('Sdis.Remocra.store.RequeteModele', {
    extend: 'Ext.data.Store',
    alias: 'store.crRequeteModele',
    storeId: 'RequeteModele',
    model: 'Sdis.Remocra.model.RequeteModele',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15
});
