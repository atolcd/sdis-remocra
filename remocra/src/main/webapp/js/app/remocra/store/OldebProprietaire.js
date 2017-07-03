Ext.require('Sdis.Remocra.model.OldebProprietaire');

Ext.define('Sdis.Remocra.store.OldebProprietaire', {
    extend: 'Ext.data.Store',
    alias: 'store.crOldebProprietaire',
    storeId: 'OldebProprietaire',
    model: 'Sdis.Remocra.model.OldebProprietaire',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15
});
