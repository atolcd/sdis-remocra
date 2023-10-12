Ext.require('Sdis.Remocra.model.RepertoireLieuData');
Ext.define('Sdis.Remocra.store.RepertoireLieuData', {
    extend: 'Ext.data.Store',
    alias: 'store.crRepertoireLieuData',
    storeId: 'RepertoireLieuData',
    model: 'Sdis.Remocra.model.RepertoireLieuData',
    pageSize: 15
});
