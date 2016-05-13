Ext.require('Sdis.Remocra.model.ImageStyle');

Ext.define('Sdis.Remocra.store.ImageStyle', {
    extend : 'Ext.data.Store',
    storeId : 'ImageStyle',
    singleton : true,

    model : 'Sdis.Remocra.model.ImageStyle',
    autoLoad : true
});
