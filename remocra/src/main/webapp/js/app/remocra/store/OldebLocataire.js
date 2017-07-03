Ext.require('Sdis.Remocra.model.OldebLocataire');

Ext.define('Sdis.Remocra.store.OldebLocataire', {
    extend: 'Ext.data.Store',
    alias: 'store.crOldebLocataire',
    storeId: 'OldebLocataire',
    model: 'Sdis.Remocra.model.OldebLocataire',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15
});
