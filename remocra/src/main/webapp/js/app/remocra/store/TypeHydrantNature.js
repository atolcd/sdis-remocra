Ext.require('Sdis.Remocra.model.TypeHydrantNature');

Ext.define('Sdis.Remocra.store.TypeHydrantNature', {
    extend: 'Sdis.Remocra.network.TypeReferenceStore',
    storeId: 'TypeHydrantNature',

    model: 'Sdis.Remocra.model.TypeHydrantNature',
    url: 'typehydrantnatures',
    listeners: {
        load: function(store, records, successful, opt) {

            Sdis.Remocra.util.Util.copyStore(store, 'TypeHydrantNaturePibi', function(record, index) {
                return record.get('typeHydrantId') == 1; // 1 = PIBI
            });

            Sdis.Remocra.util.Util.copyStore(store, 'TypeHydrantNaturePena', function(record, index) {
                return record.get('typeHydrantId') == 2; // 2 = PENA
            });
        }
    }
});
