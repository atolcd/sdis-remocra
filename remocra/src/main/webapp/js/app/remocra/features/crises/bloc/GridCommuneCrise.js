Ext.require('Ext.grid.Panel');
Ext.require('Sdis.Remocra.model.Commune');
Ext.define('Sdis.Remocra.features.crises.bloc.GridCommuneCrise', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.gridCommuneCrise',
    store: {model: 'Sdis.Remocra.model.Commune'},

    columns: [{
        dataIndex: 'nom',
        flex: 1
    } ],
    selType: 'rowmodel'
});