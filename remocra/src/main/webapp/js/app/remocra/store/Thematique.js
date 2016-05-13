Ext.require('Sdis.Remocra.model.Thematique');

Ext.define('Sdis.Remocra.store.Thematique', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    alias: 'store.crThematique',
    
    storeId: 'Thematique',
    model: 'Sdis.Remocra.model.Thematique',
    url: 'thematiques'
});
