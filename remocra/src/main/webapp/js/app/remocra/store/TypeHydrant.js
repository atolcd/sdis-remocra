Ext.require('Sdis.Remocra.model.TypeHydrant');

Ext.define('Sdis.Remocra.store.TypeHydrant', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    storeId: 'TypeHydrant',
    
    model: 'Sdis.Remocra.model.TypeHydrant',
    url: 'typehydrants'
});
