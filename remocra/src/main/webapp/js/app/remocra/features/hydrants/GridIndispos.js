Ext.require('Ext.grid.Panel');
Ext.require('Sdis.Remocra.model.HydrantIndispoTemporaire');
Ext.define('Sdis.Remocra.features.hydrants.GridIndispos', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.gridIndispos',
    store: {model: 'Sdis.Remocra.model.HydrantIndispoTemporaire'},

    columns: [{
        dataIndex: 'description',
        flex: 1
    } ],
    selType: 'rowmodel'
});