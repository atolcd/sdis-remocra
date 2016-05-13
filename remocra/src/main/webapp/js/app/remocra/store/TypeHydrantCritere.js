Ext.require('Sdis.Remocra.model.TypeHydrantCritere');

Ext.define('Sdis.Remocra.store.TypeHydrantCritere', {
    extend: 'Ext.data.Store',
    storeId: 'TypeHydrantCritere',

    model: 'Sdis.Remocra.model.TypeHydrantCritere',
    autoLoad : true
});
