Ext.require('Sdis.Remocra.model.TypeHydrantSaisie');

Ext.define('Sdis.Remocra.store.TypeHydrantSaisie', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    storeId: 'TypeHydrantSaisie',
    
    model: 'Sdis.Remocra.model.TypeHydrantSaisie',
    url: 'typehydrantsaisies'
});
