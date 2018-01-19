Ext.require('Ext.grid.Panel');
Ext.require('Sdis.Remocra.model.Hydrant');
Ext.define('Sdis.Remocra.features.hydrants.GridHydrantIndispos', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.gridHydrantIndispos',
    store: {model: 'Sdis.Remocra.model.Hydrant'},

    columns: [{
        dataIndex: 'numero',
        flex: 1
    } ],
    selType: 'rowmodel'
});