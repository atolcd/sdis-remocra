Ext.require('Sdis.Remocra.model.BlocDocument');

Ext.define('Sdis.Remocra.store.BlocDocument', {
    extend: 'Ext.data.Store',
    alias: 'store.crBlocDocument',
    
    storeId: 'BlocDocument',

    model: 'Sdis.Remocra.model.BlocDocument',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 10
});
