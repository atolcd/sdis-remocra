Ext.require('Sdis.Remocra.model.TypeHydrantMarque');

Ext.define('Sdis.Remocra.store.TypeHydrantMarque', {
    extend: 'Ext.data.Store',
    storeId: 'TypeHydrantMarque',


    associations: [{
        type: 'hasMany',
        model: 'Sdis.Remocra.model.TypeHydrantModele',
        associationKey: 'modeles',
        name: 'modeles',
        associatedName: 'modeles',
        persist: true
    }],
    
    model: 'Sdis.Remocra.model.TypeHydrantMarque',
    autoLoad : true
});
