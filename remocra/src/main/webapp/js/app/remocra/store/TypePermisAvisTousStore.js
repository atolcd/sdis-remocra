Ext.require('Sdis.Remocra.store.TypePermisAvisStore');
Ext.require('Sdis.Remocra.network.TypeReferenceStore');
Ext.require('Sdis.Remocra.model.TypePermisAvis');

// Stores filtrés côté client
Ext.define('Sdis.Remocra.store.TypePermisAvisTousStore', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    model: 'Sdis.Remocra.model.TypePermisAvis',
    singleton: true,
    storeId: 'TypePermisAvisTousStore',
    autoLoad: false
});
