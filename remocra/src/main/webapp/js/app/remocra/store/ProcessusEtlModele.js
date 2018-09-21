Ext.require('Sdis.Remocra.model.ProcessusEtlModele');
Ext.define('Sdis.Remocra.store.ProcessusEtlModele', {
    extend: 'Ext.data.Store',
    alias: 'store.crProcessusEtlModele',
    storeId: 'ProcessusEtlModele',
    model: 'Sdis.Remocra.model.ProcessusEtlModele',
    remoteFilter: true,
    pageSize: 15
});
