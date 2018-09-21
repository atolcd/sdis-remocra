Ext.require('Sdis.Remocra.model.ProcessusEtlPlanification');
Ext.define('Sdis.Remocra.store.ProcessusEtlPlanification', {
    extend: 'Ext.data.Store',
    alias: 'store.crProcessusEtlPlanification',
    storeId: 'ProcessusEtlPlanification',
    model: 'Sdis.Remocra.model.ProcessusEtlPlanification',
    remoteFilter: true,
    pageSize: 15
});
