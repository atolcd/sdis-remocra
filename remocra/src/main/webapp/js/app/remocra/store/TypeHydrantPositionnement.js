Ext.require('Sdis.Remocra.model.TypeHydrantPositionnement');

Ext.define('Sdis.Remocra.store.TypeHydrantPositionnement', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    storeId: 'TypeHydrantPositionnement',
    
    model: 'Sdis.Remocra.model.TypeHydrantPositionnement',
    url: 'typehydrantpositionnements'
});
