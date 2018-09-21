Ext.require('Sdis.Remocra.model.OgcService');
Ext.define('Sdis.Remocra.store.OgcService', {
    extend: 'Ext.data.Store',
    alias: 'store.crOgcService',
    storeId: 'OgcService',
    model: 'Sdis.Remocra.model.OgcService',
    pageSize: 15
});
