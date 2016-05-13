Ext.require('Sdis.Remocra.model.TypeDroit');

Ext.define('Sdis.Remocra.store.TypeDroit', {
    extend: 'Ext.data.Store',
    alias: 'store.crTypeDroit',
    
    storeId: 'TypeDroit',
    model: 'Sdis.Remocra.model.TypeDroit',
    remoteSort: false,
    remoteFilter: false
});
