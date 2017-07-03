Ext.require('Sdis.Remocra.model.OldebPropriete');

Ext.define('Sdis.Remocra.store.OldebPropriete', {
    extend: 'Ext.data.Store',
    alias: 'store.crOldebPropriete',
    storeId: 'OldebPropriete',
    model: 'Sdis.Remocra.model.OldebPropriete',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15
});
