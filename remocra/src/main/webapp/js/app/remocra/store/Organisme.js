Ext.require('Sdis.Remocra.model.Utilisateur');

Ext.define('Sdis.Remocra.store.Organisme', {
    extend: 'Ext.data.Store',
    storeId: 'Organisme',
    alias: 'store.Organisme',

    model: 'Sdis.Remocra.model.Organisme',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 20,
    
    autoLoad : true,
    autoSync: true
});
