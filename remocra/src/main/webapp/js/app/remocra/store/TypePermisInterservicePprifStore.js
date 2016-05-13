Ext.require('Sdis.Remocra.store.TypePermisInterserviceStore');
Ext.require('Sdis.Remocra.network.TypeReferenceStore');
Ext.require('Sdis.Remocra.model.TypePermisInterservice');

// Stores filtrés côté client
Ext.define('Sdis.Remocra.store.TypePermisInterservicePprifStore', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    model: 'Sdis.Remocra.model.TypePermisInterservice',
    singleton: true,
    storeId: 'TypePermisInterservicePprifStore',
    autoLoad: false
});
