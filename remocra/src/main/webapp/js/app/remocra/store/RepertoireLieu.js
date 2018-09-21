Ext.require('Sdis.Remocra.model.RepertoireLieu');
Ext.define('Sdis.Remocra.store.RepertoireLieu', {
    extend: 'Ext.data.Store',
    alias: 'store.crRepertoireLieu',
    storeId: 'RepertoireLieu',
    model: 'Sdis.Remocra.model.RepertoireLieu',
    pageSize: 15
});
