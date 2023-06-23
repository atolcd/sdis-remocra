Ext.require('Sdis.Remocra.model.TypeHydrantAnomalie');

Ext.define('Sdis.Remocra.store.TypeHydrantAnomalie', {
    extend: 'Ext.data.Store',
    storeId: 'TypeHydrantAnomalie',

    model: 'Sdis.Remocra.model.TypeHydrantAnomalie',
    autoLoad: true,
    groupField: 'critere_code',
    sorters: [{
        property: 'code',
        direction: 'ASC'
    }]
});
