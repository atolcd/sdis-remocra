Ext.require('Sdis.Remocra.model.TypeOldebAnomalie');

Ext.define('Sdis.Remocra.store.TypeOldebAnomalie', {
    extend: 'Ext.data.Store',
    storeId: 'TypeOldebAnomalie',
    model: 'Sdis.Remocra.model.TypeOldebAnomalie',
    autoLoad: true,
    groupField: 'categorie_nom',
    sorters: [{
        property: 'nom',
        direction: 'ASC'
    }]
});
