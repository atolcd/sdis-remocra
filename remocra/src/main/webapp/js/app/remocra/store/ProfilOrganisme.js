Ext.require('Sdis.Remocra.network.TypeReferenceStore');
Ext.require('Sdis.Remocra.model.ProfilOrganisme');

Ext.define('Sdis.Remocra.store.ProfilOrganisme', {
    extend: 'Ext.data.Store',
    alias: 'store.crProfilOrganisme',
    
    storeId: 'ProfilOrganisme',
    autoLoad: false,
    
    model: 'Sdis.Remocra.model.ProfilOrganisme'
});
