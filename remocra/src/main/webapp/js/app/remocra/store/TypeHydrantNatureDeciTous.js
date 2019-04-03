Ext.require('Sdis.Remocra.store.TypeHydrantNatureDeci');

Ext.define('Sdis.Remocra.store.TypeHydrantNatureDeciTous', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    storeId: 'TypeHydrantNatureDeciTous',

    model: 'Sdis.Remocra.model.TypeHydrantNatureDeci',
    url: 'typehydrantnaturedeci',
    listeners: {
        load: function(store, records, successful, opt) {
            store.add(Ext.create('Sdis.Remocra.model.TypeHydrantNatureDeci', {id: null, nom: ' Tous'}));
            Ext.getStore('TypeHydrantNatureDeci').each(function(record) {
                store.add(record);
            });
        }
    }
});
