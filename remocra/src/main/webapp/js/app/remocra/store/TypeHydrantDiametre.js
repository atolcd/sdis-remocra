Ext.require('Sdis.Remocra.model.TypeHydrantDiametre');

Ext.define('Sdis.Remocra.store.TypeHydrantDiametre', {
    extend: 'Ext.data.Store',
    storeId: 'TypeHydrantDiametre',

    model: 'Sdis.Remocra.model.TypeHydrantDiametre',
    autoLoad : true
});
