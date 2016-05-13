Ext.require('Sdis.Remocra.model.TypeHydrantMateriau');

Ext.define('Sdis.Remocra.store.TypeHydrantMateriau', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    storeId: 'TypeHydrantMateriau',
    
    model: 'Sdis.Remocra.model.TypeHydrantMateriau',
    url: 'typehydrantmateriaus'
});
