Ext.require('Sdis.Remocra.model.HydrantIndispoTemporaire');

Ext.define('Sdis.Remocra.store.HydrantIndispoTemporaire', {
    extend: 'Ext.data.Store',
    storeId: 'indispo',
     alias: 'store.crIndispos',

    model: 'Sdis.Remocra.model.HydrantIndispoTemporaire',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 15,
    filters: [{"property":"statut","value":-3}]
});
