Ext.require('Sdis.Remocra.network.TypeReferenceStore');
Ext.require('Sdis.Remocra.model.TypePermisAvis');

Ext.define('Sdis.Remocra.store.TypeOrganisme', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    alias: 'store.crTypeOrganisme',
    storeId: 'TypeOrganisme',
    
    autoLoad: false,
    
    url: 'typeorganisme'
});
