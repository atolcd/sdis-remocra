Ext.require('Sdis.Remocra.model.OldebVisiteSuite');

Ext.define('Sdis.Remocra.store.OldebVisiteSuite', {
    extend: 'Ext.data.Store',
    alias: 'store.crOldebVisiteSuite',
    storeId: 'OldebVisiteSuite',
    model: 'Sdis.Remocra.model.OldebVisiteSuite',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15
});
