Ext.require('Sdis.Remocra.model.TypeRciRisqueMeteo');

Ext.define('Sdis.Remocra.store.TypeRciRisqueMeteo', {
    extend : 'Ext.data.Store',
    storeId : 'TypeRciRisqueMeteo',

    model : 'Sdis.Remocra.model.TypeRciRisqueMeteo',
    autoLoad : true
});
