Ext.require('Sdis.Remocra.model.TypeHydrantNatureDeci');

Ext.define('Sdis.Remocra.store.TypeHydrantNatureDeci', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    storeId: 'TypeHydrantNatureDeci',

    model: 'Sdis.Remocra.model.TypeHydrantNatureDeci',
    url: 'typehydrantnaturedeci'
});
