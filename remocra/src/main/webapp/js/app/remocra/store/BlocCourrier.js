Ext.require('Sdis.Remocra.model.BlocCourrier');

Ext.define('Sdis.Remocra.store.BlocCourrier', {
    extend: 'Ext.data.Store',
    alias: 'store.crBlocCourrier',

    storeId: 'BlocCourrier',

    model: 'Sdis.Remocra.model.BlocCourrier',
    remoteSort: true,
    remoteFilter: true,
    pageSize: 10
});
