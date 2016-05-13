Ext.require('Sdis.Remocra.model.Droit');

Ext.define('Sdis.Remocra.store.Droit', {
    extend: 'Ext.data.Store',
    alias: 'store.crDroit',
    
    storeId: 'Droit',
    model: 'Sdis.Remocra.model.Droit',
    remoteSort: true,
    remoteFilter: false
});
