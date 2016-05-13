Ext.require('Sdis.Remocra.network.TypeReferenceStore');
Ext.require('Sdis.Remocra.model.ProfilUtilisateur');

Ext.define('Sdis.Remocra.store.ProfilUtilisateur', {
    extend: 'Ext.data.Store',
    alias: 'store.crProfilUtilisateur',
    
    storeId: 'ProfilUtilisateur',
    autoLoad: false,
    
    model: 'Sdis.Remocra.model.ProfilUtilisateur'
});
