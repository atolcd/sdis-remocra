Ext.require('Sdis.Remocra.model.TypeRciOrigineAlerte');

Ext.define('Sdis.Remocra.store.TypeRciOrigineAlerte', {
    extend : 'Ext.data.Store',
    storeId : 'TypeRciOrigineAlerte',

    model : 'Sdis.Remocra.model.TypeRciOrigineAlerte',
    autoLoad : true
});
