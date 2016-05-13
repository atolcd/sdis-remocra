Ext.require('Sdis.Remocra.model.ProfilDroit');

Ext.define('Sdis.Remocra.store.ProfilDroit', {
    extend: 'Ext.data.Store',
    alias: 'store.crProfilDroit',
    
    storeId: 'ProfilDroit',
    model: 'Sdis.Remocra.model.ProfilDroit',
    remoteSort: false,
    remoteFilter: false
});
