Ext.require('Sdis.Remocra.model.TypeRciPromCategorie');

Ext.define('Sdis.Remocra.store.TypeRciPromCategorie', {
    extend : 'Ext.data.Store',
    storeId : 'TypeRciPromCategorie',

    model : 'Sdis.Remocra.model.TypeRciPromCategorie',
    autoLoad : true
});
