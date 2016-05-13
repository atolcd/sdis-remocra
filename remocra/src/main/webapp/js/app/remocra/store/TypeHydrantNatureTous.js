Ext.require('Sdis.Remocra.store.TypeHydrantNature');

Ext.define('Sdis.Remocra.store.TypeHydrantNatureTous', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    storeId: 'TypeHydrantNatureTous',

    model: 'Sdis.Remocra.model.TypeHydrantNature',
    url: 'typehydrantnatures',
    listeners: {
        load: function(store, records, successful, opt) {
            store.add(Ext.create('Sdis.Remocra.model.TypeHydrantNature', {id: null, nom: ' Tous'}));
            Ext.getStore('TypeHydrantNature').each(function(record) {
                store.add(record);
            });
        }
    }
});
