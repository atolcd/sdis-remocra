Ext.require('Sdis.Remocra.model.Utilisateur');

Ext.define('Sdis.Remocra.store.Organisme', {
    extend: 'Ext.data.Store',
    storeId: 'Organisme',

    model: 'Sdis.Remocra.model.Organisme',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 20,
    
    autoLoad : false,
    autoSync: false
});
