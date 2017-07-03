Ext.require('Sdis.Remocra.model.OldebVisiteDocument');

Ext.define('Sdis.Remocra.store.OldebVisiteDocument', {
    extend: 'Ext.data.Store',
    alias: 'store.crOldebVisiteDocument',
    storeId: 'OldebVisiteDocument',
    model: 'Sdis.Remocra.model.OldebVisiteDocument',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 10
});
